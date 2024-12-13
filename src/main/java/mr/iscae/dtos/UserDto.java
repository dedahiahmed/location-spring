package mr.iscae.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import mr.iscae.constants.Role;

@Getter
@Setter
public class UserDto {

    private Integer id;

    @NotBlank(message = "Full name cannot be empty")
    @Size(max = 255, message = "Full name must be less than 255 characters")
    private String fullName;

    @NotBlank(message = "Username cannot be empty")
    @Size(max = 255, message = "Username must be less than 255 characters")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotNull(message = "Role cannot be null")
    private Role role;
}
