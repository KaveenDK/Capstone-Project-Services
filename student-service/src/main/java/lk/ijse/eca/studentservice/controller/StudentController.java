package lk.ijse.eca.studentservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lk.ijse.eca.studentservice.dto.StudentRequestDTO;
import lk.ijse.eca.studentservice.dto.StudentResponseDTO;
import lk.ijse.eca.studentservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Slf4j
@Validated
public class StudentController {

    private final StudentService studentService;

    private static final String NIC_REGEXP = "^\\d{9}[vV]$";

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentResponseDTO> createStudent(
            @Validated({Default.class, StudentRequestDTO.OnCreate.class}) @ModelAttribute StudentRequestDTO dto) {
        log.info("POST /api/v1/students - NIC: {}", dto.getNic());
        StudentResponseDTO response = studentService.createStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{nic}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentResponseDTO> updateStudent(
            @PathVariable @Pattern(regexp = NIC_REGEXP, message = "NIC must be 9 digits followed by V or v") String nic,
            @Valid @ModelAttribute StudentRequestDTO dto) {
        log.info("PUT /api/v1/students/{}", nic);
        StudentResponseDTO response = studentService.updateStudent(nic, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{nic}")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable @Pattern(regexp = NIC_REGEXP, message = "NIC must be 9 digits followed by V or v") String nic) {
        log.info("DELETE /api/v1/students/{}", nic);
        studentService.deleteStudent(nic);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{nic}")
    public ResponseEntity<StudentResponseDTO> getStudent(
            @PathVariable @Pattern(regexp = NIC_REGEXP, message = "NIC must be 9 digits followed by V or v") String nic) {
        log.info("GET /api/v1/students/{}", nic);
        StudentResponseDTO response = studentService.getStudent(nic);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        log.info("GET /api/v1/students");
        List<StudentResponseDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{nic}/picture")
    public ResponseEntity<byte[]> getStudentPicture(
            @PathVariable @Pattern(regexp = NIC_REGEXP, message = "NIC must be 9 digits followed by V or v") String nic) {
        log.info("GET /api/v1/students/{}/picture", nic);
        byte[] picture = studentService.getStudentPicture(nic);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(picture);
    }
}
