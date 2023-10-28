package sovcombank.jabka.newsservice.services.interfaces;

import org.springframework.http.ResponseEntity;
import ru.sovkombank.openapi.model.NewsOpenAPI;

import java.util.List;

public interface NewsService {
    ResponseEntity<Void> createNewsPost(NewsOpenAPI newsOpenAPI);
    ResponseEntity<List<NewsOpenAPI>> showAllNewsInfo();
    ResponseEntity<Void> deleteNewsById(Long id);
    ResponseEntity<Void> editNewsById(Long id, NewsOpenAPI newsOpenAPI);
    ResponseEntity<NewsOpenAPI> showNewsIdInfo(Long id);

}
