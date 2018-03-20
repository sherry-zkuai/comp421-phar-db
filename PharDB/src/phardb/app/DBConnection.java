package phardb.app;

import java.sql.Connection;
import java.util.UUID;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import phardb.exception.ConnectException;
/*
1. Add an employee
2. Check the inventory of a product in a certain branch store by product_id.
3. Restocking a product in a branch store. Insert a new restocking record in the restocking table, and update the inventory i.e. the "Has" table.
4. Insert a new dispense record. Trigger the update in prescription table 
5. Place an order. insert ordering record, and contains table. (parameters: product_id, amount, store_id, emplyee_id, member_id(can be null)
6. Quit

 */


public class DBConnection {
	private static Connection conn=null;
	private static Statement stmt=null;
	
	
	public static void psqlConnect() throws ConnectException{
		
		try{
			Class.forName("org.postgresql.Driver");
			conn=DriverManager.getConnection("jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421", 
					"cs421g49", "7749-comp421");
			stmt=conn.createStatement();
		}catch(Exception e){
			throw new ConnectException("Fail to Connect to Database: jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421");
		}
	}
	
	public static void psqlClose() throws SQLException{
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw e;
		}
	}
	
	//public static void
	
	
	public static void main(String args[]){
		try{
			psqlConnect();
			
			while(true){
				System.out.println("Choose one of the following options by type in option letter");
				System.out.println();
				
				Scanner sc=new Scanner(System.in);
				String opt=sc.nextLine();
				
				break;
			}
			
			psqlClose();
			
		}catch(Exception e){
			System.out.println(e.getMessage());
			System.exit(0);
		}
		UUID id=UUID.randomUUID();
		System.out.println(id.toString());
		
	}
}
