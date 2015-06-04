package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.sql.DataSource;

import play.Logger;
import play.db.DB;
import Model.Category;
import exception.CategoryException;

public class CategoryController {
	private DataSource ds = DB.getDataSource();
	
	private void startTransaction(Connection con) throws SQLException
	{
		con.setAutoCommit(false);
	}
	
	private void endTransaction(Connection con) throws SQLException
	{
		con.setAutoCommit(true);
	}
	
	private void stopTransaction(Connection con) throws SQLException
	{
		con.rollback();
	}
	
	private ArrayList<Category> getCategorys(String name, int user_id, int category_id)
	{
		Connection con = null;
		PreparedStatement ps = null;
		
		ArrayList<Category> list = new ArrayList<Category>();
		
		String sql = "select * from ezcategories where users_id=?";
		
		if(name != null)
			sql += " and name=?";
		
		if(category_id != -1)
			sql += " and id=?";
		
		try
		{
			con = ds.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, user_id);
			if(name != null)
				ps.setString(2, name);
			if(category_id != -1)
				ps.setInt(2, category_id);
			
			ResultSet rs = ps.executeQuery();
			while(rs.next())
			{
				Category c = new Category();
				c.setId(rs.getInt("id"));
				c.setName(rs.getString("name"));
				c.setCreated(rs.getTimestamp("created").toString());
				list.add(c);
			}
			
		}
		catch(SQLException e)
		{
			Logger.error("Database Error", e);
		}
		finally
		{
        	try {
	        	if(con != null) con.close();
	        	if(ps != null) ps.close();
			}
        	catch (SQLException e) {
			}
		}
		
		return list;
	}
	
	public boolean createNewCategory(int user_id, Category c) throws CategoryException
	{
		boolean result = true;
		String sql = "insert into ezcategories (name, users_id) values (?, ?)";
		Connection con = null;
		PreparedStatement ps = null;
		
		try
		{
			int s = getCategorys(c.getName(), user_id, -1).size();
			if(s != 0)
			{
				throw new CategoryException();
			}
			
			con = ds.getConnection();
			startTransaction(con);
			ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			ps.setString(1, c.getName());
			ps.setInt(2, user_id);
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			c.setId(rs.getInt(1));
			
			TagManager tm = new TagManager(con);
			tm.saveCategoryTag(c);
			endTransaction(con);
		}
		catch(SQLException e)
		{
			Logger.error("Database Error", e);
            if(con!=null)
            {
            	try
            	{
					stopTransaction(con);
				}
            	catch (SQLException e1)
            	{
					e1.printStackTrace();
				}
            }
            result = false;
		}
		finally
		{
        	try {
				if(ps != null) ps.close();
	        	if(con != null) con.close();
			}
        	catch (SQLException e) {
			}
		}
		return result;
	}

	public ArrayList<Category> getCategoryList(int user_id)
	{
		ArrayList<Category> list = getCategorys(null, user_id, -1);
		return list;
	}
	
	public Category getCategory(int user_id, int category_id) throws CategoryException, SQLException
	{
		ArrayList<Category> l = getCategorys(null, user_id, category_id);
		if(l.size() == 0)
			throw new CategoryException("존재하지 않는 카테고리입니다.");
		
		Category c = l.get(0);
		
		Connection con = null;
		try
		{
			con = ds.getConnection();
			TagManager tm = new TagManager(con);
			c.addTag(tm.getTagList("eztags_has_categories", c.getId()));
		}
		catch(SQLException e)
		{
			Logger.error("Database Error", e);
			throw e;
		}
		finally
		{
        	try {
	        	if(con != null) con.close();
	        }
        	catch (SQLException e) {
			}
		}
		
		return c;
	}

	public void deleteCategory(int user_id, int category_id) throws CategoryException, SQLException
	{
		String sql = "delete from ezcategories where users_id=? and id=?";
		Connection con = null;
		PreparedStatement ps = null;
		try
		{
			ArrayList<Category> l = getCategorys(null, user_id, category_id);
			if(l.size() == 0)
				throw new CategoryException("존재하지 않는 카테고리입니다.");
			
			con = ds.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, user_id);
			ps.setInt(2, category_id);
			ps.executeUpdate();
		}
		catch(SQLException e)
		{
			Logger.error("Database Error", e);
			throw e;
		}
		finally
		{
        	try {
	        	if(con != null) con.close();
	        	if(ps != null) ps.close();
			}
        	catch (SQLException e) {
			}
		}
	}

}
