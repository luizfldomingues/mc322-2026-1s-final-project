package ros.domain.model.Auxiliary;

public interface HasherInterface {
    public String encode(String string);
    public boolean match(String hashedString, String unhashedString); 
}
