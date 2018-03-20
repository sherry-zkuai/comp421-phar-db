package phardb.app;

import java.sql.Connection;
import java.util.UUID;
import java.sql.DriverManager;
import java.util.Scanner;

import phardb.exception.ConnectException;

public class DBConnection {
	private static Connection conn=null;
	
	public static void psqlConnect() throws ConnectException{
		
		try{
			Class.forName("org.postgresql.Driver");
			conn=DriverManager.getConnection("jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421", 
					"cs421g49", "7749-comp421");
		}catch(Exception e){
			throw new ConnectException("Fail to Connect to Database: jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421");
		}
	}
	
	//public static void
	
	
	public static void main(String args[]){
//		try{
//			psqlConnect();
//			
//			while(true){
//				System.out.println("Choose one of the following options by type in the words stated in ()");
//				System.out.println();
//				
//				Scanner sc=new Scanner(System.in);
//				
//			}
//			
//		}catch(Exception e){
//			System.out.println(e.getMessage());
//			System.exit(0);
//		}
		UUID id=UUID.randomUUID();
		System.out.println(id.toString());
		
	}
}
