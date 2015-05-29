package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import javax.sql.DataSource;

import exception.FileUploadException;
import play.Logger;
import play.db.DB;
import Model.EZFile;
import Model.User;

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
    		sql="insert into ezstorage_file(id, name, path, size, users_id) "
    				+ "values (?,?,?,?,?) ";
    		ps=con.prepareStatement(sql);
    		ps.setString(1, fileinfo.getId());
    		ps.setString(2, fileinfo.getName());
    		ps.setString(3, localpath);
    		ps.setLong(4, fileinfo.getSize());
    		ps.setInt(5, user.getUserId());
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
			EZFile f = getFileTuple(user_id, file_id, con);
			
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
	
	private String generateFileUniqueID(EZFile f)
	{
		String id = "";
		id += f.getName() + " - " + System.currentTimeMillis();
		id += f.getLocalpath();
		id = Utils.md5(id);
		return id;
	}
	
	private EZFile getFileTuple(int user_id, String file_id, Connection c) 
			throws SQLException
	{
		EZFile f = null;
		String sql = "select * from ezstorage_file where users_id=? and id=?";
		
		PreparedStatement ps = c.prepareStatement(sql);
		ps.setInt(1, user_id);
		ps.setString(2, file_id);
		
		ResultSet rs = ps.executeQuery();
		
		f = new EZFile();
		f.setId(file_id);
		if(!rs.next())
			return null;
		
		f.setCreateDate(rs.getTimestamp("created").toString());
		f.setSize(rs.getLong("size"));
		f.setLocalpath(rs.getString("path"));
		f.setName(rs.getString("name"));
		
		rs.close();
		ps.close();
		return f;
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
}
