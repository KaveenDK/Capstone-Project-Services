package lk.ijse.eca.programservice.exception;

public class DuplicateProgramException extends RuntimeException {

    public DuplicateProgramException(String programId) {
        super("Program already exists with ID: " + programId);
    }
}
