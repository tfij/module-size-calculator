package pl.tfij.test.modulesize;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleSizeCalculator {
    public static final String UNDEFINED_MODULE_NAME = "undefined";
    private final List<Module> modules;
    private final String rootDir;

    /**
     * Accept all if empty.
     * To include file without extension the collection should contains empty string
     */
    private final Set<String> includeOnlyFilesWithExtension;

    public ModuleSizeCalculator(List<String> modules, String rootDir, Set<String> includeOnlyFilesWithExtension) {
        this.includeOnlyFilesWithExtension = includeOnlyFilesWithExtension;
        this.modules = modules.stream()
                .map(it -> new Module(it, rootDir + "/" + it.replaceAll("\\.", "/")))
                .toList();
        this.rootDir = rootDir;
    }

    public static ModuleSizeCalculatorBuilder project(String rootDir) {
        return new ModuleSizeCalculatorBuilder(rootDir);
    }

    private Map<String, ModulePartialSummary> calculate() {
        List<FileInModule> pathStream = getAllFilesInDirectory(Paths.get(rootDir))
                .filter(this::includeFile)
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

    private boolean includeFile(Path path) {
        if (includeOnlyFilesWithExtension.isEmpty()) {
            return true;
        }
        return includeOnlyFilesWithExtension.contains(getFileExtension(path));
    }

    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
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

    public static class ModuleSizeCalculatorBuilder {
        private final String rootDir;
        private final ArrayList<String> modules = new ArrayList<>();
        private final Set<String> includeOnlyFilesWithExtension = new HashSet<>();

        public ModuleSizeCalculatorBuilder(String rootDir) {
            this.rootDir = rootDir;
        }

        public ModuleSizeCalculatorBuilder withModule(String modulePackage) {
            modules.add(modulePackage);
            return this;
        }

        /**
         * Set the set of file extensions that should be included in the analysis.
         * If nothing is set, all files will be analyzed. If you want to analyze files
         * without extensions, add an empty string to the list.
         * @param fileExtension Set the set of file extensions that should be included in the analysis
         * @return The instance of the builder class on which the method was called.
         */
        public ModuleSizeCalculatorBuilder include(String... fileExtension) {
            includeOnlyFilesWithExtension.addAll(Arrays.asList(fileExtension));
            return this;
        }

        public ProjectSummary analyze() {
            ModuleSizeCalculator moduleSizeCalculator = new ModuleSizeCalculator(modules, rootDir, includeOnlyFilesWithExtension);
            Map<String, ModulePartialSummary> analyzedModules = moduleSizeCalculator.calculate();
            return new ProjectSummary(modules, analyzedModules);
        }
    }
}

