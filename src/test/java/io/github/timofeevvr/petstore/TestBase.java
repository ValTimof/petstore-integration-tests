package io.github.timofeevvr.petstore;


import io.github.timofeevvr.petstore.model.Pet;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.stream.Stream;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestBase {


    @Autowired
    DataProvider dataProvider;
    @Autowired
    ApiClient petstoreClient;

    Stream<Pet> petProvider() {
        return Arrays.stream(dataProvider.fromJson("testdata/pets.json", Pet[].class));
    }
}
