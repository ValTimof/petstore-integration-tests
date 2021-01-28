package com.petstore.integration.tests;

import com.petstore.integration.tests.model.Pet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@Tag("pet")
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PetApiIntegrationTest {

    @Autowired
    private DataProvider dataProvider;
    @Autowired
    private ApiClient petstoreClient;

    @ParameterizedTest
    @MethodSource("petProvider")
    void givenValidPet_whenAddPet_thenPetIsAddedSuccessfully(Pet pet) {
        log.info("WHEN. ADD PET");
        Long createdPetId = petstoreClient.pet().addPet()
                .body(pet)
                .execute(response -> response)
                .then()
                .log()
                .status()
                .assertThat()
                .statusCode(HTTP_OK)
                .body(notNullValue())
                .extract()
                .as(Pet.class)
                .getId();
        log.info("THEN. CHECK PET IS ADDED");
        Pet createdPet = petstoreClient.pet().getPetById()
                .petIdPath(createdPetId)
                .execute(response -> response)
                .then()
                .log()
                .status()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .as(Pet.class);
        assertThat(createdPet)
                .isEqualToIgnoringGivenFields(pet, "id");
        //cleanup
        petstoreClient.pet().deletePet()
                .petIdPath(createdPetId)
                .execute(response -> response)
                .then()
                .log()
                .status();
    }

    @ParameterizedTest
    @MethodSource("petProvider")
    void givenPetExitsInService_whenDeletePet_thenPetIsRemoved(Pet pet) {
        log.info("GIVEN. ADD PET");
        Long createdPetId = petstoreClient.pet().addPet()
                .body(pet)
                .execute(response -> response)
                .then()
                .log()
                .status()
                .assertThat()
                .statusCode(HTTP_OK)
                .body(notNullValue())
                .extract()
                .as(Pet.class)
                .getId();
        log.info("WHEN. DELETE PET");
        petstoreClient.pet().deletePet()
                .petIdPath(createdPetId)
                .execute(response -> response)
                .then()
                .log()
                .status()
                .statusCode(HTTP_OK);
        log.info("THEN. CHECK PET IS REMOVED");
        petstoreClient.pet().getPetById()
                .petIdPath(createdPetId)
                .execute(response -> response)
                .then()
                .log()
                .status()
                .assertThat()
                .statusCode(HTTP_NOT_FOUND);
    }

    Stream<Pet> petProvider() {
        return Arrays.stream(dataProvider.fromJson("pets.json", Pet[].class));
    }

}