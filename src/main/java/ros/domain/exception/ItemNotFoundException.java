package ros.domain.exception;

public class ItemNotFoundException extends RosDomainException {
    public ItemNotFoundException(String itemName) {
        super("O item \"" + itemName + "\" não está no pedido.");
    }
}
