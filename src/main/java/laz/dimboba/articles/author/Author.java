package laz.dimboba.articles.author;

import java.util.UUID;

public record Author(
    UUID id,
    String firstName,
    String lastName
) {
}
