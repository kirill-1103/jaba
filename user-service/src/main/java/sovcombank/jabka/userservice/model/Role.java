package sovcombank.jabka.userservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.sovcombank.openapi.model.ERoleOpenApi;

@Entity
@Table(name = "roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq_generator")
    @SequenceGenerator(name = "roles_seq_generator", sequenceName = "roles_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private ERoleOpenApi name;

    public Role(ERoleOpenApi name) {
        this.name = name;
    }
}
