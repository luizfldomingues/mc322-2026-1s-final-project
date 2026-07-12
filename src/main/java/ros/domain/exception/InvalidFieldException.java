package ros.domain.exception;

public class InvalidFieldException extends RosDomainException {
    public InvalidFieldException(String fieldName) {
        super("O campo \"" + fieldName + "\" não pode ser vazio.");
    }
}
