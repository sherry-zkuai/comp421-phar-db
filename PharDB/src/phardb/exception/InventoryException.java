package phardb.exception;

public class InventoryException extends Exception{
	public InventoryException(){}
	
	public InventoryException(String pMessage){
		super(pMessage);
	}
	
	public InventoryException(Throwable pCause){
		super(pCause);
	}
	
	public InventoryException(String pMessage, Throwable pCause){
		super(pMessage,pCause);
	}
}
