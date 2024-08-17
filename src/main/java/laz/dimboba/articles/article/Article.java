package laz.dimboba.articles.article;

import laz.dimboba.articles.author.Author;

import java.time.LocalDate;
import java.util.UUID;

public record Article(
    UUID id,
    Author author,
    String title,
    String content,
    LocalDate publishDate
) {
}
