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
public class PharmacyDto {

    private Long id;

    @NotBlank(message = "Name cannot be null or empty")
    private String name;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Willaya cannot be null")
    private Wilaya willaya;

    @NotNull(message = "Moughataa cannot be null")
    private Moughataa moughataa;

    private String img;

    private boolean isOpenTonight;
    private Double distanceInKm;
}
