package sovcombank.jabka.newsservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.sovkombank.openapi.model.NewsOpenAPI;
import sovcombank.jabka.newsservice.repositories.NewsRepository;
import sovcombank.jabka.newsservice.mappers.NewsMapper;
import sovcombank.jabka.newsservice.models.News;
import sovcombank.jabka.newsservice.services.interfaces.NewsService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewServiceImpl implements NewsService {
    private final NewsMapper newsMapper;
    private final NewsRepository newsRepository;

    @Override
    public ResponseEntity<Void> createNewsPost(NewsOpenAPI newsOpenAPI) {
        News news = newsMapper.toNews(newsOpenAPI);
        if (Objects.nonNull(news)) {
            newsRepository.save(news);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();
        }
        return ResponseEntity
                .badRequest()
                .build();
    }

    @Override
    public ResponseEntity<List<NewsOpenAPI>> showAllNewsInfo() {
        List<News> news = newsRepository.findAll();
        return ResponseEntity
                .ok()
                .body(
                        news
                                .stream()
                                .map(newsMapper::toOpenApi)
                                .collect(Collectors.toList())
                );
    }

    @Override
    public ResponseEntity<Void> deleteNewsById(Long id) {
        Optional<News> newsOpt = newsRepository.findById(id);
        if (newsOpt.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        } else {
            newsRepository.deleteById(id);
            return ResponseEntity
                    .ok()
                    .build();
        }
    }

    @Override
    public ResponseEntity<Void> editNewsById(Long id, NewsOpenAPI newsOpenAPI) {
        News news = newsMapper.toNews(newsOpenAPI);
        Optional<News> newsOpt = newsRepository.findById(id);
        if (newsOpt.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        } else {
            newsRepository.save(news);
            return ResponseEntity
                    .ok()
                    .build();
        }
    }

    @Override
    public ResponseEntity<NewsOpenAPI> showNewsIdInfo(Long id) {
        Optional<News> newsOpt = newsRepository.findById(id);
        if (newsOpt.isEmpty()) {
            return ResponseEntity
                    .notFound()
                    .build();
        } else {
            News news = newsOpt.get();
            return ResponseEntity
                    .ok()
                    .body(
                            newsMapper.toOpenApi(news)
                    );
        }
    }
}
