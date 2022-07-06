package ru.izebit.project.config;

import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author <a href="mailto:izebit@gmail.com">Artem Konovalov</a> <br/>
 * Date: 05.07.2022
 */
@Configuration
@Profile("!test")
public class DatabaseConfig {

    @Bean
    @SneakyThrows
    public MongoLiquibaseDatabase mongoLiquibaseDatabase(@Value("${spring.data.mongodb.host}") String host,
                                                         @Value("${spring.data.mongodb.port}") long port,
                                                         @Value("${spring.data.mongodb.username}") String username,
                                                         @Value("${spring.data.mongodb.password}") String password,
                                                         @Value("${spring.data.mongodb.database}") String database) {
        return (MongoLiquibaseDatabase) DatabaseFactory
                .getInstance()
                .openDatabase(String.format("mongodb://%s:%s/%s", host, port, database), username, password, null, null);
    }

    @Bean
    @SneakyThrows
    public Liquibase liquibase(@Value("${spring.liquibase.change-log}") String liquebaseChangeLog,
                               MongoLiquibaseDatabase database) {
        Liquibase liquibase = new Liquibase(liquebaseChangeLog, new ClassLoaderResourceAccessor(), database);
        liquibase.update("");
        return liquibase;
    }
}
