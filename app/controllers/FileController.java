package controllers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import play.Logger;
import play.db.DB;
import Model.EZFile;
import Model.User;
import exception.FileUploadException;

public class FileController {
	
	private LocalFileManager localfile = new LocalFileManager();
	private DataSource ds = DB.getDataSource();
	/**
	 * 새로운 파일을 저장한다.
	 * @param user 파일 저장을 요청한 유저 정보
	 * @param fileinfo 파일 정보가 저장된 객체
	 * @return
	 * @throws FileUploadException 
	 */
	public boolean saveNewfile(User user, EZFile fileinfo) 
			throws FileUploadException
	{
		
		String sql = "Select * from ezstorage_file "
				+ "where name= ? AND users_id =?";
		ResultSet rs;
		Connection con = null;
		PreparedStatement ps = null;
		TagManager tm;
        try
        {
            con = ds.getConnection();
            startTransaction(con);
            tm = new TagManager(con);
            
            ps = con.prepareStatement(sql);
            ps.setString(1,fileinfo.getName());
            ps.setInt(2, user.getUserId());
            rs=ps.executeQuery();
            if(rs.next())
            {
            	return false;
            	
            }
         
            // 로컬 파일 시스템에 해당 파일을 저장한다.
            fileinfo.setId(generateFileUniqueID(fileinfo));
    		String localpath = localfile.store(fileinfo);
    		
    		ps.close();
    		// ezstorage_file table에 새로운 파일을 등록한다.
    		sql="insert into ezstorage_file"
    				+ "(id, name, path, size, users_id, type_id) "
    				+ "values (?,?,?,?,?, ?) ";
    		ps=con.prepareStatement(sql);
    		ps.setString(1, fileinfo.getId());
    		ps.setString(2, fileinfo.getName());
    		ps.setString(3, localpath);
    		ps.setLong(4, fileinfo.getSize());
    		ps.setInt(5, user.getUserId());
    		ps.setInt(6, getFileType(fileinfo.getName(), con));
    		ps.executeUpdate();
    		
    		tm.saveFileTag(fileinfo);
    		
        }
        catch(SQLException e){
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
            throw new FileUploadException("데이터베이스 에러!");
        }
        catch(Exception e)
        {
        	Logger.error("Internal Error", e);
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
            throw new FileUploadException("내부 오류!");
        }
        finally
        {
        	try {
				if(ps != null) ps.close();
	        	if(con != null)
	        	{
	        		endTransaction(con);
	        		con.close();
	        	}
			}
        	catch (SQLException e) {
			}
        }
		return true;
	}

	public boolean deleteFile(int user_id, String file_id)
	{
		Connection con = null;
		PreparedStatement ps = null;
		String sql = "delete from ezstorage_file where users_id=? and id=?";
		try
		{
			con = ds.getConnection();
			EZFile f = getFileTuples(user_id, file_id, null, con).get(0);
			
			if(f == null)
				return false;
			
			localfile.delete(f.getLocalpath());
			
			ps = con.prepareStatement(sql);
			
			ps.setInt(1, user_id);
			ps.setString(2, file_id);
			
			ps.executeUpdate();
		}
		catch (SQLException e)
		{
			Logger.error("Database Error", e);
		}
		catch(Exception e)
		{
			e.printStackTrace();
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
		return true;
	}
	
	public EZFile getFile(int user_id, String file_id, boolean withtag)
	{
		EZFile ef = null;
		
		Connection con = null;
		
		try
		{
			con = ds.getConnection();
			ef = getFileTuples(user_id, file_id, null, con).get(0);
			if(ef == null)
				return null;
			ef.setBody(localfile.get(ef.getLocalpath()));
			
			if(withtag)
			{
				TagManager tm = new TagManager(con);
				ArrayList<String> list = tm.getTagList("eztags_has_storage_file", file_id);
				ef.addTag(list);
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
			}
        	catch (SQLException e) {
			}
		}
		return ef;
	}
	
	public ArrayList<EZFile> getFileList(int user_id, int marker, int limit)
	{
		ArrayList<EZFile> list = null;
		String option = " order by created limit " + marker + ", " + limit;
		
		Connection con = null;
		try
		{
			con = ds.getConnection();
			list = getFileTuples(user_id, null, option, con);
			if(list.get(0) == null)
				return null;
		}
		catch(SQLException e)
		{
			Logger.error("Database Error", e);
		}
		finally
		{
        	try {
	        	if(con != null) con.close();
			}
        	catch (SQLException e) {
			}
		}
		
		return list;
	}
	
	private String generateFileUniqueID(EZFile f)
	{
		String id = "";
		id += f.getName() + " - " + System.currentTimeMillis();
		id += f.getLocalpath();
		id = Utils.md5(id);
		return id;
	}
	
	private ArrayList<EZFile> getFileTuples(int user_id, String file_id, String option, Connection c) 
			throws SQLException
	{
		ArrayList<EZFile> list = new ArrayList<EZFile>();
		String sql = "select * from ezstorage_file where users_id=?";
		
		if(file_id!=null)
			sql += " and id=?";
		
		if(option != null)
			sql += option;
		
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, user_id);
		
		if(file_id!=null)
			ps.setString(2, file_id);
		
		ResultSet rs = ps.executeQuery();
		
		while(rs.next())
		{
			EZFile f = new EZFile();
			f.setId(rs.getString("id"));
			f.setCreateDate(rs.getTimestamp("created").toString());
			f.setSize(rs.getLong("size"));
			f.setLocalpath(rs.getString("path"));
			f.setName(rs.getString("name"));
			f.setType(rs.getInt("type_id"));
			list.add(f);
		}
		
		rs.close();
		ps.close();
		
		if(list.size() == 0)
			list.add(null);
		return list;
	}
	
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
	
	private int getFileType(String filename, Connection con) throws SQLException
	{
		String extension = filename.substring(filename.lastIndexOf('.')+1,
											  filename.length());
		String sql = "select value from ezfile_type where extension=?";
		
		PreparedStatement ps = con.prepareStatement(sql);
		ps.setString(1, extension);
		
		ResultSet rs = ps.executeQuery();
		
		if(!rs.next())
			return -1;
		int type = rs.getInt("value");
		rs.close();
		ps.close();
		return type;
	}
}
