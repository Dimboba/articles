package laz.dimboba.articles.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.time.LocalDate.now;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/stats")
public class StatisticsController {

    private final StatsManager statsManager;

    @GetMapping
    public List<StatsManager.PublishingStats> getStatics() {
        return statsManager.getStats(now(), 7);
    }
}
