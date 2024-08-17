package laz.dimboba.articles.author;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorRepository authorRepository;

    @GetMapping
    public ResponseEntity<Page<Author>> getAllAuthors(Pageable pageable) {
        return ResponseEntity.ok(
            authorRepository.findAll(pageable)
                .map(AuthorEntity::toDto)
        );
    }

    @PostMapping
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        var entity = new AuthorEntity();
        entity.setFirstName(request.firstName());
        entity.setLastName(request.lastName());
        return ResponseEntity.ok(authorRepository.save(entity).toDto());
    }

    public record CreateAuthorRequest(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName
    ) {}
}
