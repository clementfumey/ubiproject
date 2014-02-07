package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Vector;

import database.Database;

public class Database_Functions {
	
	private Connection connection;
	
	public Database_Functions() {
		Database db = new Database();
		db.connexionBD();
		connection = db.getConnexion();
	}
	
	
	/**
	 * Add an user to the database.
	 * @param id_user
	 * @param amount
	 * @param firstname
	 * @param lastname
	 * @return true is the operation is successful and false otherwise
	 */
	public boolean addUser(String id_user, float amount, String firstname, String lastname){
			try {
				Statement s = connection.createStatement();
				int result = 0;
				result = s.executeUpdate("INSERT INTO Users VALUES('"+id_user+"',"+amount+",'"+firstname+"','"+lastname+"')");
				if (result > 0) return true; else return false;
			}
			catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
	}
	
	public ArrayList<String[]> getUsers() {
		try {
			ArrayList<String[]> prov = new ArrayList<String[]>();
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM users");
			while (rs.next()) {
				prov.add(new String[]{rs.getString("id_user").trim(),rs.getString("amount").trim(), rs.getString("firstname").trim(), rs.getString("lastname").trim()});
			}
			return prov;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Delete an user from the database
	 * @param id_user
	 * @return true if the deletion is successful and false otherwise.
	 */
	public boolean deleteUser (String id_user){
		try {
			System.out.println(id_user);
			Statement s = connection.createStatement();
			int result = 0;
			result = s.executeUpdate("DELETE FROM Users WHERE id_user='"+id_user+"'");
			if (result > 0) return true; else return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Return an user from its ID
	 * @param id_user
	 * @return A vector in which there is : amount, firstname, lastname.
	 */
	public Vector<String> getUserFromRfid(String id_user){
		Vector<String> user = new Vector<String>();
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM Users WHERE id_user='"+id_user+"'");
			rs.next();
			user.add(rs.getString("amount").trim());
			user.add(rs.getString("firstname").trim());
			user.add(rs.getString("lastname").trim());
		} catch (SQLException e) {
			e.printStackTrace();
			return user;
		}
		return user;
	}
	
	/**
	 * Add the given amount on an user account.
	 * @param id_user
	 * @param money
	 * @return true is the operation is successful and false otherwise.
	 */
	public boolean addAmount (String id_user, float money){
		try {
			Statement s = connection.createStatement();
			int result = 0;
			result = s.executeUpdate("UPDATE Users SET amount="+money+" WHERE id_user='"+id_user+"'");
			if (result > 0) return true; else return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Substract the given amount to the user account
	 * @param id_user
	 * @param money
	 * @return true if the operation is successful and the new amount is above 0, false otherwise.
	 */
	public boolean soustractAmount (String id_user, float money){
		try {
			Statement s = connection.createStatement();
			int result = 0;
			float amount = this.getAmount(id_user);
			if (amount - money < 0) return false; //Check if there is enough money on the account
			float new_amount = amount - money;
			result = s.executeUpdate("UPDATE Users SET amount="+new_amount+" WHERE id_user='"+id_user+"'");
			if (result > 0) return true; else return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Give the amount on a user account.
	 * @param id_user
	 * @return the amount if all is ok and -1 if an exception is caught.
	 */
	public float getAmount (String id_user){
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT amount FROM Users WHERE id_user='"+id_user+"'");
			rs.next();
			System.out.println(rs.getFloat("amount"));
			return rs.getFloat("amount");
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Gives the price of an article
	 * @param id_drink
	 * @return the price if all the ok and -1 otherwise.
	 */
	public float getPrice (int id_drink){
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT price FROM Provision WHERE id_user='"+id_drink+"'");
			rs.next();
			return rs.getFloat("price");
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	/**
	 * Set the price of an article
	 * @param id_drink
	 * @return boolean
	 */
	public boolean setPrice (int id_drink, float new_price){
		int result =0;
		try {
			Statement s = connection.createStatement();
			result = s.executeUpdate("UPDATE Provision SET price="+new_price+" WHERE id_drink='"+id_drink+"'");
			if (result > 0) return true; else return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Gives the id of an article
	 * @param name
	 * @return the id if all the ok and -1 otherwise.
	 */
	public int getProvisionId (String name){
		try {
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT id_drink FROM Provision WHERE name='"+name+"'");
			rs.next();
			return rs.getInt("id_drink");
		}
		catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}
	/**
	 * Create drinks available.
	 * @return Boolean
	 */
	public boolean newProvision(int id, String name, float price) {
		try {
			Statement s = connection.createStatement();
			int result = 0;
			result = s.executeUpdate("INSERT INTO Provision VALUES("+id+",'"+name+"',"+price+")");
			if (result > 0) return true; else return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * List all drinks available.
	 * @return An array list of table {name, price}, null if error.
	 */
	public ArrayList<String[]> getProvision (){
		try {
			ArrayList<String[]> prov = new ArrayList<String[]>();
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM Provision");
			while (rs.next()) {
				prov.add(new String[]{rs.getString("name").trim(), rs.getString("price").trim()});
			}
			return prov;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Create a new entry in the Sales table
	 * @param time
	 * @param terminal
	 * @param id_provision
	 * @param id_user
	 * @return true if the operation is successful and false otherwise.
	 */
	public boolean addSale (Timestamp time, int terminal, int id_provision, int quantity , String id_user){
		try {
			Statement s = connection.createStatement();
			int result = 0;
			result = s.executeUpdate("INSERT INTO Sales(time,terminal,id_drink,quantity,id_user) VALUES('"+time+"',"+terminal+","+id_provision+","+quantity+",'"+id_user+"')");
			if (result > 0) return true; else return false;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	public ArrayList<String[]> getSales() {
		try {
			ArrayList<String[]> prov = new ArrayList<String[]>();
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM sales");
			while (rs.next()) {
				prov.add(new String[]{rs.getString("id_sale").trim(),rs.getString("time").trim(), rs.getString("terminal").trim(), rs.getString("id_drink").trim(),rs.getString("quantity").trim(),rs.getString("id_user").trim()});
				System.out.println(prov.toString());
			}
			return prov;
		}
		catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}






}
