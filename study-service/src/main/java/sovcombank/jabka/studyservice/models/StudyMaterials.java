package sovcombank.jabka.studyservice.models;

import jakarta.persistence.*;
import lombok.*;
import sovcombank.jabka.studyservice.models.enums.StudyMaterialsType;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "study_materials")
@NoArgsConstructor
public class StudyMaterials {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "materials_seq_generator")
    @SequenceGenerator(name = "materials_seq_generator", sequenceName = "materials_seq", allocationSize = 1)
    private Long id;
    @Enumerated(EnumType.STRING)
    private StudyMaterialsType type;
    @ManyToOne
    private Subject subject;
    private String materialsText;
    @OneToMany(  mappedBy = "task")
    private Set<Homework> homeworks;

    @ManyToMany
    @JoinTable(
            name = "materials_files",
            joinColumns = @JoinColumn(
                    name = "materials_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "file_id", referencedColumnName = "id"
            )
    )
    private Set<FileName> attachedFiles;


}
