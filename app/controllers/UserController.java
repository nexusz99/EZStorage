package controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import exception.PasswordNotCorrectException;
import exception.UserNotExistedException;
import Model.User;
import play.db.DB;
import play.Logger;

public class UserController {
	
	private DataSource ds = DB.getDataSource();
	
	public boolean signup(User u) throws SQLException
	{
		boolean ret = false;
		String sql = "insert into ezusers (username, passwd, firstname,"
		             + " lastname) values (?,?,?,?)";
		String passwordhash = md5password(u.getPasswd());
		
		try
		{
			Connection con = ds.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, u.getUsername());
			ps.setString(2, passwordhash);
			ps.setString(3, u.getFirstname());
			ps.setString(4, u.getLastname());
			
			ps.executeUpdate();
			
			ret = true;
			
		}
		catch(SQLException e){
			if( e.getSQLState().equals("23000"))
				throw e;
			Logger.error("Database Error", e);
		}
		
		return ret;
	}
	
	public User signin(String username, String passwd, String remoteAddr) 
			throws PasswordNotCorrectException, UserNotExistedException
	{
		User u = null;
		String passwdhash = md5password(passwd);
		String sql = "select * from ezusers where username=?";
		try {
			Connection con = ds.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);
			
			ResultSet re = ps.executeQuery();
			if(!re.next()){
				Logger.debug("UserNotExistedException - ID : "+username);
				throw new UserNotExistedException();
			}
			
			String userPasswd = re.getString("passwd");
			if(!userPasswd.equals(passwdhash))
			{
				Logger.debug("PasswordNotCorrectException - ID : "+username);
				throw new PasswordNotCorrectException();
			}
			
			u = new User();
			u.setUsername(username);
			u.setFirstname(re.getString("firstname"));
			u.setLastname(re.getString("lastname"));
			
			Session.makeNewSession(u, remoteAddr);
			
		} catch (SQLException e) {
			Logger.error("Database Error", e);
		}
		return u;
	}
	
	private String md5password(String passwd)
	{
		String hash=null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(passwd.getBytes());
			byte bs[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (byte b : bs) {
				sb.append(String.format("%02x", b & 0xff));
			}
			hash = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			Logger.debug("MD5 Hashing fail", e);
		}
		return hash;
	}
}
