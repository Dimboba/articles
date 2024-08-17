package laz.dimboba.articles.article;

import laz.dimboba.articles.statistics.ArticleStatsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<ArticleEntity, UUID> {

    @Query(value = "select publish_date, count(*) as number " +
        "from articles where publish_date >= ?1 and publish_date < ?2 " +
        "GROUP BY publish_date " +
        "ORDER BY publish_date ", nativeQuery = true)
    List<ArticleStatsInfo> getStats(LocalDate start, LocalDate end);
}
