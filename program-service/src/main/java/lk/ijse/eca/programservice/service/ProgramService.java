package lk.ijse.eca.programservice.service;

import lk.ijse.eca.programservice.dto.ProgramDto;

import java.util.List;

public interface ProgramService {

    ProgramDto createProgram(ProgramDto dto);

    ProgramDto getProgram(String programId);

    List<ProgramDto> getAllPrograms();

    ProgramDto updateProgram(String programId, ProgramDto dto);

    void deleteProgram(String programId);
}
