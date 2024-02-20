package pl.tfij.test.modulesize;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static pl.tfij.test.modulesize.FileInModule.Module.UNDEFINED_MODULE;

public class ProjectSummary {
    private final List<FileInModule.Module> definedModules;
    private final Map<FileInModule.Module, ModulePartialSummary> analyzedModules;
    private final DecimalFormat decimalFormat;

    ProjectSummary(List<FileInModule.Module> modules, Map<FileInModule.Module, ModulePartialSummary> analyzedModules) {
        definedModules = modules;
        this.analyzedModules = analyzedModules;
        decimalFormat = numberFormatter();
    }

    private DecimalFormat numberFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        return new DecimalFormat("#.####", symbols);
    }

    /**
     * Verifies whether none of the defined modules is empty.
     *
     * @return The ProjectSummary instance to allow method chaining.
     * @throws AssertionError When any of the modules is empty.
     */
    public ProjectSummary verifyNoEmptyModules() {
        Optional<FileInModule.Module> emptyModule = definedModules.stream().filter(it -> !analyzedModules.containsKey(it)).findFirst();
        emptyModule.ifPresent(module -> {
            throw new AssertionError("Module `%s` is empty.".formatted(module.name()));
        });
        return this;
    }

    /**
     * Verifies that the relative size of each moduleName is smaller than the given threshold.
     *
     * @param threshold The maximum relative size allowed for each moduleName. Must be a positive number in the range (0, 1].
     * @return The ProjectSummary instance to allow method chaining.
     * @throws IllegalArgumentException if the threshold is not within the valid range.
     * @throws AssertionError           if any moduleName's relative size exceeds the threshold.
     */
    public ProjectSummary verifyEachModuleRelativeSizeIsSmallerThan(double threshold) {
        verifyRelativeSizeThreshold(threshold);
        Comparator<ModulePartialSummary> comparing = Comparator.comparing(ModulePartialSummary::relativeModuleSize);
        Optional<ModulePartialSummary> biggestModule = analyzedModules.values().stream().max(comparing);
        biggestModule.ifPresent(summary -> {
            if (summary.relativeModuleSize() > threshold) {
                throw new AssertionError("Module `%s` relative size is %s. Max allowed size is %s."
                        .formatted(
                                summary.module().name(),
                                decimalFormat.format(summary.relativeModuleSize()),
                                decimalFormat.format(threshold)
                        ));
            }
        });
        return this;
    }

    /**
     * Verifies that the relative size of the specified moduleName is smaller than the given threshold.
     *
     * @param module    The name of the moduleName to verify.
     * @param threshold The maximum relative size allowed for the moduleName.
     * @return The ProjectSummary instance to allow method chaining.
     * @throws IllegalArgumentException if the specified moduleName is not defined.
     * @throws IllegalArgumentException if the threshold is not within the valid range.
     * @throws AssertionError           if the relative size of the moduleName exceeds the threshold.
     */
    public ProjectSummary verifyModuleRelativeSizeIsSmallerThan(String module, double threshold) {
        Optional<FileInModule.Module> definedModule = definedModules.stream().filter(it -> Objects.equals(it.name(), module)).findFirst();
        if (definedModule.isEmpty()) {
            throw new IllegalArgumentException("Module `%s` was not defined.".formatted(module));
        }
        verifyRelativeSizeThreshold(threshold);
        Optional.ofNullable(analyzedModules.get(definedModule.get())).ifPresent(moduleSummary -> {
            if (moduleSummary.relativeModuleSize() > threshold) {
                throw new AssertionError("Module `%s` relative size is %s. Max allowed size is %s."
                        .formatted(
                                moduleSummary.module().name(),
                                decimalFormat.format(moduleSummary.relativeModuleSize()),
                                decimalFormat.format(threshold)
                        ));
            }
        });
        return this;
    }

    private void verifyRelativeSizeThreshold(double threshold) {
        if (threshold <= 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be positive number in range (0, 1]. Given value is %s."
                    .formatted(decimalFormat.format(threshold)));
        }
    }

    /**
     * Verifies that the number of files in the undefined moduleName is smaller than the specified allowedFileCount.
     *
     * @param allowedFileCount The maximum number of files allowed in the undefined moduleName. Value must be positive or zero int number.
     * @return The ProjectSummary instance to allow method chaining.
     * @throws IllegalArgumentException if the given allowedFileCount has invalid value.
     * @throws AssertionError           if the number of files in the undefined moduleName exceeds the allowedFileCount.
     */
    public ProjectSummary verifyUndefinedModuleNumberOfFilesIsSmallerThan(int allowedFileCount) {
        if (allowedFileCount < 0) {
            throw new IllegalArgumentException("allowedFileCount must be positive number or zero. Give value is %s.".formatted(allowedFileCount));
        }
        Optional.ofNullable(analyzedModules.get(UNDEFINED_MODULE)).ifPresent(it -> {
            if (it.numberOfFiles() > allowedFileCount) {
                throw new AssertionError("Number of files in undefined moduleName is %s. Max allowed count is %s."
                        .formatted(it.numberOfFiles(), allowedFileCount));
            }
        });
        return this;
    }

    /**
     * Generates a Mermaid pie chart representation based on analyzed modules.
     *
     * @return A string representing the Mermaid pie chart.
     */
    public String createMermaidPieChart() {
        String pieChartHeader = "pie showData title Modules size (Total LOC: %d)\n".formatted(linesOfCode());
        String pieChartData = analyzedModules.values().stream()
                .sorted(Comparator.comparing(it -> it.module().name()))
                .map(it -> "    \"%s\" : %s".formatted(it.module().name(), it.moduleLinesOfCode()))
                .collect(Collectors.joining("\n"));
        return pieChartHeader + pieChartData;
    }

    /**
     * Saves the Mermaid pie chart representation of the project summary to the specified target path.
     *
     * @param target the path where the Mermaid pie chart will be saved.
     * @return The ProjectSummary instance to allow method chaining.
     * @throws ModuleSizeCalculatorException if an I/O error occurs while saving the Mermaid chart
     * @throws IllegalArgumentException      if the target path is null
     */
    public ProjectSummary saveMermaidPieChart(Path target) {
        if (target == null) {
            throw new IllegalArgumentException("The target argument must be not null.");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(target.toFile()));) {
            String mermaidPieChart = createMermaidPieChart();
            writer.write(mermaidPieChart);
            return this;
        } catch (IOException ex) {
            throw new ModuleSizeCalculatorException("IO error occur on saving mermaid chart to %s.".formatted(target.toAbsolutePath()), ex);
        }
    }

    /**
     * Generates a summary of the analyzed modules.
     *
     * @return A list containing ModuleSummary objects representing each analyzed moduleName.
     */
    public List<ModuleSummary> modulesSummary() {
        return analyzedModules.values().stream()
                .map(it -> new ModuleSummary(it.module().name(), it.numberOfFiles(), it.moduleLinesOfCode(), it.relativeModuleSize()))
                .toList();
    }

    /**
     * @return total number of files in the project.
     */
    public int numberOfFiles() {
        return analyzedModules.values().stream().mapToInt(it -> it.numberOfFiles()).sum();
    }

    /**
     * @return total number of lines of code in the project.
     */
    public int linesOfCode() {
        return analyzedModules.values().stream().mapToInt(it -> it.moduleLinesOfCode()).sum();
    }

    /**
     * @param moduleName the name of the analyzed moduleName
     * @param numberOfFiles number of files in the moduleName
     * @param linesOfCode total number of lines of code in the moduleName
     * @param relativeSize relative size of the moduleName. It is a number in the range of 0-1.
     *        One represents 100%, indicating that all the code of the project is in this moduleName.
     */
    public record ModuleSummary(String moduleName, int numberOfFiles, int linesOfCode, double relativeSize) { }
}
