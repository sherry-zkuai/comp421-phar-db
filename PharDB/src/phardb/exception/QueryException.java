package phardb.exception;

public class QueryException extends Exception{
	public QueryException(){
		
	}
	
	public QueryException(String pMessage){
		super(pMessage);
	}
	
	public QueryException(Throwable pCause){
		super(pCause);
	}
	
	public QueryException(String pMessage, Throwable pCause){
		super(pMessage,pCause);
	}
}
