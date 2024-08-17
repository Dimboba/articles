package laz.dimboba.articles;

import org.springframework.boot.SpringApplication;

public class TestArticlesApplication {

    public static void main(String[] args) {
        SpringApplication.from(ArticlesApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
