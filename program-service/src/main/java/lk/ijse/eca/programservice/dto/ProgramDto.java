package lk.ijse.eca.programservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramDto {

    public interface OnCreate {}

    @NotBlank(groups = OnCreate.class, message = "Program ID is required")
    @Pattern(groups = OnCreate.class, regexp = "^[A-Z]+$", message = "Program ID must contain uppercase letters only (A-Z)")
    private String programId;

    @NotBlank(message = "Description cannot be blank")
    private String description;
}
