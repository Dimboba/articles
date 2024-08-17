package laz.dimboba.articles.article;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import laz.dimboba.articles.author.AuthorRepository;
import laz.dimboba.articles.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticlesController {

    private final ArticleRepository articleRepository;
    private final AuthorRepository authorRepository;

    @GetMapping
    public ResponseEntity<Page<Article>> getArticles(Pageable pageable) {
        var articles = articleRepository.findAll(pageable)
            .map(ArticleEntity::toDto);
        return ResponseEntity.ok(articles);
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(
        @Valid @RequestBody CreateArticleRequest request
    ) {
        var author = authorRepository.findById(request.authorId)
            .orElseThrow(() -> new BadRequestException("Author not found"));

        var entity = new ArticleEntity();
        entity.setPublishDate(request.publishDate());
        entity.setTitle(request.title());
        entity.setContent(request.content());
        entity.setAuthor(author);
        return ResponseEntity.ok(
            articleRepository.save(entity).toDto()
        );
    }

    public record CreateArticleRequest(
        @NotNull
        UUID authorId,
        @NotBlank
        @Size(max = 100)
        String title,
        @NotBlank
        @Size(max = 20_000)
        String content,
        @NotNull
        @FutureOrPresent
        LocalDate publishDate
    ) {}
}
