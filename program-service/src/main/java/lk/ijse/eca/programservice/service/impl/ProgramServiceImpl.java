package lk.ijse.eca.programservice.service.impl;

import lk.ijse.eca.programservice.dto.ProgramDto;
import lk.ijse.eca.programservice.entity.Program;
import lk.ijse.eca.programservice.exception.DuplicateProgramException;
import lk.ijse.eca.programservice.exception.ProgramNotFoundException;
import lk.ijse.eca.programservice.mapper.ProgramMapper;
import lk.ijse.eca.programservice.repository.ProgramRepository;
import lk.ijse.eca.programservice.service.ProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramServiceImpl implements ProgramService {

    private final ProgramRepository programRepository;
    private final ProgramMapper programMapper;

    @Override
    @Transactional
    public ProgramDto createProgram(ProgramDto dto) {
        log.debug("Creating program with ID: {}", dto.getProgramId());

        if (programRepository.existsById(dto.getProgramId())) {
            log.warn("Duplicate program ID detected: {}", dto.getProgramId());
            throw new DuplicateProgramException(dto.getProgramId());
        }

        Program saved = programRepository.save(programMapper.toEntity(dto));
        log.info("Program created successfully: {}", saved.getProgramId());
        return programMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgramDto getProgram(String programId) {
        log.debug("Fetching program with ID: {}", programId);
        return programRepository.findById(programId)
                .map(programMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Program not found: {}", programId);
                    return new ProgramNotFoundException(programId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgramDto> getAllPrograms() {
        log.debug("Fetching all programs");
        List<ProgramDto> programs = programRepository.findAll()
                .stream()
                .map(programMapper::toDto)
                .toList();
        log.debug("Fetched {} program(s)", programs.size());
        return programs;
    }

    @Override
    @Transactional
    public ProgramDto updateProgram(String programId, ProgramDto dto) {
        log.debug("Updating program with ID: {}", programId);

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> {
                    log.warn("Program not found for update: {}", programId);
                    return new ProgramNotFoundException(programId);
                });

        programMapper.updateEntity(dto, program);
        Program updated = programRepository.save(program);
        log.info("Program updated successfully: {}", updated.getProgramId());
        return programMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteProgram(String programId) {
        log.debug("Deleting program with ID: {}", programId);

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> {
                    log.warn("Program not found for deletion: {}", programId);
                    return new ProgramNotFoundException(programId);
                });

        programRepository.delete(program);
        log.info("Program deleted successfully: {}", programId);
    }
}
