package sovcombank.jabka.newsservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.sovkombank.openapi.model.NewsOpenAPI;
import sovcombank.jabka.newsservice.models.News;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface NewsMapper {
    @Mapping(target = "creationDate", source = "date", dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX")
    News toNews(NewsOpenAPI newsOpenAPI);
    @Mapping(target = "date", source = "creationDate", dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX")
    NewsOpenAPI toOpenApi(News news);
}
