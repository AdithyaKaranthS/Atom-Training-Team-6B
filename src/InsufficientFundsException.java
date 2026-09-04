/**
 * Signals that a withdrawal is larger than the account's available balance.
 *
 * This is an unchecked exception because callers can handle the expected
 * validation failure without being forced to declare it in every method signature.
 */
public class InsufficientFundsException extends RuntimeException {

    /**
     * Creates an exception with a message describing the failed withdrawal.
     *
     * @param message details about the available and requested amounts
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}