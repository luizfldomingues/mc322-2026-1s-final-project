package ros.domain.exception;

public class ItemNotAvailableException extends RosDomainException {
    public ItemNotAvailableException(String itemName) {
        super("O item \"" + itemName + "\" não está disponível no momento.");
    }
}
