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
	 * @param remoteAddr 유저가 접속한 아이피 주소
	 */
	public static void makeNewSession(User u, String remoteAddr)
	{
		
		String sql = "Insert into ezsession (session_key, ipaddress, last_update, users_id) values (?,?,now(),?)";
		String key= UUID.randomUUID().toString();
		try
		{
			Connection con = ds.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
		
			ps.setString(1, key);
			ps.setString(2, remoteAddr);
			ps.setInt(3, u.getUserId());
			ps.executeUpdate();
		}
		catch(SQLException e){
			Logger.error("Database Error", e);
		}
		// User 객체에 위에서 만든 세션 키 삽입
		u.setSession(key);
	}
	
	/**
	 * 세션이 유요한지 검사한다
	 * @param session 검사할 세션 값
	 * @param reniteAddr 접속한 IP
	 * @return 세션 유효 여부
	 */
	public static boolean isValideSession(String session, String remoteAddr)
	{
		String sql="Select * from ezsession where session_key=?";
		ResultSet rs;
		try
		{
			Connection con = ds.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			
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
			if(rs.getString("ipaddress")!=remoteAddr)
				return false;
			
			// 4. 현재 시간이 last_update 로 부터 600초 이내에 존재하는지?
			 DateTime dt= new DateTime();
			if(rs.getDate("last_update").equals(dt.minusSeconds(expireSeconds)))
				return false;
		}
		catch(SQLException e){
			Logger.error("Database Error", e);
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
		try// 세션 파기는 expired 를 1 로 설정한다.
		{
			Connection con = ds.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, session);
			ps.executeUpdate();
			
		}
		catch(SQLException e){
			Logger.error("Database Error", e);
		}
	}
	
	/**
	 * 세션의 last_update 값을 갱신한다.
	 * @param session last_update값을 갱신할 세션 값
	 */
	public static void updateLastUpdateTime(String session)
	{
		// session 의 last_update 값을 갱신하는 코드 작성
		String sql = "Update ezsession set last_update=now() where session_key= ?";
		try// 세션 파기는 expired 를 1 로 설정한다.
		{
			Connection con = ds.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, session);
			ps.executeUpdate();
			
		}
		catch(SQLException e){
			Logger.error("Database Error", e);
		}
	}
}
