package sovcombank.jabka.studyservice.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;


@Entity
@Table(name = "homework")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "homework_seq_generator")
    @SequenceGenerator(name = "homework_seq_generator", sequenceName = "homework_seq", allocationSize = 1)
    private Long id;

    @Column(name="student_id",nullable = false)
    @NonNull
    private Long studentId;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private Long grade;

    private String comment;

    @OneToMany(mappedBy = "homework")
    private Set<FileName> fileNames;

    @ManyToOne
    private StudyMaterials task;
}
