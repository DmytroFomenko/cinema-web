package dfomenko.entity;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket")
public class Ticket {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seance_id")
    private Long seanceId;

    @Column(name = "place_id")
    private Long placeId;

    @Column(name = "place_status", columnDefinition = "ENUM('FREE', 'RESERVED', 'TAKEN')")
    @Enumerated(EnumType.STRING)
    private PlaceStatus placeStatus;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "code")
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seance_id", nullable = false, insertable = false, updatable = false)
    private Seance seance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false, insertable = false, updatable = false)
    private Place place;

    public String getColorByStatus() {
        return switch (this.placeStatus) {
            case FREE -> "#8aff9d";
            case RESERVED -> "#efff8a";
            case TAKEN -> "#ff8aad";
            default -> "#FF0000";
        };
    }


}
