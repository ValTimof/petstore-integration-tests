package com.petstore.integration.tests;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;

public class TestNameGenerator extends DisplayNameGenerator.ReplaceUnderscores {
    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        return camelCaseToWhiteSpaces(super.generateDisplayNameForClass(testClass));
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        return camelCaseToWhiteSpaces(super.generateDisplayNameForNestedClass(nestedClass));
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return camelCaseToWhiteSpaces(super.generateDisplayNameForMethod(testClass, testMethod));
    }

    private static String camelCaseToWhiteSpaces(String name) {
        return name
                .replaceAll("([A-Z])", " $1")
                .replaceAll("^\\s?.", name.substring(0, 1).toUpperCase());
    }
}
