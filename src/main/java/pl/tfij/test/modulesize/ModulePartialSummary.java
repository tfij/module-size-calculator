package pl.tfij.test.modulesize;

import java.util.List;

record ModulePartialSummary(String module, int numberOfFiles, int moduleLinesOfCode, int projectLinesOfCode) {

    static ModulePartialSummary aggregate(List<FileInModule> fileInModules, int projectLinesOfCode) {
        String module = fileInModules.get(0).module().map(it -> it.name()).orElse(ModuleSizeCalculator.UNDEFINED_MODULE_NAME);
        int numberOfFiles = fileInModules.size();
        int totalLinesOfCode = fileInModules.stream().mapToInt(it -> it.lineOfCode()).sum();
        return new ModulePartialSummary(module, numberOfFiles, totalLinesOfCode, projectLinesOfCode);
    }

    double relativeModuleSize() {
        return 1.0 * moduleLinesOfCode / projectLinesOfCode;
    }
}
