package pl.tfij.test.modulesize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

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
                .withModule("pl.tfij.notification") // empty module
                .withModule("pl.tfij.users")
                .analyze();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> projectSummary.verifyNoEmptyModules());
        Assertions.assertEquals("Module `pl.tfij.notification` is empty.", error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.5, 0.7, 1.0})
    @DisplayName("Should not throw AssertionError when call verifyEachModuleRelativeSizeIsSmallerThan() and a module size is smaller then limit")
    void noErrorForVerifyEachModuleRelativeSizeIsSmallerThan(double threshold) {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        Assertions.assertDoesNotThrow(() -> projectSummary.verifyEachModuleRelativeSizeIsSmallerThan(threshold));
    }

    @Test
    @DisplayName("Should throw AssertionError when call verifyEachModuleRelativeSizeIsSmallerThan() and a module size is grater then limit")
    void assertionErrorForVerifyEachModuleRelativeSizeIsSmallerThan() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> projectSummary.verifyEachModuleRelativeSizeIsSmallerThan(0.2));
        Assertions.assertEquals("Module `pl.tfij.products` relative size is 0.429. Max allowed size is 0.2.", error.getMessage());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1    | Threshold must be positive number in range (0, 1]. Given value is -1.
            0     | Threshold must be positive number in range (0, 1]. Given value is 0.
            1.001 | Threshold must be positive number in range (0, 1]. Given value is 1.001.
            10    | Threshold must be positive number in range (0, 1]. Given value is 10.
            """, delimiter = '|')
    @DisplayName("Should throw IllegalArgumentException when call verifyEachModuleRelativeSizeIsSmallerThan() with invalid threshold")
    void argumentExceptionForVerifyEachModuleRelativeSizeIsSmallerThan(double threshold, String expectedMessage) {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        IllegalArgumentException error = Assertions.assertThrows(IllegalArgumentException.class, () -> projectSummary.verifyEachModuleRelativeSizeIsSmallerThan(threshold));
        Assertions.assertEquals(expectedMessage, error.getMessage());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            pl.tfij.commons      | 0.1
            pl.tfij.orders       | 0.2
            pl.tfij.payments     | 0.25
            pl.tfij.products     | 0.5
            pl.tfij.shipping     | 0.1
            pl.tfij.users        | 0.1
            """, delimiter = '|')
    @DisplayName("Should not throw AssertionError when call verifyModuleRelativeSizeIsSmallerThan() and a module size is smaller then limit")
    void noErrorForVerifyModuleRelativeSizeIsSmallerThan(String module, double threshold) {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        Assertions.assertDoesNotThrow(() -> projectSummary.verifyModuleRelativeSizeIsSmallerThan(module, threshold));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            pl.tfij.commons      | 0.01 | Module `pl.tfij.commons` relative size is 0.0165. Max allowed size is 0.01.
            pl.tfij.orders       | 0.1  | Module `pl.tfij.orders` relative size is 0.1722. Max allowed size is 0.1.
            pl.tfij.payments     | 0.2  | Module `pl.tfij.payments` relative size is 0.2335. Max allowed size is 0.2.
            pl.tfij.products     | 0.4  | Module `pl.tfij.products` relative size is 0.429. Max allowed size is 0.4.
            pl.tfij.shipping     | 0.05 | Module `pl.tfij.shipping` relative size is 0.0506. Max allowed size is 0.05.
            pl.tfij.users        | 0.05 | Module `pl.tfij.users` relative size is 0.0885. Max allowed size is 0.05.
            """, delimiter = '|')
    @DisplayName("Should throw AssertionError when call verifyModuleRelativeSizeIsSmallerThan() and a module size is grater then limit")
    void assertionErrorForVerifyModuleRelativeSizeIsSmallerThan(String module, double threshold, String expectedMessage) {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> projectSummary.verifyModuleRelativeSizeIsSmallerThan(module, threshold));
        Assertions.assertEquals(expectedMessage, error.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when call verifyModuleRelativeSizeIsSmallerThan() with not defined module")
    void argumentExceptionForVerifyModuleRelativeSizeIsSmallerThan() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.notification")
                .withModule("pl.tfij.users")
                .analyze();

        IllegalArgumentException error = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> projectSummary.verifyModuleRelativeSizeIsSmallerThan("pl.tfij.not-defined-module", 0.5));
        Assertions.assertEquals("Module `pl.tfij.not-defined-module` was not defined.", error.getMessage());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1    | Threshold must be positive number in range (0, 1]. Given value is -1.
            0     | Threshold must be positive number in range (0, 1]. Given value is 0.
            1.001 | Threshold must be positive number in range (0, 1]. Given value is 1.001.
            10    | Threshold must be positive number in range (0, 1]. Given value is 10.
            """, delimiter = '|')
    @DisplayName("Should throw IllegalArgumentException when call verifyModuleRelativeSizeIsSmallerThan() with invalid threshold")
    void argumentExceptionForVerifyEachModuleRelativeSizeIsSmallerThanWithInvalidThreshold(double threshold, String expectedMessage) {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        IllegalArgumentException error = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> projectSummary.verifyModuleRelativeSizeIsSmallerThan("pl.tfij.commons", threshold));
        Assertions.assertEquals(expectedMessage, error.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 6, 100})
    @DisplayName("Should not throw AssertionError when call verifyUndefinedModuleNumberOfFilesIsSmallerThan() and a module size is smaller then limit")
    void noErrorForVerifyUndefinedModuleNumberOfFilesIsSmallerThan(int fileCount) {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .analyze();

        Assertions.assertDoesNotThrow(() -> projectSummary.verifyUndefinedModuleNumberOfFilesIsSmallerThan(fileCount));
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            0 | Number of files in undefined module is 5. Max allowed count is 0.
            1 | Number of files in undefined module is 5. Max allowed count is 1.
            4 | Number of files in undefined module is 5. Max allowed count is 4.
            """, delimiter = '|')
    @DisplayName("Should throw AssertionError when call verifyUndefinedModuleNumberOfFilesIsSmallerThan() and file count of unnamed module is grater then limit")
    void assertionErrorForVerifyUndefinedModuleNumberOfFilesIsSmallerThan(int allowedFileCount, String expectedMessage) {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .analyze();

        AssertionError error = Assertions.assertThrows(AssertionError.class, () -> projectSummary.verifyUndefinedModuleNumberOfFilesIsSmallerThan(allowedFileCount));
        Assertions.assertEquals(expectedMessage, error.getMessage());
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            -1   | allowedFileCount must be positive number or zero. Give value is -1.
            -2   | allowedFileCount must be positive number or zero. Give value is -2.
            -100 | allowedFileCount must be positive number or zero. Give value is -100.
            """, delimiter = '|')
    @DisplayName("Should throw IllegalArgumentException when call verifyUndefinedModuleNumberOfFilesIsSmallerThan() with invalid threshold")
    void argumentExceptionForVerifyUndefinedModuleNumberOfFilesIsSmallerThan(int allowedFileCount, String expectedMessage) {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        IllegalArgumentException error = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> projectSummary.verifyUndefinedModuleNumberOfFilesIsSmallerThan(allowedFileCount));
        Assertions.assertEquals(expectedMessage, error.getMessage());
    }
}
