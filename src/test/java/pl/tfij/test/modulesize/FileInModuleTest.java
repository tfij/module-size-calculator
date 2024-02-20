package pl.tfij.test.modulesize;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileInModuleTest {

    @Test
    void shouldReturnEmptyDirForUndefinedModule() {
        Assertions.assertTrue(FileInModule.Module.UNDEFINED_MODULE.definedModule().isEmpty());
    }

}