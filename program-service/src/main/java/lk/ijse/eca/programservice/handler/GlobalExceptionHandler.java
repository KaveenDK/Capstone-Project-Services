package lk.ijse.eca.programservice.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lk.ijse.eca.programservice.exception.DuplicateProgramException;
import lk.ijse.eca.programservice.exception.ProgramNotFoundException;
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

    @ExceptionHandler(ProgramNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleProgramNotFound(
            ProgramNotFoundException ex, HttpServletRequest request) {
        log.warn("Program not found: {}", ex.getMessage());
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.NOT_FOUND, "Program Not Found", ex.getMessage(), request.getRequestURI());
        return problemResponse(HttpStatus.NOT_FOUND, problem);
    }

    @ExceptionHandler(DuplicateProgramException.class)
    public ResponseEntity<ProblemDetail> handleDuplicateProgram(
            DuplicateProgramException ex, HttpServletRequest request) {
        log.warn("Duplicate program: {}", ex.getMessage());
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.CONFLICT, "Duplicate Program", ex.getMessage(), request.getRequestURI());
        return problemResponse(HttpStatus.CONFLICT, problem);
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
