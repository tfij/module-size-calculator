# Module Size Calculator

The Module Size Calculator is a Java library designed for analyzing the size of modules within a project based on lines of code (LOC).
It provides functionalities to calculate and visualize the distribution of code among different modules, 
helping developers understand the codebase's structure and identify potential areas for optimization or refactoring.

In the context of a library, a module is simply a package/directory with all its contents (recursive search).
This definition is flexible and allows the library to be used in most standard projects.

## Features

* Analyze project modules recursively.
* Include specific file extensions for analysis (e.g., Java, CSV).
* Generate Mermaid pie charts to visualize module sizes.
* Verify module sizes against predefined thresholds.
* Handle exceptions gracefully for invalid inputs or file operations.

## Usage

### Sample jUnit test

Below is an example jUnit test verifying the size of modules using the library:

```java
@Test
void shouldVerifyModulesSize() {
    ProjectSummary projectSummary = ModuleSizeCalculator.project("src/main/java")
            .withModule("com.example.module1")
            .withModule("com.example.module2")
            .withModule("com.example.module3")
            .withModule("com.example.commons")
            .analyze()
            .verifyEachModuleRelativeSizeIsSmallerThan(0.3)
            .verifyModuleRelativeSizeIsSmallerThan("com.example.commons", 0.1)
            .verifyNoEmptyModules()
            .verifyUndefinedModuleNumberOfFilesIsSmallerThan(1)
            .saveMermaidPieChart(Path.of("target/modules-size.mermaid"));
}
```

### Analyzing Project Modules

To analyze project modules, use the `ModuleSizeCalculator` class.
You can specify the project directory and include specific file extensions if needed.

```java
ProjectSummary projectSummary = ModuleSizeCalculator.project("src/main/java")
    .withModule("com.example.module1")
    .withModule("com.example.module2")
    // Add more modules as needed
    .include("java", "csv") // Include specific file extensions
    .analyze();
```

### Verifying Module Sizes

The library allows to perform various verifications on the module sizes

#### Verifying Relative Size of Each Module

Check if the relative size of each module is smaller than a specified threshold (relative to the total project size).
If, for example, the project has 20 modules and 80% of the code is in one of them, we cannot talk about modularization because almost all the code is in one place.
This check ensures that none of the modules is larger than a certain threshold.

```java
projectSummary.verifyEachModuleRelativeSizeIsSmallerThan(0.3);
```

#### Verifying Relative Size of a Specific Module

Verify if the relative size of a specific module is smaller than a specified threshold (relative to the total project size).
An example use would be to ensure that a specific module, e.g. commons, is small.

```java
projectSummary.verifyModuleRelativeSizeIsSmallerThan("com.example.commons", 0.1);
```

#### Checking for Empty Modules

Ensure that none of the modules are empty.
It may be useful, for example, to ensure that tests are up-to-date when a module is removed but the definition in the test was forgotten to be corrected.

```java
projectSummary.verifyNoEmptyModules();
```

#### Verifying Number of Files in Undefined Modules

Check if the number of files in modules without names (undefined) is smaller than a specified limit.
An example of such a file could be a class with a `main()` method that is in the main project directory, along with the packages defining all modules.

```
\ com
  \ example
    | module1
    | module2
    | module2
    \ Main.java
```

The method can also catch situations when a new module is added to the project which is not added to the module definitions in the test.
In such a case, files from the new module will be classified as belonging to the undefined module and the assertion will not pass.
This allows you to ensure that the test is up-to-date.

```java
projectSummary.verifyUndefinedModuleNumberOfFilesIsSmallerThan(1);
```

### Generating Mermaid Pie Chart

You can generate [Mermaid pie charts](https://mermaid.js.org/syntax/pie.html) to visualize module sizes using the saveMermaidPieChart() method.

```java
projectSummary.saveMermaidPieChart(Path.of("target/modules-size.mermaid"));
```

## Installation

You can include this library in your project using Maven or Gradle.
Add the following dependency to your pom.xml file for Maven:

```xml
<dependency>
    <groupId>pl.tfij</groupId>
    <artifactId>module-size-calculator</artifactId>
    <version>1.0.0</version>
</dependency>
```

For Gradle, add the following to your build.gradle file:

```groovy
implementation 'pl.tfij:module-size-calculator:1.0.0'
```

## Contributions

Contributions to the library are welcome.
If you encounter any bugs or have suggestions for improvements, please submit an issue or pull request on the GitHub repository.
