package sovcombank.jabka.studyservice.mappers;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

@Mapper(componentModel = "spring")
public class DateMapper {

    public Date toDate(OffsetDateTime offsetDateTime){
        Instant instant = offsetDateTime.toInstant();
        return Date.from(instant);
    }

    public OffsetDateTime toOffsetDateTime(Date date)    {
        Instant instant = date.toInstant();
        return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
