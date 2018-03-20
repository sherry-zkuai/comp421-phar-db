package phardb.app;

import java.sql.Connection;
import java.util.UUID;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import phardb.exception.*;
/*
1. Add an employee
2. Check the inventory of a product in a certain branch store by product_id.
3. Re-stocking a product in a branch store. Insert a new re-stocking record in the re-stocking table, and update the inventory i.e. the "Has" table.
4. Insert a new dispense record. Trigger the update in prescription table 
5. Place an order. insert ordering record, and contains table. (parameters: product_id, amount, store_id, emplyee_id, member_id(can be null)
6. Quit

 */


public class DBConnection {
	
	private static Connection conn=null;
	private static Statement stmt=null;
	static Scanner sc=new Scanner(System.in);
	
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
	
	/**Add a new employee;
	 * @author Zirui Kuai
	 * @throws QueryException
	 */
	public static void promptA() throws QueryException{
		String eName=null;
		String storeId=null;
		String pharId=null;
		
		System.out.print("Enter the Employee Name: ");
		if(sc.hasNextLine()){
			eName=sc.nextLine();
		}
		System.out.print("Enter the store ID: ");
		if(sc.hasNextLine()){
			storeId=sc.nextLine();
		}
		System.out.print("Enter the license no. (if not a pharmacist, press 'return'): ");
		if(sc.hasNextLine()){
			pharId=sc.nextLine();
		}
		//System.out.println(eName+" "+storeId+" "+pharId);
		//sc.close();
		
		String eId=UUID.randomUUID().toString();
		String sql="insert into employee values ('"+eId+"','"+eName+"','"+storeId+"','"+pharId+"')";
		try {
			stmt.executeUpdate(sql);
			System.out.println("Success");
		} catch (SQLException e) {
			System.out.println("Fail");
			throw new QueryException("Query Exception: "+sql);
		}
	}
	
	public static void promptB(){
		
	}
	
	public static void promptC(){
		
	}
	
	public static void promptD(){
		
	}
	
	public static void promptE(){
		
	}
	
	
	public static void main(String args[]){
		try{
			psqlConnect();
			
			while(true){
				System.out.println("Choose one of the following options by type in option letter");
				System.out.println("A. Add a new employee to a store");
				System.out.println("B. Check the inventory of a product in a store");
				System.out.println("C. Re-stock a product in a store");
				System.out.println("D. Dispense drug from a prescription");
				System.out.println("E. Place an order at a store");
				System.out.println("Q. Quit");
				
				
				String opt=sc.nextLine();
				//sc.close();
				
				if(opt.equals("A")){
					promptA();
				}else if(opt.equals("B")){
					promptB();
				}else if(opt.equals("C")){
					promptC();
				}else if(opt.equals("D")){
					promptD();
				}else if(opt.equals("E")){
					promptE();
				}else if(opt.equals("Q")){
					System.out.println("Successfully quit");
					System.exit(0);
					break;
				}else{
					System.out.println("Invalid Input. Please try again.");
				}
				
				System.out.println();
				
			}
			
			psqlClose();
			
		}catch(Exception e){
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.exit(0);
		}

	}
}
