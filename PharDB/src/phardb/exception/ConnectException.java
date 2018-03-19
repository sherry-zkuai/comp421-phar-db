package phardb.exception;

public class ConnectException extends Exception{
	public ConnectException(){}
	
	public ConnectException(String pMessage){
		super(pMessage);
	}
	
	public ConnectException(Throwable pCause){
		super(pCause);
	}
	
	public ConnectException(String pMessage,Throwable pCause){
		super(pMessage,pCause);
	}
}
