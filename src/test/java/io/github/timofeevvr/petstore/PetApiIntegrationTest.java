package io.github.timofeevvr.petstore;

import io.github.timofeevvr.extensions.Retryable;
import io.github.timofeevvr.petstore.model.Pet;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static io.github.timofeevvr.petstore.DataProvider.petsFromTestdata;
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

    @Execution(ExecutionMode.CONCURRENT)
    @ParameterizedTest(name = "[{index}] {displayName}")
    @MethodSource("io.github.timofeevvr.petstore.DataProvider#petsFromTestdata")
    void addPetAndCheckPetIsAdded(Pet pet) {
        // when
        var createdPetId = petstoreSteps.addPet(pet).getId();
        // then
        var createdPet = petstoreSteps.getExistingPet(createdPetId);
        createdPets.add(createdPet);

        assertThat(createdPet)
                .as("Check the pet is created correctly")
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(pet);
    }

    @Retryable(2) // just for demo
    @ParameterizedTest(name = "[{index}] {displayName}")
    @MethodSource("existingPetIds")
    void deletePetAndCheckPetIsDeleted(Long petId) {
        // when
        petstoreSteps.deletePet(petId);
        // then
        petstoreSteps.getPet(petId)
                .assertThat()
                .statusCode(HTTP_NOT_FOUND);
    }

    Stream<Long> existingPetIds() {
        var pets = petstoreSteps.findAllPets();
        if (pets.length > 0) {
            return Arrays.stream(pets).map(Pet::getId).limit(5);
        } else {
            return petsFromTestdata().map(pet -> petstoreSteps.addPet(pet).getId());
        }
    }

}