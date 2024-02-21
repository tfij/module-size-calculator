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

/**
 * The ModuleSizeCalculator class calculates the size of modules in a project.
 * It provides methods to configure and analyze modules based on their packages or directories.
 * <p>
 * This class uses the ModuleSizeCalculatorBuilder inner class to provide a fluent API for configuring modules.
 * </p>
 */
public class ModuleSizeCalculator {
    private final List<FileInModule.Module> modules;
    private final String rootDir;

    /**
     * Accept all if empty.
     * To include file without extension the collection should contains empty string
     */
    private final Set<String> includeOnlyFilesWithExtension;

    ModuleSizeCalculator(List<FileInModule.Module> modules, String rootDir, Set<String> includeOnlyFilesWithExtension) {
        this.includeOnlyFilesWithExtension = includeOnlyFilesWithExtension;
        this.modules = modules;
        this.rootDir = rootDir;
    }

    /**
     * Creates a new ModuleSizeCalculatorBuilder instance for a project located at the specified root directory.
     *
     * @param rootDir The root directory of the project.
     * @return A ModuleSizeCalculatorBuilder instance initialized with the project's root directory.
     */
    public static ModuleSizeCalculatorBuilder project(String rootDir) {
        return new ModuleSizeCalculatorBuilder(rootDir);
    }

    private Map<FileInModule.Module, ModulePartialSummary> calculate() {
        List<FileInModule> pathStream = getAllFilesInDirectory(Paths.get(rootDir)).stream()
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

    private Optional<FileInModule.Module> matchModule(Path file, List<FileInModule.Module> modules) {
        return modules.stream()
                .flatMap(it -> it.definedModule().stream())
                .filter(module -> isSubpath(file, module.moduleDir()))
                .<FileInModule.Module>map(it -> it)
                .findFirst();
    }

    private static boolean isSubpath(Path path1, Path path2) {
        if (path1.getNameCount() < path2.getNameCount()) {
            return false;
        }
        Path subpath = path1.subpath(0, path2.getNameCount());
        return subpath.equals(path2);
    }

    private static List<Path> getAllFilesInDirectory(Path directory) {
        try (Stream<Path> paths = Files.walk(directory)) {
            return paths.filter(Files::isRegularFile).toList();
        } catch (IOException ex) {
            throw new ModuleSizeCalculatorException("Error occur on scanning project in `%s` directory.".formatted(directory.toAbsolutePath()), ex);
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
            throw new ModuleSizeCalculatorException("Error occur on counting lines of `%s` file.".formatted(filePath), ex);
        }
    }

    /**
     * The ModuleSizeCalculatorBuilder class provides a fluent API for configuring modules and analyzing projects.
     * It allows users to specify modules, include file extensions, and analyze the project.
     * <p>
     * This class is typically used in conjunction with the ModuleSizeCalculator class to configure and perform analysis.
     * </p>
     */
    public static class ModuleSizeCalculatorBuilder {
        private final String rootDir;
        private final List<FileInModule.Module> modules = new ArrayList<>();
        private final Set<String> includeOnlyFilesWithExtension = new HashSet<>();

        ModuleSizeCalculatorBuilder(String rootDir) {
            this.rootDir = rootDir;
        }

        /**
         * Adds a moduleName package to the list of modules for analysis.
         *
         * @param modulePackage The package of the moduleName to be added.
         * @return The ModuleSizeCalculatorBuilder instance to allow method chaining.
         */
        public ModuleSizeCalculatorBuilder withModule(String modulePackage) {
            FileInModule.Module module = new FileInModule.DefinedModule(modulePackage, Paths.get(rootDir + "/" + modulePackage.replaceAll("\\.", "/")));
            modules.add(module);
            return this;
        }

        /**
         * Adds a directory module to the list of modules for analysis.
         *
         * @param path The path of the directory module to be added.
         * @return The ModuleSizeCalculatorBuilder instance to allow method chaining.
         */
        public ModuleSizeCalculatorBuilder withDirModule(String path) {
            modules.add(new FileInModule.DefinedModule(path, Paths.get(rootDir + "/" + path)));
            return this;
        }

        /**
         * Set the set of file extensions that should be included in the analysis.
         * If nothing is set, all files will be analyzed. If you want to analyze files
         * without extensions, add an empty string to the list.
         *
         * @param fileExtension Set the set of file extensions that should be included in the analysis
         * @return The instance of the builder class on which the method was called.
         */
        public ModuleSizeCalculatorBuilder include(String... fileExtension) {
            includeOnlyFilesWithExtension.addAll(Arrays.asList(fileExtension));
            return this;
        }

        /**
         * Analyzes the modules using the provided configuration and returns a summary of the project.
         *
         * @return A ProjectSummary instance containing the summary of the analyzed project.
         */
        public ProjectSummary analyze() {
            ModuleSizeCalculator moduleSizeCalculator = new ModuleSizeCalculator(modules, rootDir, includeOnlyFilesWithExtension);
            Map<FileInModule.Module, ModulePartialSummary> analyzedModules = moduleSizeCalculator.calculate();
            return new ProjectSummary(modules, analyzedModules);
        }
    }
}

