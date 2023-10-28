package sovcombank.jabka.studyservice.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "professor_id")
@NoArgsConstructor
public class ProfessorIdTable {
    @Id
    @Column(name="professor_id")
    private Long professorId;
}
