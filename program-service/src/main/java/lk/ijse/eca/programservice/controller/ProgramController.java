package lk.ijse.eca.programservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lk.ijse.eca.programservice.dto.ProgramDto;
import lk.ijse.eca.programservice.service.ProgramService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/programs")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ProgramController {

    private final ProgramService programService;

    private static final String PROGRAM_ID_REGEXP = "^[A-Z]+$";

    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ProgramDto> createProgram(
            @Validated({Default.class, ProgramDto.OnCreate.class})
            @RequestBody ProgramDto dto) {
        log.info("POST /api/v1/programs - programId: {}", dto.getProgramId());
        return ResponseEntity.status(HttpStatus.CREATED).body(programService.createProgram(dto));
    }

    @GetMapping(value = "/{programId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProgramDto> getProgram(
            @PathVariable
            @Pattern(regexp = PROGRAM_ID_REGEXP, message = "Program ID must contain uppercase letters only (A-Z)")
            String programId) {
        log.info("GET /api/v1/programs/{}", programId);
        return ResponseEntity.ok(programService.getProgram(programId));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProgramDto>> getAllPrograms() {
        log.info("GET /api/v1/programs - retrieving all programs");
        return ResponseEntity.ok(programService.getAllPrograms());
    }

    @PutMapping(
            value = "/{programId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ProgramDto> updateProgram(
            @PathVariable
            @Pattern(regexp = PROGRAM_ID_REGEXP, message = "Program ID must contain uppercase letters only (A-Z)")
            String programId,
            @Valid @RequestBody ProgramDto dto) {
        log.info("PUT /api/v1/programs/{}", programId);
        return ResponseEntity.ok(programService.updateProgram(programId, dto));
    }

    @DeleteMapping("/{programId}")
    public ResponseEntity<Void> deleteProgram(
            @PathVariable
            @Pattern(regexp = PROGRAM_ID_REGEXP, message = "Program ID must contain uppercase letters only (A-Z)")
            String programId) {
        log.info("DELETE /api/v1/programs/{}", programId);
        programService.deleteProgram(programId);
        return ResponseEntity.noContent().build();
    }
}
