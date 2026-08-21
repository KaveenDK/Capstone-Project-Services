package lk.ijse.eca.programservice.exception;

public class ProgramNotFoundException extends RuntimeException {

    public ProgramNotFoundException(String programId) {
        super("Program not found with ID: " + programId);
    }
}
