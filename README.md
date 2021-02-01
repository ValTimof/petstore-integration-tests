[![Java CI with Gradle](https://github.com/timofeevvr/petstore-integration-tests/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/timofeevvr/petstore-integration-tests/actions)

# petstore-integration-tests
A sample test automation framework 

## Run tests with Allure
```shell
./gradlew -DPETSTORE_URL=https://petstore.swagger.io/v2 -DincludeTags=pet,store clean test allureReport allureServe --info --stacktrace
```

## Run tests with Report Portal
1. Start Report Portal  
`docker-compose -f report-portal/docker-compose.yaml up`
2. Get `rp.uuid`
   1. Open Report Portal UI http://localhost:8080/ui/#login
   2. Login as Default user with password 1q2w3e
   3. Open Profile
   4. Copy `rp.uuid` from `CONFIGURATION EXAMPLES` section
3. Enable Report Portal and add `rp.uuid` parameter to run tests command
```shell
./gradlew -Drp.enable=true -Drp.uuid=%UUID% -DPETSTORE_URL=https://petstore.swagger.io/v2 -DincludeTags=pet,store clean test --info --stacktrace
```

## Run tests as standalone jar
```shell
./gradlew clean assemble --info --stacktrace
java -DPETSTORE_URL=https://petstore.swagger.io/v2 -jar build/libs/petstore.integration.tests.jar -p io.github.timofeevvt -t pet -t store --reports-dir test-results --disable-ansi-colors
```
