package phardb.app;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.UUID;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

import phardb.exception.*;
/*
1. Add an employee
2. Check the inventory of a product in a certain branch store by product_id.
3. Re-stocking a product in a branch store. Insert a new re-stocking record in the re-stocking table, and update the inventory i.e. the "Has" table.
4. Insert a new dispense record. Trigger the update in prescription table 
5. Place an order. insert ordering record, and contains table. (parameters: product_id, amount, store_id, emplyee_id, member_id(can be null)
6. Quit

 */

/**
 * 
 * @author Group 49
 *
 */

public class DBConnection {
	
	private static Connection conn=null;
	private static Statement stmt=null;
	static Scanner sc=new Scanner(System.in);
	
	public static void psqlConnect() throws ConnectException{
		
		try{
			Class.forName("org.postgresql.Driver");
			conn=DriverManager.getConnection("jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421", 
					"username", "password");
			stmt=conn.createStatement();
		}catch(Exception e){
			throw new ConnectException("Fail to Connect to Database: jdbc:postgresql://comp421.cs.mcgill.ca:5432/cs421");
		}
	}
	
	public static void psqlClose() throws ConnectException{
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			throw new ConnectException("Fail to Close Connection");
		}
	}
	
	/**Add a new employee;
	 * @author Zirui Kuai
	 * @throws QueryException
	 */
	public static void promptA(){
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
			//throw new QueryException("Query Exception: "+sql);
			System.out.println("msg: "+e.getMessage()+
					"code: "+e.getErrorCode()+
					"state: "+e.getSQLState());
		}
	}
	
	/**Check inventory of a product in a store or accross all stores.
	 * @author Zirui Kuai
	 */
	public static void promptB(){
		System.out.print("Enter the Product ID: ");
		String pId=sc.nextLine();
		System.out.print("Enter the Store ID (Leave it empty if want to search across all stores): ");
		String storeId=sc.nextLine();
		
		String sql="select * from has where product_id='"+pId+"'";
		if(!(storeId==null||storeId.equals(""))){
			sql+=" and store_id='"+storeId+"'";
		}
		
		System.out.println("Store			|Product			|Quantity");
		System.out.println("------------------------------------------------------------");
		
		ResultSet rs;
		try {
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String store=rs.getString("store_id");
				String product=rs.getString("product_id");
				int quantity=rs.getInt("hquantity");
				System.out.println(store+"			|"+product+"			|"+quantity);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fail");
			System.out.println("msg: "+e.getMessage()+
					"code: "+e.getErrorCode()+
					"state: "+e.getSQLState());
		}
		
	}
	
	/**Re-stock a product at a store; Update on table Restocking
	 * @author Zirui Kuai
	 */
	public static void promptC(){
		System.out.print("Entern the Store ID: ");
		String storeId=sc.nextLine();
		System.out.print("Entern the Product ID: ");
		String pId=sc.nextLine();
		System.out.print("Enter the restocked quantity: ");
		String quantity=sc.nextLine();
		System.out.print("Enter the stocking price: ");
		String price=sc.nextLine();
		
		String rId=UUID.randomUUID().toString();
		String sql="insert into restocking values ('"+rId+"',"+price+","+quantity+",now(),'"+storeId+"','"+pId+"')";
		try {
			stmt.executeUpdate(sql);
			System.out.println("Update Restocking successfully");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fail");
			System.out.println("msg: "+e.getMessage()+
					"code: "+e.getErrorCode()+
					"state: "+e.getSQLState());
		}
	}
	
	/**Dispense prescription drugs from a prescription at a store. Update on tables Dispenses and Ordering.
	 * @author Zirui Kuai
	 */
	public static void promptD(){
		System.out.print("Store ID: ");
		String storeId=sc.nextLine();
		System.out.print("Prescription ID: ");
		String presId=sc.nextLine();
		System.out.print("Pharmacist ID: ");
		String pharId=sc.nextLine();
		System.out.print("Prescription Drug ID: ");
		String drugId=sc.nextLine();
		System.out.print("Drug Amount: ");
		String amount=sc.nextLine();
		BigDecimal total=new BigDecimal(0);
		
		try{
			String psql="select price from product,prescription_drug where product.product_id=prescription_drug.product_id and prescription_drug.product_id='"+drugId+"'";
			ResultSet rs=stmt.executeQuery(psql);
			while(rs.next()){
				BigDecimal price=rs.getBigDecimal(1);
				price=price.multiply(new BigDecimal(amount));
				total=total.add(price);
			}
			rs.close();
		}catch(SQLException e){
			System.out.println("Fail");
			System.out.println("msg: "+e.getMessage()+
					"code: "+e.getErrorCode()+
					"state: "+e.getSQLState());
		}
		
		String oId=UUID.randomUUID().toString();
		String sql1="select count(*) from activeprescription where prescription_id='"+presId+"'";
		String sql2="insert into ordering values ('"+oId+"',"+total+",now(),'"+storeId+"','"+pharId+"')";
		String sql3="insert into dispenses values ('"+pharId+"','"+drugId+"','"+presId+"','"+oId+"',now(),"+amount+")";
		try {
			int num=0;
			ResultSet rs=stmt.executeQuery(sql1);
			while(rs.next()){
				num=rs.getInt(1);
			}
			rs.close();
			if(num==0){
				System.out.println("Dispense disallowed since no refill times remain");
				return;
			}else{
				int count=0;
				ResultSet rs1=stmt.executeQuery("select count(*) from employee where employee_id='"+pharId+"' and not license_no is null and store_id='"+storeId+"'");
				while(rs1.next()){
					count=rs1.getInt(1);
				}
				rs1.close();
				if(count==0){
					System.out.println("No such pharmacist exist in this store");
					return;
				}else{
					stmt.executeUpdate(sql2);
					System.out.println("Update Ordering successfully");
					stmt.executeUpdate(sql3);
					System.out.println("Update Dispenses successfully");
				}
			}
		} catch (SQLException e) {
			System.out.println("Fail");
			e.printStackTrace();
			System.out.println("msg: "+e.getMessage()+
					"code: "+e.getErrorCode()+
					"state: "+e.getSQLState());
		}
	}
	
	/**Place an order at a store. Update on tables Contains and Ordering.
	 * @author Zirui Kuai
	 * @throws QueryException
	 */
	public static void promptE(){
		String storeId=null;
		String eId=null;
		String prodIn=null;
		HashMap<String,Integer> products=new HashMap<String,Integer>();
		BigDecimal total=new BigDecimal(0);
		String memberId=null;
		
		System.out.print("Enter the store ID: ");
		storeId=sc.nextLine();
		
		System.out.print("Enter the employee ID: ");
		eId=sc.nextLine();
		
		System.out.println("Entern the product quantity (In the form of product_id,quantity\n for each product; if no more products, enter :q)");
		while(sc.hasNextLine()){
			prodIn=sc.nextLine();
			if(prodIn.equals(":q")){
				break;
			}else{
				String pId=prodIn.split(",")[0];
				Integer quantity=Integer.parseInt(prodIn.split(",")[1]);
				//System.out.println(quantity);
				products.put(pId, quantity);
			}
		}
		
		System.out.print("Enter the membership no. (if not a member, leave it blank): ");
		memberId=sc.nextLine();
		
		int count=0;
		try{
			ResultSet rs1=stmt.executeQuery("select count(*) from employee where employee_id='"+eId+"' and license_no is null and store_id='"+storeId+"'");
			while(rs1.next()){
				count=rs1.getInt(1);
			}
			rs1.close();
		}catch(SQLException e){
			System.out.println("Fail");
			System.out.println("msg: "+e.getMessage()+
					"code: "+e.getErrorCode()+
					"state: "+e.getSQLState());
		}
		if(count==0){
			System.out.println("No such employee exist in this store");
			return;
		}else{
			String oId=UUID.randomUUID().toString();
			
			//1.Check if enough inventory
			//2.Calculate the total price
			for(Map.Entry<String, Integer> entry:products.entrySet()){
				String p=entry.getKey();
				Integer q=entry.getValue();
				String sql2="select price from product,other_product where product.product_id=other_product.product_id and other_product.product_id='"+p+"'";
				String sql3="select hquantity from has where store_id='"+storeId+"' and product_id='"+p+"'";
				try{
					int hq=0;
					ResultSet rs2=stmt.executeQuery(sql3);
					while(rs2.next()){
						hq=rs2.getInt(1);
					}
					rs2.close();
					if(hq<q){
						throw new InventoryException("Store "+storeId+" does not have enough inventory for product "+p+".");
					}
				}catch(SQLException e){
					System.out.println("msg: "+e.getMessage()+
							"code: "+e.getErrorCode()+
							"state: "+e.getSQLState());
					return;
				}catch(InventoryException e){
					System.out.println(e.getMessage());
					return;
				}
				try{
					ResultSet rs=stmt.executeQuery(sql2);
					while(rs.next()){
						BigDecimal price=rs.getBigDecimal("price");
						price=price.multiply(new BigDecimal(q));
						total=total.add(price);
					}
					rs.close();
				}catch(SQLException e){
					System.out.println("Fail");
					//e.printStackTrace();
					//throw new QueryException("Query Exception: "+sql2);
					System.out.println("msg: "+e.getMessage()+
							"code: "+e.getErrorCode()+
							"state: "+e.getSQLState());
					return;
				}
				
			}
			//System.out.println(total);
			
			//Insert into Ordering table
			String sql="insert into ordering values('"+oId+"',"+total+",now(),'"+storeId+"','"+eId+"')";
			try {
				stmt.executeUpdate(sql);
				System.out.println("Update Ordering table successfully");
			} catch (SQLException e) {
				System.out.println("Fail");
				//throw new QueryException("Query Exception: "+sql);
				System.out.println("msg: "+e.getMessage()+
						"code: "+e.getErrorCode()+
						"state: "+e.getSQLState());
				return;
			}
			
			//Insert into Contains table
			for(Map.Entry<String, Integer> entry:products.entrySet()){
				String p=entry.getKey();
				Integer q=entry.getValue();
				String sql1="insert into contains values ('"+oId+"','"+p+"',"+q+")";
				try {
					stmt.executeUpdate(sql1);
				} catch (SQLException e) {
					System.out.println("Fail");
					//e.printStackTrace();
					//throw new QueryException("Query Exception: "+sql1);
					System.out.println("msg: "+e.getMessage()+
							"code: "+e.getErrorCode()+
							"state: "+e.getSQLState());
					return;
				}
			}
			System.out.println("Update Contains table successfully");
			
			if(!memberId.isEmpty()){
				int mCount=0;
				try{
					ResultSet rs3=stmt.executeQuery("select count(*) from member where member_id='"+memberId+"'");
					while(rs3.next()){
						mCount=rs3.getInt(1);
					}
					rs3.close();
				}catch (SQLException e){
					System.out.println("msg: "+e.getMessage()+
							"code: "+e.getErrorCode()+
							"state: "+e.getSQLState());
					return;
				}
				if(mCount==0){
					System.out.println("No such member exists");
				}else{
					int points=total.toBigInteger().intValue();
					//ystem.out.println(points);
					try {
						stmt.executeUpdate("insert into gain_points_from values ('"+memberId+"','"+oId+"',"+points+")");
						System.out.println("Update Member Successfully");
					} catch (SQLException e) {
						System.out.println("msg: "+e.getMessage()+
								"code: "+e.getErrorCode()+
								"state: "+e.getSQLState());
						return;
					}
				}
			}
		}
		
	}
	
	
	/**Check prescription records by patient name.
	 * @author Zhengbo Wang
	 */
	public static void promptF(){
		System.out.print("Enter the patient's name: ");
		String pName=sc.nextLine();
		
		String sql="select * from prescription where patient='"+pName+"'";
		
		System.out.println("Prescription ID\t|Doctor\t\t\t|Patient\t\t|Amount\t|Refills Remain\t|Instruction");
		System.out.println("------------------------------------------------------------");
		
		ResultSet rs;
		try {
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				String prescription_id=rs.getString("prescription_id");
				String doctor=rs.getString("doctor");
				String patient=rs.getString("patient");
				String instruction= rs.getString("instruction");
				int amount = rs.getInt("amount");
				int refill_remain  = rs.getInt("refill_remain");
				System.out.println(prescription_id+"\t\t|"+doctor+"\t\t|"+patient+"\t\t|"+amount+"\t|"+refill_remain+"\t\t|"+instruction);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Fail");
			System.out.println("msg: "+e.getMessage()+
					"code: "+e.getErrorCode()+
					"state: "+e.getSQLState());
		}
		
	}
	
	
	public static void main(String args[]){
		try{
			psqlConnect();
			
			while(true){
				System.out.println("Choose one of the following options by type in option letter");
				System.out.println("A. Add a new employee to a store");
				System.out.println("B. Check the inventory of a product in a store or across all stores");
				System.out.println("C. Re-stock a product in a store");
				System.out.println("D. Dispense drug from a prescription");
				System.out.println("E. Place an order at a store");
				System.out.println("F. Check prescription records by patient name");
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
				}else if(opt.equals("F")){
					promptF();
				}else if(opt.equals("Q")){
					System.out.println("Successfully quit");
					psqlClose();
					System.exit(0);
					break;
				}else{
					System.out.println("Invalid Input. Please try again.");
				}
				
				System.out.println();
				
			}
			
			//psqlClose();
			
		}catch(ConnectException e){
			//e.printStackTrace();
			System.out.println("msg: "+e.getMessage());
			System.exit(1);
		}finally{
			try {
				psqlClose();
			} catch (ConnectException e) {
				//e.printStackTrace();
				System.out.println("msg: "+e.getMessage());
				System.exit(1);
			}
		}

	}
}
