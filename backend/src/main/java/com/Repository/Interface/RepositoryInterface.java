package com.Repository.Interface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public interface RepositoryInterface<T> {
    String url = loadDatabaseUrl();
    static String loadDatabaseUrl() {
        Properties properties = new Properties();
        try (var input = RepositoryInterface.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("config.properties not found in classpath");
            }
            properties.load(input);
            return properties.getProperty("db.url");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database URL from config file", e);
        }
    }

    T add(T t);

    void update(int id, T t);

    void remove(int id);

    List<T> getAll();

    T getById(int id);
}
