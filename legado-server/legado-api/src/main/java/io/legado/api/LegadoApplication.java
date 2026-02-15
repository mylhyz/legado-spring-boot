package io.legado.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Legado 后端服务启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"io.legado"})
@EnableJpaRepositories(basePackages = {"io.legado.model.repository"})
@EntityScan(basePackages = {"io.legado.model.entity"})
@EnableCaching
public class LegadoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegadoApplication.class, args);
    }

}
