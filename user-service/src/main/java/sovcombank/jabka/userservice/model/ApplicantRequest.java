package sovcombank.jabka.userservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import sovcombank.jabka.userservice.model.enums.ApplicantRequestStatus;

@Entity
@Table(name = "applicant_request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicantRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "applicant_request_seq_generator")
    @SequenceGenerator(name = "applicant_request_seq_generator", sequenceName = "applicant_request_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String leaderName;

    @Column(nullable = false)
    @NotBlank
    private String subunitName;

    @Column(nullable = false)
    @NotBlank
    private String currentPosition;

    @Column(nullable = false)
    @NotBlank
    private String workExperience;

    @Column(nullable = false)
    @NotBlank
    private String personalAchievements;

    @Column(nullable = false)
    @NotBlank
    private String motivationMessage;

    @Enumerated(EnumType.STRING)
    private ApplicantRequestStatus requestStatus;

    @ManyToOne
    @NotBlank
    private UserEntity user;
}
