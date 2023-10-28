package sovcombank.jabka.studyservice.models;

import jakarta.persistence.*;
import lombok.*;
import sovcombank.jabka.studyservice.models.enums.ClassFormat;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "schedule")
@NoArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_seq_generator")
    @SequenceGenerator(name = "schedule_seq_generator", sequenceName = "schedule_seq", allocationSize = 1)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="date_time")
    private Date dateTime;
    @ManyToOne
    private StudyGroup group;
    @ManyToOne
    private Subject subject;
    @Enumerated(EnumType.STRING)
    @Column(name="class_format")
    private ClassFormat classFormat;
    private String auditorium;
    @Column(name="link_for_the_class")
    private String linkForTheClass;
    @ManyToOne
    private ProfessorIdTable professor;
    @OneToMany(mappedBy = "schedule", fetch = FetchType.EAGER)
    private Set<AttendanceStatistics> attendanceStatistics;


}
