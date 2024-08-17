package laz.dimboba.articles;

import laz.dimboba.articles.article.Article;
import laz.dimboba.articles.author.Author;
import laz.dimboba.articles.security.AuthController;
import laz.dimboba.articles.statistics.StatsManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.time.LocalDate.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@AutoConfigureWebTestClient(timeout = "3600000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ArticlesApplicationTests {

    @Autowired
    private WebTestClient webClient;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldNotCreateAuthorIfUnauthorized() {
        webClient.get()
            .uri("/api/authors")
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void shouldCreateAuthor() {
        var res = createDefaultAuthor(defaultAuthorRequest);
        assertNotNull(res);
        assertEquals(defaultAuthorRequest.get("firstName"), res.firstName());
        assertEquals(defaultAuthorRequest.get("lastName"), res.lastName());
    }

    @Test
    void shouldGetAuthors() {
        createDefaultAuthor(defaultAuthorRequest);
        var page = webClient.get().uri("/api/authors")
            .header("Authorization", getAdminToken())
            .exchange()
            .expectStatus().isOk()
            .expectBody(Object.class)
            .returnResult().getResponseBody();
        assertNotNull(page);
    }

    @Test
    void shouldNotCreateAuthorIdFieldIsNotValid() {
        var incorrectUser = Map.of("firstName", "test");

        webClient.post().uri("/api/authors")
            .header("Authorization", getAdminToken())
            .body(BodyInserters.fromValue(incorrectUser))
            .exchange()
            .expectStatus().isBadRequest();

        incorrectUser = Map.of("firstName", "", "lastName", "test");

        webClient.post().uri("/api/authors")
            .header("Authorization", getUserToken())
            .body(BodyInserters.fromValue(incorrectUser))
            .exchange()
            .expectStatus().isBadRequest();
    }

    @Test
    void shouldCreateArticle() {
        Author author = createDefaultAuthor(defaultAuthorRequest);
        var request = getDefaultArticleRequest(author.id());
        var article = createDeafultArticle(request);
        assertNotNull(article);
        assertEquals(author, article.author());
        assertEquals(request.get("title"), article.title());
        assertEquals(request.get("content"), article.content());
        assertEquals(request.get("publishDate"), article.publishDate());
    }

    @Test
    void shouldNotCreateArticleIfUnauthorized() {
        webClient.get()
            .uri("/api/articles")
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    void shouldNotCreateArticleIfAuthorNotExist() {
        var request = Map.of(
            "authorId", UUID.randomUUID(),
            "title", "test title",
            "content", "test content",
            "publishDate", now().plusDays(3)
        );

        webClient.post().uri("/api/articles")
            .header("Authorization", getAdminToken())
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void shouldNotCreateArticleIfTitleTooLong() {
        var request = Map.of(
            "authorId", UUID.randomUUID(),
            "title", new StringBuilder().repeat("t", 101).toString(),
            "content", "test content",
            "publishDate", now().plusDays(3)
        );

        webClient.post().uri("/api/articles")
            .header("Authorization", getAdminToken())
            .body(BodyInserters.fromValue(request))
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void shouldNotGetStatsForNotAdminUser() {
        webClient.get()
            .uri("/api/admin/stats")
            .header("Authorization", getUserToken())
            .exchange()
            .expectStatus().isForbidden();

        webClient.get()
            .uri("/api/admin/stats")
            .exchange()
            .expectStatus().isForbidden();

    }

    @Test
    void shouldGetStaticsForAdminUser() {
        Author author = createDefaultAuthor(defaultAuthorRequest);
        for(int i = 0; i < 3; i++) {
            var request = getDefaultArticleRequest(author.id(), now().plusDays(1));
            createDeafultArticle(request);
        }
        for(int i = 0; i < 2; i++) {
            var request = getDefaultArticleRequest(author.id(), now().plusDays(2));
            createDeafultArticle(request);
        }
        for(int i = 0; i < 2; i++) {
            var request = getDefaultArticleRequest(author.id(), now().plusDays(3));
            createDeafultArticle(request);
        }
        var res = webClient.get()
            .uri("/api/admin/stats")
            .header("Authorization", getAdminToken())
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(StatsManager.PublishingStats.class)
            .returnResult().getResponseBody();
        assertNotNull(res);
        var mappedRes = res.stream().collect(Collectors.toMap(
            StatsManager.PublishingStats::date,
            StatsManager.PublishingStats::number
            ));
        assertEquals(3, mappedRes.get(now().plusDays(1)));
        assertEquals(2, mappedRes.get(now().plusDays(2)));
        assertEquals(2, mappedRes.get(now().plusDays(3)));
    }

    private String getAdminToken() {
        return "Bearer " + Objects.requireNonNull(webClient.post().uri("/auth")
                .body(BodyInserters.fromValue(Map.of(
                    "username", "admin",
                    "password", "admin"
                )))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthController.LoginResponse.class)
                .returnResult()
                .getResponseBody())
            .token();
    }

    private String getUserToken() {
        return "Bearer " + Objects.requireNonNull(webClient.post().uri("/auth")
                .body(BodyInserters.fromValue(Map.of(
                    "username", "user",
                    "password", "password"
                )))
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthController.LoginResponse.class)
                .returnResult()
                .getResponseBody())
            .token();
    }

    private Article createDeafultArticle(Map<String, ?> body) {
        return webClient.post().uri("/api/articles")
            .header("Authorization", getUserToken())
            .body(BodyInserters.fromValue(body))
            .exchange()
            .expectStatus().isOk()
            .expectBody(Article.class)
            .returnResult()
            .getResponseBody();
    }

    private Author createDefaultAuthor(Map<String, ?> body) {
        return webClient.post().uri("/api/authors")
            .header("Authorization", getUserToken())
            .body(BodyInserters.fromValue(body))
            .exchange()
            .expectStatus().isOk()
            .expectBody(Author.class)
            .returnResult()
            .getResponseBody();
    }

    private Map<String, ?> getDefaultArticleRequest(UUID author, LocalDate publishDate) {
        return Map.of(
            "authorId", author,
            "title", "test title",
            "content", "test content",
            "publishDate", publishDate
        );
    }

    private Map<String, ?> getDefaultArticleRequest(UUID author) {
        return getDefaultArticleRequest(author, now().plusMonths(1));
    }

    private final Map<String, ?> defaultAuthorRequest = Map.of(
        "firstName", "Test",
        "lastName", "Testov"
    );

}
