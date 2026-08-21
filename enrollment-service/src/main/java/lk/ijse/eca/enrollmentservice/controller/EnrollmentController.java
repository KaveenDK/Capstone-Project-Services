package lk.ijse.eca.enrollmentservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lk.ijse.eca.enrollmentservice.dto.EnrollmentDto;
import lk.ijse.eca.enrollmentservice.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<EnrollmentDto> createEnrollment(
            @Valid @RequestBody EnrollmentDto dto) {
        log.info("POST /api/v1/enrollments - studentId: {}, programId: {}", dto.getStudentId(), dto.getProgramId());
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollmentService.createEnrollment(dto));
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnrollmentDto> getEnrollment(
            @PathVariable @Positive(message = "Enrollment ID must be a positive number") Long id) {
        log.info("GET /api/v1/enrollments/{}", id);
        return ResponseEntity.ok(enrollmentService.getEnrollment(id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EnrollmentDto>> getAllEnrollments() {
        log.info("GET /api/v1/enrollments - retrieving all enrollments");
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping(params = "programId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EnrollmentDto>> getEnrollmentsByProgramId(
            @RequestParam @NotBlank(message = "Program ID must not be blank") String programId) {
        log.info("GET /api/v1/enrollments?programId={} - retrieving enrollments by program", programId);
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByProgramId(programId));
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<EnrollmentDto> updateEnrollment(
            @PathVariable @Positive(message = "Enrollment ID must be a positive number") Long id,
            @Valid @RequestBody EnrollmentDto dto) {
        log.info("PUT /api/v1/enrollments/{}", id);
        return ResponseEntity.ok(enrollmentService.updateEnrollment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(
            @PathVariable @Positive(message = "Enrollment ID must be a positive number") Long id) {
        log.info("DELETE /api/v1/enrollments/{}", id);
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}
