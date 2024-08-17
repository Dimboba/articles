package laz.dimboba.articles.statistics;

import laz.dimboba.articles.article.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsManager {
    private final ArticleRepository articleRepository;

    public List<PublishingStats> getStats(LocalDate startDate, int duration) {
        return articleRepository.getStats(startDate, startDate.plusDays(duration))
            .stream()
            .map(info -> new PublishingStats(info.getPublishDate(), info.getNumber()))
            .toList();
    }

    public record PublishingStats(
        LocalDate date,
        int number
    ) {}
}
