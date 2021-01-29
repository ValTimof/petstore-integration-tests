package io.github.timofeevvr.petstore;


import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TestBase {

    @Autowired
    PetstoreSteps petstoreSteps;
}
