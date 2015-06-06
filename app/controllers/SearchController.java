package controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.sql.DataSource;

import play.db.DB;
import Model.Category;
import Model.ResultFile;
import exception.CategoryException;

public class SearchController {

	private DataSource ds = DB.getDataSource();

	public Collection<ResultFile> searchFile(int user_id, ArrayList<String> tags)
	{
		
		Connection con = null;
		PreparedStatement ps = null, subps = null;

		String getTagIDSQL = "create TEMPORARY table ttags "
				+ "ENGINE=MEMORY as (select id from eztags where name in (?";
		
		for (int i = 1; i < tags.size(); i++) {
			getTagIDSQL += ",?";
		}
		getTagIDSQL += "))";
		String getFileList = "create TEMPORARY table files "
				+ "ENGINE=MEMORY as (select distinct storage_file_id "
				+ "from eztags_has_storage_file where tags_id in "
				+ "(select id from ttags) and user_id= ? )";
		
		String getMatch = "select tmp.storage_file_id, ef.name, ft.value  from (select storage_file_id from ttags left join "
				+ "eztags_has_storage_file f on id = f.tags_id "
				+ "where f.storage_file_id=?) as tmp join ezstorage_file as ef on tmp.storage_file_id = ef.id "
				+ "join ezfile_type as ft on ef.type_id = ft.id";
		
		String getFileName = "SELECT storage_file_id from files";

		
		ArrayList<ResultFile> result = new ArrayList<ResultFile>();
		HashMap<String, ResultFile> re = new HashMap<String, ResultFile>();
		try {
			con=DB.getConnection();
			
			// 태그 id 수집
			ps = con.prepareStatement(getTagIDSQL);
			for (int i = 1; i <= tags.size(); i++) {
				ps.setString(i, tags.get(i-1));
			}
			
			ps.executeUpdate();
			ps.close();
			// 태그에 매칭되는 파일 목록 수집
			
			ps = con.prepareStatement(getFileList);
			ps.setInt(1, user_id);
			ps.executeUpdate();
			ps.close();
			
			ps = con.prepareStatement(getFileName);

			ResultSet rs = ps.executeQuery();
			ResultFile tmp = null;
			int size = tags.size();
			
			while (rs.next()) {
				String id = rs.getString("storage_file_id");
				subps = con.prepareStatement(getMatch);
				subps.setString(1, id);
				ResultSet inter = subps.executeQuery();

				inter.last();
				double m = inter.getRow();
				
				tmp = new ResultFile();
				tmp.file_name = inter.getString("ef.name");// file이름 넣기
				tmp.id = id;
				tmp.type = inter.getInt("ft.value");
				tmp.rate = (m / size)*100;
				
				if(!re.containsKey(tmp.file_name))
					re.put(tmp.file_name, tmp);
				
				inter.close();
				subps.close();
			}
			rs.close();
			subps = con.prepareStatement("drop table ttags, files");
			subps.executeUpdate();
			subps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				if(ps!=null)
					ps.close();
				if(con!=null) 
					con.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return re.values();
	}

	public Collection<ResultFile> searchCategory(int user_id, int category_id) throws CategoryException, SQLException {
		CategoryController cc = new CategoryController();
		Category c = cc.getCategory(user_id, category_id);
		
		Collection<ResultFile> re = searchFile(user_id, c.getTags());
		return re;
	}

}
