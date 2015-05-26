package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import javax.sql.DataSource;

import play.Logger;
import play.db.DB;
import Model.EZFile;
import Model.User;

public class FileController {
	
	private LocalFileManager localfile = new LocalFileManager();
	private TagManager tagmanager = new TagManager();
	private DataSource ds = DB.getDataSource();
	/**
	 * 새로운 파일을 저장한다.
	 * @param user 파일 저장을 요청한 유저 정보
	 * @param fileinfo 파일 정보가 저장된 객체
	 * @return
	 */
	public boolean saveNewfile(User user, EZFile fileinfo)
	{
		
		String sql = "Select * from ezstorage_file "
				+ "where name= ? AND users_id =?";
		ResultSet rs;
		Connection con = null;
		PreparedStatement ps = null;
        try
        {
            con = ds.getConnection();
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
        }
        catch(SQLException e){
            Logger.error("Database Error", e);
        } finally{
        	try {
				if(ps != null) ps.close();
	        	if(con != null) con.close();
			} catch (SQLException e) {
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
}
