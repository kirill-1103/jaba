package sovcombank.jabka.newsservice.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_sequence_generator")
    @SequenceGenerator(name = "news_sequence_generator", sequenceName = "news_sequence", allocationSize = 1)
    private Long id;
    @Column(name = "creation_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    private String header;
    @Column(length = 100000)
    private String text;
    private List<String> hashtags;
    private List<String> imgs;
}
