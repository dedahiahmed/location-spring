package mr.iscae.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import mr.iscae.constants.Moughataa;
import mr.iscae.constants.Wilaya;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CabinetDto {

    private Long id;

    @NotBlank(message = "Nom cannot be null or empty")
    private String nom;

    @NotNull(message = "Willaya cannot be null")
    private Wilaya willaya;

    @NotNull(message = "Moughataa cannot be null")
    private Moughataa moughataa;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;
}
