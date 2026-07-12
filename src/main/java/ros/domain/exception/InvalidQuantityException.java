package ros.domain.exception;

public class InvalidQuantityException extends RosDomainException {
    public InvalidQuantityException() {
        super("A quantidade deve ser um valor positivo.");
    }
}
