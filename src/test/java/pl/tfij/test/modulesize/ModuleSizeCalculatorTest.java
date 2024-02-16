package pl.tfij.test.modulesize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

class ModuleSizeCalculatorTest {

    @Test
    @DisplayName("Should analyze all files (recursively)")
    void shouldAnalyzeTestProject() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        Assertions.assertEquals(7, projectSummary.modulesSummary().size());
        Assertions.assertEquals(22, projectSummary.numberOfFiles());
        Assertions.assertEquals(1028, projectSummary.linesOfCode());
    }

    @Test
    @DisplayName("Should analyze java files")
    void shouldAnalyzeJavaFiles() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .include("java")
                .analyze();

        Assertions.assertEquals(7, projectSummary.modulesSummary().size());
        Assertions.assertEquals(20, projectSummary.numberOfFiles());
        Assertions.assertEquals(968, projectSummary.linesOfCode());
    }

    @Test
    @DisplayName("Should analyze java and csv files")
    void shouldAnalyzeJavaAndCsvFiles() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .include("java", "csv")
                .analyze();

        Assertions.assertEquals(7, projectSummary.modulesSummary().size());
        Assertions.assertEquals(21, projectSummary.numberOfFiles());
        Assertions.assertEquals(1013, projectSummary.linesOfCode());
    }

    @Test
    @DisplayName("Should analyze java, csv and no-extension files")
    void shouldAnalyzeJavaCsvNoExtensionFiles() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .include("java", "csv", "")
                .analyze();

        Assertions.assertEquals(7, projectSummary.modulesSummary().size());
        Assertions.assertEquals(22, projectSummary.numberOfFiles());
        Assertions.assertEquals(1028, projectSummary.linesOfCode());
    }

    @Test
    @DisplayName("Should throw ModuleSizeCalculatorException when project dir does not exists")
    void shouldThrowExceptionIfProjectDirNotExists() {
        ModuleSizeCalculator.ModuleSizeCalculatorBuilder calculatorForNotExistingProject = ModuleSizeCalculator.project("src/test/resources/not-existing-project")
                .withModule("pl.tfij.commons");

        ModuleSizeCalculatorException moduleSizeCalculatorException = Assertions.assertThrows(
                ModuleSizeCalculatorException.class,
                () -> calculatorForNotExistingProject.analyze());

        String expectedMessageRegex = "Error occur on scanning project in `.*[\\/\\\\]src[\\/\\\\]test[\\/\\\\]resources[\\/\\\\]not-existing-project` directory\\.";
        Assertions.assertTrue(moduleSizeCalculatorException.getMessage().matches(expectedMessageRegex));
    }

}