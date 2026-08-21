package lk.ijse.eca.studentservice.exception;

public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(String nic) {
        super("Student with NIC '" + nic + "' not found");
    }
}
