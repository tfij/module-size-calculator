package pl.tfij.test.modulesize;

/**
 * The ModuleSizeCalculatorException class represents an exception that can occur during the calculation of module sizes.
 */
public class ModuleSizeCalculatorException extends RuntimeException {
    ModuleSizeCalculatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
