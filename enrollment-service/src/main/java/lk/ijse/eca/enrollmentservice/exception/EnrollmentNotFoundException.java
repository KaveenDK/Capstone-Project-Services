package lk.ijse.eca.enrollmentservice.exception;

public class EnrollmentNotFoundException extends RuntimeException {

    public EnrollmentNotFoundException(Long id) {
        super("Enrollment with ID '" + id + "' not found");
    }
}
