package mr.iscae.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import mr.iscae.constants.Role;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "fullName cannot be blank")
    private String fullName;
    @NotBlank(message = "username cannot be blank")
    private String username;
    @NotBlank(message = "password cannot be blank")
    private String password;
    private Role role;
}
