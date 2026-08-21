package lk.ijse.eca.studentservice.exception;

public class DuplicateStudentException extends RuntimeException {

    public DuplicateStudentException(String nic) {
        super("Student with NIC '" + nic + "' already exists");
    }
}
