package phardb.app;

import java.sql.Connection;
import java.sql.DriverManager;

import phardb.exception.ConnectException;

public class DBConnection {
	public static void psqlConnect() throws ConnectException{
		Connection conn=null;
		try{
			Class.forName("org.postgresql.Driver");
			conn=DriverManager.getConnection("jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421", 
					"cs421g49", "7749-comp421");
		}catch(Exception e){
			throw new ConnectException("Fail to Connect to Database: jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421");
		}
	}
	public static void main(String args[]){
		//psqlConnect();
	}
}
