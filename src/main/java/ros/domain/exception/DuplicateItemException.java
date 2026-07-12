package ros.domain.exception;

public class DuplicateItemException extends RosDomainException {
    public DuplicateItemException(String itemName) {
        super("O item \"" + itemName + "\" já existe no pedido.");
    }
}
