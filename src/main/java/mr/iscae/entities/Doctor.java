package mr.iscae.entities;


import jakarta.persistence.*;
import lombok.*;
import mr.iscae.constants.Speciality;

import java.util.Map;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Speciality speciality;

    @ElementCollection
    @CollectionTable(name = "schedule", joinColumns = @JoinColumn(name = "doctor_id"))
    @MapKeyColumn(name = "day")
    @Column(name = "hours")
    private Map<String, String> schedule;

    @ManyToOne
    @JoinColumn(name = "cabinet_id", nullable = false)
    private Cabinet cabinet;
}

