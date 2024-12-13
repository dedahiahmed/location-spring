package mr.iscae.entities;


import jakarta.persistence.*;
import lombok.*;
import mr.iscae.constants.Moughataa;
import mr.iscae.constants.Wilaya;

@Entity
@Table(name = "pharmacy")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double latitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Wilaya willaya;

    @Enumerated(EnumType.STRING)
    @Column(name = "moughataa", nullable = false)
    private Moughataa moughataa;

    private String img;

    @Column(name = "is_open_tonight")
    private boolean isOpenTonight;
}

