package pl.tfij.test.modulesize;

import java.util.List;

record ModulePartialSummary(FileInModule.Module module, int numberOfFiles, int moduleLinesOfCode, int projectLinesOfCode) {

    static ModulePartialSummary aggregate(List<FileInModule> fileInModules, int projectLinesOfCode) {
        FileInModule.Module module = fileInModules.get(0).module().orElse(FileInModule.Module.UNDEFINED_MODULE);
        int numberOfFiles = fileInModules.size();
        int totalLinesOfCode = fileInModules.stream().mapToInt(it -> it.lineOfCode()).sum();
        return new ModulePartialSummary(module, numberOfFiles, totalLinesOfCode, projectLinesOfCode);
    }

    double relativeModuleSize() {
        return 1.0 * moduleLinesOfCode / projectLinesOfCode;
    }
}
