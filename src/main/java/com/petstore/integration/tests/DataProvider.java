package com.petstore.integration.tests;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class DataProvider {

    private final Gson gson;

    public DataProvider(Gson gson) {
        this.gson = gson;
    }

    public <T> T fromJson(String path, Class<T> classOfT) {
        String jsonString = readResourceFileToString(path);
        return gson.fromJson(jsonString, classOfT);
    }

    public String readResourceFileToString(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UncheckedIOException("Resource file doesn't exists " + path, e);
        }
    }
}
