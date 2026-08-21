package lk.ijse.eca.enrollmentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDto {

    private Long id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "Program ID is required")
    private String programId;

    private StudentDto student;
}
