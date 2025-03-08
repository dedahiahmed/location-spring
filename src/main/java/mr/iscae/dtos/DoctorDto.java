package mr.iscae.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import mr.iscae.constants.Speciality;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {

    private Long id;

    @NotBlank(message = "Name cannot be null or empty")
    private String name;

    @NotNull(message = "Speciality cannot be null")
    private Speciality speciality;

    private Map<String, String> schedule;

    @NotNull(message = "Cabinet cannot be null")
    private Long cabinetId;
    
    private String cabinetName;
    private Double cabinetLongitude;
    private Double cabinetAltitude;
}
