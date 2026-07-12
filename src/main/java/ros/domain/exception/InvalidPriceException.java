package ros.domain.exception;

public class InvalidPriceException extends RosDomainException {
    public InvalidPriceException() {
        super("O preço deve ser um valor positivo.");
    }
}
