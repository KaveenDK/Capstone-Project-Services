package lk.ijse.eca.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
public class StudentResponseDTO {

    private String nic;
    private String name;
    private String address;
    private String mobile;
    private String email;
    private String picture;
}
