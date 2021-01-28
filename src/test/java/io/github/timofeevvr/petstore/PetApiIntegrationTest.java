package io.github.timofeevvr.petstore;

import io.github.timofeevvr.petstore.model.Pet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Slf4j
@Tag("pet")
class PetApiIntegrationTest extends TestBase {

    @ParameterizedTest
    @MethodSource("petProvider")
    void givenValidPet_whenAddPet_thenPetIsAddedSuccessfully(Pet pet) {
        log.info("WHEN. ADD PET");
        Long createdPetId = petstoreSteps.pet().addPet()
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
        Pet createdPet = petstoreSteps.pet().getPetById()
                .petIdPath(createdPetId)
                .execute(response -> response)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .extract()
                .as(Pet.class);
        assertThat(createdPet)
                .isEqualToIgnoringGivenFields(pet, "id");
        //cleanup
        petstoreSteps.pet().deletePet()
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
        Long createdPetId = petstoreSteps.pet().addPet()
                .body(pet)
                .execute(response -> response)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .body(notNullValue())
                .extract()
                .as(Pet.class)
                .getId();
        log.info("WHEN. DELETE PET");
        petstoreSteps.pet().deletePet()
                .petIdPath(createdPetId)
                .execute(response -> response)
                .then()
                .statusCode(HTTP_OK);
        log.info("THEN. CHECK PET IS REMOVED");
        petstoreSteps.pet().getPetById()
                .petIdPath(createdPetId)
                .execute(response -> response)
                .then()
                .assertThat()
                .statusCode(HTTP_NOT_FOUND);
    }

}