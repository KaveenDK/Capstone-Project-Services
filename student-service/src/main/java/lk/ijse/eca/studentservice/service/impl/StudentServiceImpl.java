package lk.ijse.eca.studentservice.service.impl;

import lk.ijse.eca.studentservice.dto.StudentRequestDTO;
import lk.ijse.eca.studentservice.dto.StudentResponseDTO;
import lk.ijse.eca.studentservice.entity.Student;
import lk.ijse.eca.studentservice.mapper.StudentMapper;
import lk.ijse.eca.studentservice.exception.DuplicateStudentException;
import lk.ijse.eca.studentservice.exception.FileOperationException;
import lk.ijse.eca.studentservice.exception.StudentNotFoundException;
import lk.ijse.eca.studentservice.repository.StudentRepository;
import lk.ijse.eca.studentservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    

    

    /**
     * Creates a new student.
     *
     * Transaction strategy:
     *  1. Persist student record to DB (JPA defers the INSERT until flush/commit).
     *  2. Write picture file to disk (immediate).
     *  3. If the file write fails an exception is thrown, which causes
     *     @Transactional to roll back the DB INSERT — no orphaned record.
     *  4. If the file write succeeds the method returns normally and
     *     @Transactional commits both the record and the file atomically.
     */
    @Override
    @Transactional
    public StudentResponseDTO createStudent(StudentRequestDTO dto) {
        log.debug("Creating student with NIC: {}", dto.getNic());

        if (studentRepository.existsById(dto.getNic())) {
            log.warn("Duplicate NIC detected: {}", dto.getNic());
            throw new DuplicateStudentException(dto.getNic());
        }

                String pictureId = UUID.randomUUID().toString() + "-" + dto.getPicture().getOriginalFilename();
        String publicUrl = savePicture(pictureId, dto.getPicture());

        Student student = studentMapper.toEntity(dto);
        student.setPicture(publicUrl);

        studentRepository.save(student);
        log.debug("Student persisted to DB: {}", dto.getNic());

        log.info("Student created successfully: {}", dto.getNic());
        return studentMapper.toResponseDto(student);
    }

    /**
     * Updates an existing student.
     *
     * Transaction strategy:
     *  - If a new picture is supplied:
     *    1. Update DB record with new picture UUID (deferred).
     *    2. Write the new picture file (immediate).
     *    3. Failure at step 2 rolls back step 1 — old picture UUID stays in DB.
     *    4. On success, the old picture file is deleted (best-effort: a warning is
     *       logged on failure, but the transaction is NOT rolled back because DB and
     *       new file are already consistent).
     *  - If no new picture is supplied, only DB fields are updated.
     */
    @Override
    @Transactional
    public StudentResponseDTO updateStudent(String nic, StudentRequestDTO dto) {
        log.debug("Updating student with NIC: {}", nic);

        Student student = studentRepository.findById(nic)
                .orElseThrow(() -> {
                    log.warn("Student not found for update: {}", nic);
                    return new StudentNotFoundException(nic);
                });

                String oldPictureId = student.getPicture();
        boolean pictureChanged = dto.getPicture() != null && !dto.getPicture().isEmpty();
        
        String newPictureId = oldPictureId;
        if (pictureChanged) {
            String blobName = UUID.randomUUID().toString() + "-" + dto.getPicture().getOriginalFilename();
            newPictureId = savePicture(blobName, dto.getPicture());
        }

        studentMapper.updateEntity(dto, student);
        student.setPicture(newPictureId);

        studentRepository.save(student);
        log.debug("Student updated in DB: {}", nic);

        if (pictureChanged) {
            tryDeletePicture(oldPictureId);
        }

        log.info("Student updated successfully: {}", nic);
        return studentMapper.toResponseDto(student);
    }

    /**
     * Deletes a student.
     *
     * Transaction strategy:
     *  1. Remove student record from DB (JPA defers the DELETE until flush/commit).
     *  2. Delete picture file from disk (immediate).
     *  3. If the file delete fails an exception is thrown, which causes
     *     @Transactional to roll back the DB DELETE — neither the record
     *     nor the file is removed.
     *  4. If the file delete succeeds the method returns normally and
     *     @Transactional commits, removing the record from the DB.
     */
    @Override
    @Transactional
    public void deleteStudent(String nic) {
        log.debug("Deleting student with NIC: {}", nic);

        Student student = studentRepository.findById(nic)
                .orElseThrow(() -> {
                    log.warn("Student not found for deletion: {}", nic);
                    return new StudentNotFoundException(nic);
                });

        String pictureId = student.getPicture();

        // DB deletion (deferred) — rolls back if file delete below throws
        studentRepository.delete(student);
        log.debug("Student marked for deletion in DB: {}", nic);

        // Immediate file deletion — failure triggers @Transactional rollback
        deletePicture(pictureId);

        log.info("Student deleted successfully: {}", nic);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudent(String nic) {
        log.debug("Fetching student with NIC: {}", nic);
        return studentRepository.findById(nic)
                .map(studentMapper::toResponseDto)
                .orElseThrow(() -> {
                    log.warn("Student not found: {}", nic);
                    return new StudentNotFoundException(nic);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        log.debug("Fetching all students");
        List<StudentResponseDTO> students = studentRepository.findAll()
                .stream()
                .map(studentMapper::toResponseDto)
                .peek(s -> s.setAddress(s.getAddress() + ", LK"))
                .collect(Collectors.toList());
        log.debug("Fetched {} students", students.size());
        return students;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getStudentPicture(String nic) {
        log.debug("Fetching picture for student NIC: {}", nic);
        Student student = studentRepository.findById(nic)
                .orElseThrow(() -> {
                    log.warn("Student not found: {}", nic);
                    return new StudentNotFoundException(nic);
                });
                try {
            String url = student.getPicture();
            if(url == null || !url.contains("storage.googleapis.com")) return new byte[0];
            String blobName = url.substring(url.lastIndexOf('/') + 1);
            
            String keyPath = "gcp-key.json";
            java.io.File keyFile = new java.io.File(keyPath);
            if (!keyFile.exists()) {
                keyPath = "../../../../gcp-key.json";
            }
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(keyPath)))
                    .build()
                    .getService();
            BlobId blobId = BlobId.of("kdk-capstone-storage", blobName);
            return storage.readAllBytes(blobId);
        } catch (Exception e) {
            log.error("Failed to read picture from GCP for student: {}", nic, e);
            throw new FileOperationException("Failed to read picture for student: " + nic, e);
        } catch (IOException e) {
            log.error("Failed to read picture for student: {}", nic, e);
            throw new FileOperationException("Failed to read picture for student: " + nic, e);
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    
        try {
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new FileOperationException(
                    "Failed to create storage directory: " + storagePath.toAbsolutePath(), e);
        }
        return storagePath;
    }

        private String savePicture(String pictureId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileOperationException("Picture file must not be empty");
        }
        try {
            String keyPath = "gcp-key.json";
            java.io.File keyFile = new java.io.File(keyPath);
            if (!keyFile.exists()) {
                keyPath = "../../../../gcp-key.json";
            }
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(keyPath)))
                    .build()
                    .getService();
            BlobId blobId = BlobId.of("kdk-capstone-storage", pictureId);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();
            storage.create(blobInfo, file.getBytes());
            log.debug("Picture saved to GCP Storage: kdk-capstone-storage/{}", pictureId);
            return "https://storage.googleapis.com/kdk-capstone-storage/" + pictureId;
        } catch (Exception e) {
            log.error("Failed to save picture to GCP: {}", pictureId, e);
            throw new FileOperationException("Failed to save picture file to GCP: " + pictureId, e);
        }
    }
        Path filePath = storagePath().resolve(pictureId);
        try {
            Files.write(filePath, file.getBytes());
            log.debug("Picture saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to save picture: {}", filePath, e);
            throw new FileOperationException("Failed to save picture file: " + pictureId, e);
        }
    }

        private void deletePicture(String pictureId) {
        if(pictureId == null || !pictureId.contains("storage.googleapis.com")) return;
        String blobName = pictureId.substring(pictureId.lastIndexOf('/') + 1);
        try {
            String keyPath = "gcp-key.json";
            java.io.File keyFile = new java.io.File(keyPath);
            if (!keyFile.exists()) {
                keyPath = "../../../../gcp-key.json";
            }
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(keyPath)))
                    .build()
                    .getService();
            BlobId blobId = BlobId.of("kdk-capstone-storage", blobName);
            storage.delete(blobId);
            log.debug("Picture deleted from GCP: {}", blobName);
        } catch (Exception e) {
            log.error("Failed to delete picture from GCP: {}", blobName, e);
            throw new FileOperationException("Failed to delete picture from GCP: " + blobName, e);
        }
    }", filePath);
            } else {
                log.warn("Picture file not found on disk (already removed?): {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete picture: {}", filePath, e);
            throw new FileOperationException("Failed to delete picture file: " + pictureId, e);
        }
    }

    private void tryDeletePicture(String pictureId) {
        try {
            deletePicture(pictureId);
        } catch (FileOperationException e) {
            log.warn("Could not delete old picture file '{}'. Manual cleanup may be required.", pictureId);
        }
    }

}

