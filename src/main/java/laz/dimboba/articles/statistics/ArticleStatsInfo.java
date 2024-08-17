package laz.dimboba.articles.statistics;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

public interface ArticleStatsInfo {
    @Value("#{target.publish_date}")
    LocalDate getPublishDate();
    @Value("#{target.number}")
    Integer getNumber();
}
