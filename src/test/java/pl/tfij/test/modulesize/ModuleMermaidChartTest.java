package pl.tfij.test.modulesize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class ModuleMermaidChartTest {

    @Test
    @DisplayName("Should generate Mermaid pie chart")
    void generateMermaidPieChart() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        String mermaidPieChart = projectSummary.createMermaidPieChart();

        String expectedChartContent = """
                pie showData title Modules size (Total LOC: 1028)
                    "pl.tfij.commons" : 17
                    "pl.tfij.orders" : 177
                    "pl.tfij.payments" : 240
                    "pl.tfij.products" : 441
                    "pl.tfij.shipping" : 52
                    "pl.tfij.users" : 91
                    "undefined" : 10
                """.trim();
        Assertions.assertEquals(expectedChartContent, mermaidPieChart);
    }

    @Test
    @DisplayName("Should save Mermaid pie chart")
    void saveMermaidPieChart() throws IOException {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        Path testChart = Files.createTempFile("test-mermaid-file", ".mermaid");
        projectSummary.saveMermaidPieChart(testChart);

        String expectedChartContent = """
                pie showData title Modules size (Total LOC: 1028)
                    "pl.tfij.commons" : 17
                    "pl.tfij.orders" : 177
                    "pl.tfij.payments" : 240
                    "pl.tfij.products" : 441
                    "pl.tfij.shipping" : 52
                    "pl.tfij.users" : 91
                    "undefined" : 10
                """.trim();
        Assertions.assertEquals(expectedChartContent, getFileContent(testChart));
    }

    @Test
    @DisplayName("Should throw ModuleSizeCalculatorException if try to save file as directory")
    void errorOnSavingMermaidPieChart() throws IOException {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        Path testChart = Files.createTempDirectory("test-mermaid-directory");

        ModuleSizeCalculatorException exception = Assertions.assertThrows(
                ModuleSizeCalculatorException.class,
                () -> projectSummary.saveMermaidPieChart(testChart));
        Assertions.assertTrue(exception.getMessage().startsWith("IO error occur on saving mermaid chart to "));
    }

    @Test
    @DisplayName("Should throw ModuleSizeCalculatorException if try to save file as directory")
    void argumentErrorOnSavingMermaidPieChart() {
        ProjectSummary projectSummary = ModuleSizeCalculator.project("src/test/resources/test-project")
                .withModule("pl.tfij.commons")
                .withModule("pl.tfij.orders")
                .withModule("pl.tfij.payments")
                .withModule("pl.tfij.products")
                .withModule("pl.tfij.shipping")
                .withModule("pl.tfij.users")
                .analyze();

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> projectSummary.saveMermaidPieChart(null));
        Assertions.assertEquals("The target argument must be not null.", exception.getMessage());
    }

    private static String getFileContent(Path testChart) throws FileNotFoundException {
        FileReader fileReader = new FileReader(testChart.toFile());
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        return bufferedReader.lines().collect(Collectors.joining("\n"));
    }
}
