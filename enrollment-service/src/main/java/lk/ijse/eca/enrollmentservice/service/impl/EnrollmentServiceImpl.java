package lk.ijse.eca.enrollmentservice.service.impl;

import lk.ijse.eca.enrollmentservice.client.ProgramServiceClient;
import lk.ijse.eca.enrollmentservice.client.StudentServiceClient;
import lk.ijse.eca.enrollmentservice.dto.EnrollmentDto;
import lk.ijse.eca.enrollmentservice.dto.StudentDto;
import lk.ijse.eca.enrollmentservice.entity.Enrollment;
import lk.ijse.eca.enrollmentservice.exception.EnrollmentNotFoundException;
import lk.ijse.eca.enrollmentservice.mapper.EnrollmentMapper;
import lk.ijse.eca.enrollmentservice.repository.EnrollmentRepository;
import lk.ijse.eca.enrollmentservice.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
    private final StudentServiceClient studentServiceClient;
    private final ProgramServiceClient programServiceClient;

    @Override
    @Transactional
    public EnrollmentDto createEnrollment(EnrollmentDto dto) {
        log.debug("Creating enrollment for student: {}, program: {}", dto.getStudentId(), dto.getProgramId());

        // Validate both references before any DB write
        StudentDto student = studentServiceClient.getStudent(dto.getStudentId());
        programServiceClient.validateProgram(dto.getProgramId());

        Enrollment enrollment = enrollmentMapper.toEntity(dto);
        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Enrollment created successfully with ID: {}", saved.getId());
        return enrollmentMapper.toDto(saved, student);
    }

    @Override
    @Transactional
    public EnrollmentDto updateEnrollment(Long id, EnrollmentDto dto) {
        log.debug("Updating enrollment with ID: {}", id);

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Enrollment not found for update: {}", id);
                    return new EnrollmentNotFoundException(id);
                });

        // Validate both references before updating
        StudentDto student = studentServiceClient.getStudent(dto.getStudentId());
        programServiceClient.validateProgram(dto.getProgramId());

        enrollmentMapper.updateEntity(dto, enrollment);
        Enrollment updated = enrollmentRepository.save(enrollment);
        log.info("Enrollment updated successfully: {}", id);
        return enrollmentMapper.toDto(updated, student);
    }

    @Override
    @Transactional
    public void deleteEnrollment(Long id) {
        log.debug("Deleting enrollment with ID: {}", id);

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Enrollment not found for deletion: {}", id);
                    return new EnrollmentNotFoundException(id);
                });

        enrollmentRepository.delete(enrollment);
        log.info("Enrollment deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentDto getEnrollment(Long id) {
        log.debug("Fetching enrollment with ID: {}", id);

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Enrollment not found: {}", id);
                    return new EnrollmentNotFoundException(id);
                });

        StudentDto student = studentServiceClient.getStudent(enrollment.getStudentId());
        return enrollmentMapper.toDto(enrollment, student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDto> getAllEnrollments() {
        log.debug("Fetching all enrollments");

        List<EnrollmentDto> enrollments = enrollmentRepository.findAll()
                .stream()
                .map(enrollment -> {
                    StudentDto student = studentServiceClient.getStudent(enrollment.getStudentId());
                    return enrollmentMapper.toDto(enrollment, student);
                })
                .toList();

        log.debug("Fetched {} enrollment(s)", enrollments.size());
        return enrollments;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentDto> getEnrollmentsByProgramId(String programId) {
        log.debug("Fetching enrollments for programId: {}", programId);

        List<EnrollmentDto> enrollments = enrollmentRepository.findByProgramIdOrderByDateDescIdDesc(programId)
                .stream()
                .map(enrollment -> {
                    StudentDto student = studentServiceClient.getStudent(enrollment.getStudentId());
                    return enrollmentMapper.toDto(enrollment, student);
                })
                .toList();

        log.debug("Fetched {} enrollment(s) for programId: {}", enrollments.size(), programId);
        return enrollments;
    }
}
