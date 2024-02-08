package pl.tfij.test.modulesize;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleSizeCalculator {
    public static final String UNDEFINED_MODULE_NAME = "undefined";
    private final List<Module> modules;
    private final String rootDir;

    public ModuleSizeCalculator(List<String> modules, String rootDir) {
        this.modules = modules.stream()
                .map(it -> new Module(it, rootDir + "/" + it.replaceAll("\\.", "/")))
                .toList();
        this.rootDir = rootDir;
    }

    public static ModuleSizeCalculatorBuilder project(String rootDir) {
        return new ModuleSizeCalculatorBuilder(rootDir);
    }

    public static class ModuleSizeCalculatorBuilder {
        private final String rootDir;
        private final ArrayList<String> modules = new ArrayList<>();

        public ModuleSizeCalculatorBuilder(String rootDir) {
            this.rootDir = rootDir;
        }

        public ModuleSizeCalculatorBuilder withModule(String modulePackage) {
            modules.add(modulePackage);
            return this;
        }

        public ProjectSummary analyze() {
            ModuleSizeCalculator moduleSizeCalculator = new ModuleSizeCalculator(modules, rootDir);
            Map<String, ModulePartialSummary> analyzedModules = moduleSizeCalculator.calculate();
            return new ProjectSummary(modules, analyzedModules);
        }
    }

    public static class ProjectSummary {
        private final ArrayList<String> definedModules;
        private final Map<String, ModulePartialSummary> analyzedModules;

        ProjectSummary(ArrayList<String> modules, Map<String, ModulePartialSummary> analyzedModules) {
            definedModules = modules;
            this.analyzedModules = analyzedModules;
        }

        public ProjectSummary verifyNoEmptyModules() {
            Optional<String> emptyModule = definedModules.stream().filter(it -> !analyzedModules.containsKey(it)).findFirst();
            emptyModule.ifPresent(module -> {
                throw new AssertionError("Module `%s` is empty.".formatted(module));
            });
            return this;
        }

        public ProjectSummary verifyEachModuleRelativeSizeIsSmallerThen(double threshold) {
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

        public ProjectSummary verifyModuleIsSmallerThen(String module, double threshold) {
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

        public ProjectSummary verifyUndefinedModuleNumberOfFilesIsSmallerThen(int threshold) {
            Optional.ofNullable(analyzedModules.get(UNDEFINED_MODULE_NAME)).ifPresent(it -> {
                if (it.numberOfFiles() > threshold) {
                    throw new AssertionError("Number of files in undefined module is %s. Max available count is %s.".formatted(it.numberOfFiles(), threshold));
                }
            });
            return this;
        }

        public String toMermaidPieChart() {
            String pieChartHeader = "pie showData title Modules size\n";
            String pieChartData = analyzedModules.values().stream()
                    .map(it -> "    \"%s\" : %s".formatted(it.module(), it.linesOfCode()))
                    .collect(Collectors.joining("\n"));
            return pieChartHeader + pieChartData;
        }
    }

    public Map<String, ModulePartialSummary> calculate() {
        List<FileInModule> pathStream = getAllFilesInDirectory(Paths.get(rootDir))
                .filter(it -> it.getFileName().toString().endsWith(".java"))
                .map(it -> new FileInModule(
                        it.toString(),
                        matchModule(it, modules),
                        countLines(it)))
                .toList();
        int projectLinesOfCode = pathStream.stream().mapToInt(it -> it.lineOfCode).sum();
        return pathStream.stream()
                .collect(Collectors.groupingBy(it -> it.module))
                .values()
                .stream()
                .map(it -> ModulePartialSummary.aggregate(it, projectLinesOfCode))
                .collect(Collectors.toMap(it -> it.module, it -> it));
    }


    private Optional<Module> matchModule(Path file, List<Module> modules) {
        return modules.stream().filter(moduleDir -> file.startsWith(moduleDir.moduleDir)).findFirst();
    }

    private static Stream<Path> getAllFilesInDirectory(Path directory) {
        try {
            return Files.walk(directory)
                    .filter(Files::isRegularFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static int countLines(Path filePath) {
        try {
            int lineCount = 0;
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                while (reader.readLine() != null) {
                    lineCount++;
                }
            }
            return lineCount;
        } catch (IOException ex) {
            throw new RuntimeException("Error occur on counting lines in `%s`".formatted(filePath), ex);
        }
    }

    private static class Module {
        private final String name;
        private final String moduleDir;

        public Module(String name, String moduleDir) {
            this.name = name;
            this.moduleDir = moduleDir;
        }
    }

    private static class FileInModule {
        private final String file;
        private final Optional<Module> module;
        private final int lineOfCode;

        public FileInModule(String file, Optional<Module> module, int lineOfCode) {
            this.file = file;
            this.module = module;
            this.lineOfCode = lineOfCode;
        }
    }

    public static class ModulePartialSummary {
        private final String module;
        private final int numberOfFiles;
        private final int moduleLinesOfCode;
        private final int projectLinesOfCode;

        private ModulePartialSummary(String module, int numberOfFiles, int totalLinesOfCode, int projectLinesOfCode) {
            this.module = module;
            this.numberOfFiles = numberOfFiles;
            this.moduleLinesOfCode = totalLinesOfCode;
            this.projectLinesOfCode = projectLinesOfCode;
        }

        public static ModulePartialSummary aggregate(List<FileInModule> fileInModules, int projectLinesOfCode) {
            String module = fileInModules.get(0).module.map(it -> it.name).orElse(UNDEFINED_MODULE_NAME);
            int numberOfFiles = fileInModules.size();
            int totalLinesOfCode = fileInModules.stream().mapToInt(it -> it.lineOfCode).sum();
            return new ModulePartialSummary(module, numberOfFiles, totalLinesOfCode, projectLinesOfCode);
        }

        public String module() {
            return module;
        }

        public int numberOfFiles() {
            return numberOfFiles;
        }

        public int linesOfCode() {
            return moduleLinesOfCode;
        }

        double relativeModuleSize() {
            return 1.0 * moduleLinesOfCode / projectLinesOfCode;
        }
    }


}

