package lk.ijse.eca.studentservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lk.ijse.eca.studentservice.validation.ValidImage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class StudentRequestDTO {

    public interface OnCreate {}

    @NotBlank(groups = OnCreate.class, message = "NIC is required")
    @Pattern(groups = OnCreate.class, regexp = "^\\d{9}[vV]$", message = "NIC must be 9 digits followed by V or v")
    private String nic;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ]*$", message = "Name can only contain letters and spaces")
    private String name;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Mobile is required")
    private String mobile;

    @Email(message = "Invalid email format")
    private String email;

    @NotNull(groups = OnCreate.class, message = "Picture is required")
    @ValidImage
    private MultipartFile picture;
}
