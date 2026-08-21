package lk.ijse.eca.enrollmentservice.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lk.ijse.eca.enrollmentservice.exception.EnrollmentNotFoundException;
import lk.ijse.eca.enrollmentservice.exception.ProgramServiceException;
import lk.ijse.eca.enrollmentservice.exception.StudentServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Global exception handler following RFC 9457 (Problem Details for HTTP APIs).
 * All error responses are served as {@code application/problem+json}.
 */
@RestControllerAdvice
@Slf4j
@Order(-1)
public class GlobalExceptionHandler {

    @ExceptionHandler(EnrollmentNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEnrollmentNotFound(
            EnrollmentNotFoundException ex, HttpServletRequest request) {
        log.warn("Enrollment not found: {}", ex.getMessage());
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.NOT_FOUND, "Enrollment Not Found", ex.getMessage(), request.getRequestURI());
        return problemResponse(HttpStatus.NOT_FOUND, problem);
    }

    @ExceptionHandler(StudentServiceException.class)
    public ResponseEntity<ProblemDetail> handleStudentServiceException(
            StudentServiceException ex, HttpServletRequest request) {
        log.error("Student service error: {}", ex.getMessage(), ex);
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.BAD_GATEWAY,
                "Student Service Error",
                "Unable to retrieve student details. Please try again later.",
                request.getRequestURI());
        return problemResponse(HttpStatus.BAD_GATEWAY, problem);
    }

    @ExceptionHandler(ProgramServiceException.class)
    public ResponseEntity<ProblemDetail> handleProgramServiceException(
            ProgramServiceException ex, HttpServletRequest request) {
        log.error("Program service error: {}", ex.getMessage(), ex);
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.BAD_GATEWAY,
                "Program Service Error",
                "Unable to validate program. Please ensure the program ID is valid and try again.",
                request.getRequestURI());
        return problemResponse(HttpStatus.BAD_GATEWAY, problem);
    }

    /**
     * Handles {@code @Valid} on {@code @RequestBody} and {@code @ModelAttribute}.
     * {@link org.springframework.web.bind.MethodArgumentNotValidException} is a subclass
     * of {@link BindException}, so this single handler covers both cases.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(
            BindException ex, HttpServletRequest request) {

        String detail = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation failed for [{}]: {}", request.getRequestURI(), detail);

        ProblemDetail problem = buildProblemDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, "Validation Error", detail, request.getRequestURI());

        // Attach individual field errors as RFC 9457 extension members
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> problem.setProperty(fe.getField(), fe.getDefaultMessage()));

        return problemResponse(HttpStatus.UNPROCESSABLE_ENTITY, problem);
    }

    /**
     * Handles constraint violations from {@code @Validated} path/query variable validation.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest request) {

        String detail = ex.getConstraintViolations()
                .stream()
                .map(cv -> {
                    String path = cv.getPropertyPath().toString();
                    String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                    return field + ": " + cv.getMessage();
                })
                .collect(Collectors.joining("; "));

        log.warn("Constraint violation for [{}]: {}", request.getRequestURI(), detail);

        ProblemDetail problem = buildProblemDetail(
                HttpStatus.UNPROCESSABLE_ENTITY, "Validation Error", detail, request.getRequestURI());

        ex.getConstraintViolations().forEach(cv -> {
            String path = cv.getPropertyPath().toString();
            String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
            problem.setProperty(field, cv.getMessage());
        });

        return problemResponse(HttpStatus.UNPROCESSABLE_ENTITY, problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneric(
            Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception on [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI());
        return problemResponse(HttpStatus.INTERNAL_SERVER_ERROR, problem);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private ProblemDetail buildProblemDetail(
            HttpStatus status, String title, String detail, String instance) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(instance));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    private ResponseEntity<ProblemDetail> problemResponse(HttpStatus status, ProblemDetail problem) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(problem);
    }
}
