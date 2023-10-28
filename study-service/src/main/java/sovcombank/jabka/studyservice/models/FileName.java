package sovcombank.jabka.studyservice.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "files")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileName {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_seq_generator")
    @SequenceGenerator(name = "file_seq_generator", sequenceName = "file_seq", allocationSize = 1)
    private Long id;
    @Column(name="initial_name")
    private String initialName;
    @Column(name="name_s3",unique = true)
    private String nameS3;
    @ManyToOne
    private Homework homework;
}
