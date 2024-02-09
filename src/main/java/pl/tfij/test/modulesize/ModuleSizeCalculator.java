package pl.tfij.test.modulesize;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public Map<String, ModulePartialSummary> calculate() {
        List<FileInModule> pathStream = getAllFilesInDirectory(Paths.get(rootDir))
                .filter(it -> it.getFileName().toString().endsWith(".java"))
                .map(it -> new FileInModule(
                        it.toString(),
                        matchModule(it, modules),
                        countLines(it)))
                .toList();
        int projectLinesOfCode = pathStream.stream().mapToInt(it -> it.lineOfCode()).sum();
        return pathStream.stream()
                .collect(Collectors.groupingBy(it -> it.module()))
                .values()
                .stream()
                .map(it -> ModulePartialSummary.aggregate(it, projectLinesOfCode))
                .collect(Collectors.toMap(it -> it.module(), it -> it));
    }


    private Optional<Module> matchModule(Path file, List<Module> modules) {
        return modules.stream().filter(moduleDir -> file.startsWith(moduleDir.moduleDir())).findFirst();
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


}

