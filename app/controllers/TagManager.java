package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import Model.Category;
import Model.EZFile;

public class TagManager {
	
	private Connection con = null;
	
	public TagManager(Connection c)
	{
		con = c;
	}
	
	public void saveCategoryTag(Category c) throws SQLException
	{
		Iterator<String> tags = c.iterTags();
		String query = "insert into eztags (name) values (?) on duplicate key update reference_count=reference_count+1";
		String mquery = "insert into eztags_has_categories values (?, ?)";
		
		PreparedStatement ps = null, mps = null;
		
		try
		{
			while(tags.hasNext())
			{
				String t = tags.next();
				ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, t);
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				rs.next();
				int tagid = rs.getInt(1);
				rs.close();
				ps.close();
				ps = null;
				
				mps = con.prepareStatement(mquery);
				mps.setInt(2, c.getId());
				mps.setInt(1, tagid);
				mps.executeUpdate();
				mps.close();
				mps = null;
			}
		}
		catch (SQLException e) 
		{
			throw e;
		}
	}
	
	public void saveFileTag(EZFile fileinfo) throws SQLException
	{
		Iterator<String> tags = fileinfo.iterTags();
		
		// 태그를 iterator 하면서 eztags 테이블에 태그를 등록
		String query = "insert into eztags (name) values (?)"
				+ "on duplicate key update reference_count=reference_count+1";
		String mquery = "insert into eztags_has_storage_file values (?, ?)";
		
		PreparedStatement ps = null, mps = null;
		
		try
		{
			while(tags.hasNext())
			{
				String t = tags.next();
				ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, t);
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				rs.next();
				int tagid = rs.getInt(1);
				rs.close();
				ps.close();
				ps = null;
				
				mps = con.prepareStatement(mquery);
				mps.setString(2, fileinfo.getId());
				mps.setInt(1, tagid);
				mps.executeUpdate();
				mps.close();
				mps = null;
			}
		}
		catch (SQLException e) 
		{
			throw e;
		}
	}
}
