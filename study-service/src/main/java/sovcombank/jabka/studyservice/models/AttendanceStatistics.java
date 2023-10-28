package sovcombank.jabka.studyservice.models;

import jakarta.persistence.*;
import lombok.*;
import sovcombank.jabka.studyservice.models.enums.AttendanceStatus;

@Entity
@Table(name = "attendance")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class AttendanceStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_seq_generator")
    @SequenceGenerator(name = "attendance_seq_generator", sequenceName = "attendance_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, name = "student_id")
    @NonNull
    private Long studentId;

    @ManyToOne
    private Schedule schedule;

    @NonNull
    @Column(nullable = false, name = "attendance_status")
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendanceStatus;

}
