java -jar build/libs/petstore.integration.tests.jar \
    -c com.petstore.integration.tests.PetApiIntegrationTest \
    -c com.petstore.integration.tests.StoreApiIntegrationTest \
    -t pet -t store \
    --reports-dir ./test-results \
    --disable-ansi-colors