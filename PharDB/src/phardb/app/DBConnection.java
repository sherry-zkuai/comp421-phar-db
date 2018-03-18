package phardb.app;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	public static void psqlConnect(){
		Connection conn=null;
		try{
			Class.forName("org.postgresql.Driver");
			conn=DriverManager.getConnection("jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421", 
					"cs421g49", "7749-comp421");
		}catch(Exception e){
			e.printStackTrace();
			System.err.println(e.getClass().getName()+": "+e.getMessage());
	        System.exit(0);
		}
		System.out.println("successfull");
	}
	public static void main(String args[]){
		psqlConnect();
	}
}
