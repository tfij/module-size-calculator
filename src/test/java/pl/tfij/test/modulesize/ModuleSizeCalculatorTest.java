package pl.tfij.test.modulesize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ModuleSizeCalculatorTest {

    @Test
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
        Assertions.assertEquals(20, projectSummary.numberOfFiles());
        Assertions.assertEquals(968, projectSummary.linesOfCode());
    }

    @Test
    @DisplayName("Should not throw AssertionError when call verifyNoEmptyModules() and no module is empty")
    void noErrorForVerifyNoEmptyModules() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        Assertions.assertDoesNotThrow(() -> projectSummary.verifyNoEmptyModules());
    }

    @Test
    @DisplayName("Should throw AssertionError when call verifyNoEmptyModules() and a module is empty")
    void assertionErrorForVerifyNoEmptyModules() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.notification")
                .withModule("pl.tfij.users")
                .analyze();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> projectSummary.verifyNoEmptyModules());
        Assertions.assertEquals("Module `pl.tfij.notification` is empty.", error.getMessage());
    }

}