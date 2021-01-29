package io.github.timofeevvr.petstore;

import com.google.gson.Gson;
import io.github.timofeevvr.petstore.model.Pet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
public class DataProvider {

    private static final Gson gson = new JSON().getGson();

    private DataProvider() {
    }

    public static Stream<Pet> petProvider() {
        return Arrays.stream(fromJson("testdata/pets.json", Pet[].class));
    }

    public static <T> T fromJson(String path, Class<T> classOfT) {
        String jsonString = readResourceFileToString(path);
        return gson.fromJson(jsonString, classOfT);
    }

    public static String readResourceFileToString(String path) {
        try (InputStream is = new ClassPathResource(path).getInputStream()) {
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UncheckedIOException("Resource file doesn't exists " + path, e);
        }
    }
}
