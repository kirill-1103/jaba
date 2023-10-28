package sovcombank.jabka.newsservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sovkombank.openapi.api.NewsApi;
import ru.sovkombank.openapi.api.NewsApiDelegate;
import ru.sovkombank.openapi.model.NewsOpenAPI;
import sovcombank.jabka.libs.security.config.SecurityConfig;
import sovcombank.jabka.newsservice.services.interfaces.NewsService;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController implements NewsApiDelegate {
    private final NewsService newsService;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> createNewsPost(@RequestBody NewsOpenAPI newsOpenAPI) {
        return newsService.createNewsPost(newsOpenAPI);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('CURATOR') or hasRole('COMMITTE') or hasRole('ENROLLEE')")
    public ResponseEntity<List<NewsOpenAPI>> showAllNewsInfo() {
        return newsService.showAllNewsInfo();
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> deleteNewsById(@Valid @PathVariable Long id) {
        return newsService.deleteNewsById(id);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> editNewsById(@PathVariable(name = "id") Long id,
                                             @Valid @RequestBody NewsOpenAPI newsOpenAPI) {
        return newsService.editNewsById(id, newsOpenAPI);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('MODERATOR') or hasRole('TEACHER') " +
            "or hasRole('CURATOR') or hasRole('COMMITTE') or hasRole('ENROLLEE')")
    public ResponseEntity<NewsOpenAPI> showNewsIdInfo(@Valid @PathVariable("id") Long id) {
        return newsService.showNewsIdInfo(id);
    }
}
