package sovcombank.jabka.studyservice.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "study_groups")
@NoArgsConstructor
public class StudyGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_seq_generator")
    @SequenceGenerator(name = "group_seq_generator", sequenceName = "group_seq", allocationSize = 1)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;

}
