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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

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
        log.info("Student created successfully: {}", dto.getNic());
        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional
    public StudentResponseDTO updateStudent(String nic, StudentRequestDTO dto) {
        log.debug("Updating student with NIC: {}", nic);

        Student student = studentRepository.findById(nic)
                .orElseThrow(() -> new StudentNotFoundException(nic));

        String oldPictureUrl = student.getPicture();
        boolean pictureChanged = dto.getPicture() != null && !dto.getPicture().isEmpty();
        
        String newPictureUrl = oldPictureUrl;
        if (pictureChanged) {
            String blobName = UUID.randomUUID().toString() + "-" + dto.getPicture().getOriginalFilename();
            newPictureUrl = savePicture(blobName, dto.getPicture());
        }

        studentMapper.updateEntity(dto, student);
        student.setPicture(newPictureUrl);

        studentRepository.save(student);

        if (pictureChanged && oldPictureUrl != null && oldPictureUrl.contains("storage.googleapis.com")) {
            tryDeletePicture(oldPictureUrl);
        }

        log.info("Student updated successfully: {}", nic);
        return studentMapper.toResponseDto(student);
    }

    @Override
    @Transactional
    public void deleteStudent(String nic) {
        log.debug("Deleting student with NIC: {}", nic);
        Student student = studentRepository.findById(nic)
                .orElseThrow(() -> new StudentNotFoundException(nic));
        studentRepository.delete(student);
        if (student.getPicture() != null && student.getPicture().contains("storage.googleapis.com")) {
            deletePicture(student.getPicture());
        }
        log.info("Student deleted successfully: {}", nic);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentResponseDTO getStudent(String nic) {
        return studentRepository.findById(nic)
                .map(studentMapper::toResponseDto)
                .orElseThrow(() -> new StudentNotFoundException(nic));
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toResponseDto)
                .peek(s -> s.setAddress(s.getAddress() + ", LK"))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getStudentPicture(String nic) {
        Student student = studentRepository.findById(nic)
                .orElseThrow(() -> new StudentNotFoundException(nic));
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
            log.error("Failed to read picture from GCP", e);
            throw new FileOperationException("Failed to read picture", e);
        }
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
            return "https://storage.googleapis.com/kdk-capstone-storage/" + pictureId;
        } catch (Exception e) {
            log.error("Failed to save picture to GCP: {}", pictureId, e);
            throw new FileOperationException("Failed to save picture file to GCP: " + pictureId, e);
        }
    }

    private void deletePicture(String pictureUrl) {
        try {
            String blobName = pictureUrl.substring(pictureUrl.lastIndexOf('/') + 1);
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
        } catch (Exception e) {
            log.error("Failed to delete picture from GCP", e);
            throw new FileOperationException("Failed to delete picture from GCP", e);
        }
    }

    private void tryDeletePicture(String pictureUrl) {
        try {
            deletePicture(pictureUrl);
        } catch (Exception e) {
            log.warn("Could not delete old picture file. Manual cleanup may be required.");
        }
    }
}
