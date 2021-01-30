package io.github.timofeevvr.petstore;

import io.github.timofeevvr.petstore.model.Pet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Tag("pet")
class PetApiIntegrationTest extends TestBase {

    static List<Pet> createdPets = new ArrayList<>();

    @AfterAll
    void cleanup() {
        createdPets.forEach(petstoreSteps::deletePet);
    }

    @ParameterizedTest
    @MethodSource("io.github.timofeevvr.petstore.DataProvider#petProvider")
    void addPetSuccessfullyAddsPetToTheStore(Pet pet) {
        // when
        var createdPetId = petstoreSteps.addPet(pet).getId();
        // then
        var createdPet = petstoreSteps.getExistingPet(createdPetId);
        createdPets.add(createdPet);

        assertThat(createdPet)
                .as("Check the pet created correctly")
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(pet);
    }

    @ParameterizedTest
    @MethodSource("io.github.timofeevvr.petstore.DataProvider#petProvider")
    void deletePetSuccessfullyRemovesPetFromTheStore(Pet pet) {
        // given
        var createdPetId = petstoreSteps.addPet(pet).getId();
        // when
        petstoreSteps.deletePet(createdPetId);
        // then
        petstoreSteps.getPet(createdPetId)
                .assertThat()
                .statusCode(HTTP_NOT_FOUND);
    }

}