package com.roles.usermanagement.web.config;

import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class UserRoleSchemaMigration {
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationRunner migrateUserRoleSchema(DataSource dataSource, JdbcTemplate jdbc) {
        return args -> {
            try (var connection = dataSource.getConnection()) {
                if (!"PostgreSQL".equals(connection.getMetaData().getDatabaseProductName())) return;
            }
            String sql = new ClassPathResource("db/postgresql/single-role-and-user-permissions.sql")
                    .getContentAsString(StandardCharsets.UTF_8);
            jdbc.execute(sql.replace("\uFEFF", ""));
        };
    }
}
