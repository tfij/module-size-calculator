package pl.tfij.test.modulesize;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectSummary {
    private final ArrayList<String> definedModules;
    private final Map<String, ModulePartialSummary> analyzedModules;

    ProjectSummary(ArrayList<String> modules, Map<String, ModulePartialSummary> analyzedModules) {
        definedModules = modules;
        this.analyzedModules = analyzedModules;
    }

    /**
     * Verifies whether none of the defined modules is empty
     *
     * @return the object on which the method was called to enable chaining
     * @throws AssertionError When any of the modules is empty.
     */
    public ProjectSummary verifyNoEmptyModules() {
        Optional<String> emptyModule = definedModules.stream().filter(it -> !analyzedModules.containsKey(it)).findFirst();
        emptyModule.ifPresent(module -> {
            throw new AssertionError("Module `%s` is empty.".formatted(module));
        });
        return this;
    }

    public ProjectSummary verifyEachModuleRelativeSizeIsSmallerThan(double threshold) {
        if (threshold <= 0 || threshold > 1) {
            throw new IllegalArgumentException("Threshold must be positive number in range (0, 1]. Given value is %s.".formatted(threshold));
        }
        Comparator<ModulePartialSummary> comparing = Comparator.comparing(ModulePartialSummary::relativeModuleSize);
        Optional<ModulePartialSummary> biggestModule = analyzedModules.values().stream().max(comparing);
        biggestModule.ifPresent(module -> {
            if (module.relativeModuleSize() > threshold) {
                throw new AssertionError("Module `%s` relative size is %.3f. Max available size is %.3f.".formatted(module.module(), module.relativeModuleSize(), threshold));
            }
        });
        return this;
    }

    public ProjectSummary verifyModuleIsSmallerThan(String module, double threshold) {
        if (!definedModules.contains(module)) {
            throw new IllegalArgumentException("Module `%s` was not defined.".formatted(module));
        }
        Optional.ofNullable(analyzedModules.get(module)).ifPresent(it -> {
            if (it.relativeModuleSize() > threshold) {
                throw new AssertionError("Module `%s` relative size is %.3f. Max available size is %.3f.".formatted(it.module(), it.relativeModuleSize(), threshold));
            }
        });
        return this;
    }

    public ProjectSummary verifyUndefinedModuleNumberOfFilesIsSmallerThan(int threshold) {
        Optional.ofNullable(analyzedModules.get(ModuleSizeCalculator.UNDEFINED_MODULE_NAME)).ifPresent(it -> {
            if (it.numberOfFiles() > threshold) {
                throw new AssertionError("Number of files in undefined module is %s. Max available count is %s.".formatted(it.numberOfFiles(), threshold));
            }
        });
        return this;
    }

    public String toMermaidPieChart() {
        String pieChartHeader = "pie showData title Modules size\n";
        String pieChartData = analyzedModules.values().stream()
                .map(it -> "    \"%s\" : %s".formatted(it.module(), it.moduleLinesOfCode()))
                .collect(Collectors.joining("\n"));
        return pieChartHeader + pieChartData;
    }

    /**
     * The list contains summaries for all defined modules. This summary can be used for generating custom reports, assertions, etc.
     *
     * @return list of analysed modules
     */
    public List<ModuleSummary> modulesSummary() {
        return analyzedModules.values().stream()
                .map(it -> new ModuleSummary(it.module(), it.numberOfFiles(), it.moduleLinesOfCode(), it.relativeModuleSize()))
                .toList();
    }

    /**
     * @return total number of files in the project
     */
    public int numberOfFiles() {
        return analyzedModules.values().stream().mapToInt(it -> it.numberOfFiles()).sum();
    }

    /**
     * @return total number of lines of code in the project
     */
    public int linesOfCode() {
        return analyzedModules.values().stream().mapToInt(it -> it.moduleLinesOfCode()).sum();
    }

    /**
     * @param module the name of the analyzed module
     * @param numberOfFiles number of files in the module
     * @param linesOfCode total number of lines of code in the module
     * @param relativeSize relative size of the module. It is a number in the range of 0-1.
     *        One represents 100%, indicating that all the code of the project is in this module.
     */
    public record ModuleSummary(String module, int numberOfFiles, int linesOfCode, double relativeSize) { }
}
