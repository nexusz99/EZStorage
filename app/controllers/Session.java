package controllers;

import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;

import org.joda.time.DateTime;

import play.Logger;
import play.db.DB;
import Model.User;

public class Session {
    
    private static final int expireSeconds = 600;
    
    private static DataSource ds = DB.getDataSource();
    /**
     * 새로운 새션을 생성한다.
     * @param u 유저 정보가 담긴 객체
     */
    public static void makeNewSession(User u, String remoteAddr)
    {
        // 이곳에 세션을 생성하고 session 테이블에 저장하는 코드를 작성
        
        String sql = "Insert into ezsession (session_key, ipaddress, last_update, users_id) values (?,?,now(),?)";
        String key= UUID.randomUUID().toString();
        Connection con = null;
        PreparedStatement ps = null;
        try
        {
            con = ds.getConnection();
            ps = con.prepareStatement(sql);
        
            ps.setString(1, key);
            ps.setString(2, remoteAddr);
            ps.setInt(3, u.getUserId());
            ps.executeUpdate();
        }
        catch(SQLException e){
            Logger.error("Database Error", e);
        }
        finally{
        	try {
				if(ps != null) ps.close();
	        	if(con != null) con.close();
			} catch (SQLException e) {
			}
        }
        // User 객체에 위에서 만든 세션 키 삽입
        u.setSession(key);
    }
    
    /**
     *      */
    public static boolean isValideSession(String session, String remoteAddr)
    {
        String sql="Select * from ezsession where session_key=?";
        ResultSet rs;
        Connection con = null;
        PreparedStatement ps = null;
        try
        {
            con = ds.getConnection();
            ps = con.prepareStatement(sql);
            
            //해당 session값을 갖는 행을 가져옴.
            ps.setString(1, session);
            rs=ps.executeQuery();
        
            
            // 해당 세션이 올바른 값인지 확인
            
            // 2. ezsession 테이블에 해당 session 값이 있는지?
            if(!rs.next())
                return false;
                
            // 1. 이미 expired 된 것인지?
            if(rs.getInt("expired")==1)
                return false;
            
            // 3. 디비에 저장된 ipaddress 와 remoteAddr 이 같은지?
            if(rs.getString("ipaddress").compareTo(remoteAddr)!=0)
                return false;
            
            // 4. 현재 시간이 last_update 로 부터 600초 이내에 존재하는지?
             DateTime dt= new DateTime();
            if(rs.getDate("last_update").equals(dt.minusSeconds(expireSeconds)))
                return false;
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
    
    /**
     * 세션을 파기한다
     * 
     * 
     * @param session 파기할 세션 값
     */
    public static void expireSession(String session)
    {
        // 세션을 파기하는 내용을 구현한다.
       
        String sql = "Update ezsession set expired = 1 where session_key= ?";
        Connection con = null;
        PreparedStatement ps = null;
        try// 세션 파기는 expired 를 1 로 설정한다.
        {
            con = ds.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, session);
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
    }
    
  public static void updateLastUpdateTime(String session)
    {
        // session 의 last_update 값을 갱신하는 코드 작성
        String sql = "Update ezsession set last_update=now() where session_key= ?";
        Connection con = null;
        PreparedStatement ps = null;
        try// 세션 파기는 expired 를 1 로 설정한다.
        {
            con = ds.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, session);
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
    }

}
