package mr.iscae.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import mr.iscae.constants.Moughataa;
import mr.iscae.constants.Wilaya;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cabinet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Wilaya willaya;

    @Enumerated(EnumType.STRING)
    @Column(name = "moughataa", nullable = false)
    private Moughataa moughataa;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    @OneToMany(mappedBy = "cabinet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Doctor> doctors;
}

