package pl.tfij.test.modulesize;

import java.nio.file.Path;
import java.util.Optional;

record FileInModule(String file, Optional<Module> module, int lineOfCode) {

    sealed interface Module permits DefinedModule, UndefinedModule {

        String name();
        Optional<DefinedModule> definedModule();

        Module UNDEFINED_MODULE = new UndefinedModule();
    }

    private static final class UndefinedModule implements Module {
        @Override
        public String name() {
            return "undefined";
        }

        @Override
        public Optional<DefinedModule> definedModule() {
            return Optional.empty();
        }
    }

    record DefinedModule(String name, Path moduleDir) implements Module {
        @Override
        public Optional<DefinedModule> definedModule() {
            return Optional.of(this);
        }
    }
}
