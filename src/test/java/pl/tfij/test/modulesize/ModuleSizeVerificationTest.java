package pl.tfij.test.modulesize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ModuleSizeVerificationTest {

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
