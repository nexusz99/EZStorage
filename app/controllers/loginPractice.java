
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*; 
import java.util.Scanner;
import java.util.UUID;

public class loginPractice {
	
	// JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://data.khuhacker.com/ezstorage";

	   //  Database credentials
	   static final String USER = "ezstorage";
	   static final String PASS = "19870101!@#";
	   static Connection conn = null;
	   static Statement stmt = null;
	   static int cnt;
	   
	   
	   boolean Join(String id,String password,String name)//회원가입
	   {
		 
		   password=Hash_MD5(password); //md5로 password를 hash
		   PreparedStatement pst;
		   try{ 
			  String str="SELECT COUNT(userID) FROM User"; //uid 생성을 위해 현재 있는 user개수를 가져옴
			  ResultSet rs= stmt.executeQuery(str);
			  if(rs.next())
		   	  {
				  cnt= rs.getInt("COUNT(userID)");
		   	  }
			  else{
				  cnt=0;
			  }
			  pst=conn.prepareStatement("SELECT userID FROM User WHERE userID=?"); // id중복 찾는 sql
			  pst.setString(1, id);
			  rs=pst.executeQuery();
			  if(!rs.next()) //중복이 없으면 회원가입 완료
			  {
				  pst=conn.prepareStatement("insert into User (uid,userId,password,name) values (?,?,?,?)");
				  pst.setInt(1,cnt);
				  pst.setString(2,id)	;
				  pst.setString(3,password);
				  pst.setString(4,name);
				  pst.executeUpdate();
				  pst.clearParameters();
				  pst.close();
				  rs.close();
				  return true;
			  }
			  else //id중복
			  {
				  System.out.println("duplicated ID");
				  return false;
				  
			  }
		   }
		   catch(SQLException se){
			      //Handle errors for JDBC
			   se.printStackTrace();
			   System.out.println(se.getMessage());
		   }
		   catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			      System.out.println(e.getMessage());
			}
		   return false;
		}
		  
	   boolean login(String id,String password) //로그인함수
	   {
		   password=Hash_MD5(password); // md5로 password를 hash 
		   PreparedStatement pst;
		   try{
			  pst=conn.prepareStatement("SELECT password FROM User WHERE userID=?");
			  pst.setString(1, id); 
			  ResultSet rs=pst.executeQuery();
			  pst.clearParameters();
			  if(!rs.next())  //아이디 존재하지 않음
			  {
				  System.out.println("Not exist");
				  return false;
			  }
			  else
			  {
				  if(rs.getString("password").equals(password))
				  {
					  pst=conn.prepareStatement("Insert into Session (id, time ,sessionKey) values (?,?,?)");
					  pst.setString(1,id);
					  pst.setLong(2, System.currentTimeMillis());
					  pst.setString(3, UUID.randomUUID().toString());
					  pst.executeUpdate();
					  System.out.println("Login!");
					  pst.clearParameters();
					  pst.close();
					  return true;
				  }
				  else //비밀번호 불일치
				  {
					  System.out.println(password);
					  System.out.println(rs.getString("password"));
					  return false;
				  }
				  
			  }
		   }
		   catch(SQLException se){
			      //Handle errors for JDBC
			   se.printStackTrace();
			   System.out.println(se.getMessage());
		   }
		   catch(Exception e){
			      //Handle errors for Class.forName
			      e.printStackTrace();
			      System.out.println(e.getMessage());
			}
		return false;
		   
	   }
	   
	   public static void main(String[] args) {
		 loginPractice temp=new loginPractice();
		  Scanner scan = new Scanner(System.in);
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = conn.createStatement();
		     
		      //STEP 5: Extract data from result set=======================================
		     
		      String id=new String();
			   String password=new String();
			   String name=new String();
			   id=scan.next();
			   password=scan.next();
			   name=scan.next();
			   temp.Join(id, password, name);
			   temp.login(id,password);
		      //STEP 6: Clean-up environment=======================================
		     
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   System.out.println("End");
		   }
	   
	   String Hash_MD5(String str){
			String MD5 = ""; 
			try{
				MessageDigest md = MessageDigest.getInstance("MD5"); 
				md.update(str.getBytes()); 
				byte byteData[] = md.digest();
				StringBuffer sb = new StringBuffer(); 
				for(int i = 0 ; i < byteData.length ; i++){
					sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
				}
				MD5 = sb.toString();
				
			}catch(NoSuchAlgorithmException e){
				e.printStackTrace(); 
				MD5 = null; 
			}
			return MD5;
		}
}
