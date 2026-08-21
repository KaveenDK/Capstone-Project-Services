package lk.ijse.eca.enrollmentservice.service;

import lk.ijse.eca.enrollmentservice.dto.EnrollmentDto;

import java.util.List;

public interface EnrollmentService {

    EnrollmentDto createEnrollment(EnrollmentDto dto);

    EnrollmentDto updateEnrollment(Long id, EnrollmentDto dto);

    void deleteEnrollment(Long id);

    EnrollmentDto getEnrollment(Long id);

    List<EnrollmentDto> getAllEnrollments();

    List<EnrollmentDto> getEnrollmentsByProgramId(String programId);
}
