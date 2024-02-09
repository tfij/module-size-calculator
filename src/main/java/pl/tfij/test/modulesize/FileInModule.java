package pl.tfij.test.modulesize;

import java.util.Optional;

record FileInModule(String file, Optional<Module> module, int lineOfCode) {
}
