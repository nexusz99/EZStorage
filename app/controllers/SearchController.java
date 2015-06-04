package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import play.db.DB;
import Model.User;

class fileNode {
	public String file_name;
	public String id;
	public double rate;
}

public class SearchController {

	private DataSource ds = DB.getDataSource();

	public ArrayList<fileNode> searchFile(User user, ArrayList<String> tags)
			throws SQLException {
		String s_tags = "";
		for (int i = 0; i < tags.size(); i++) {
			s_tags += "'" + tags.get(i) + "'";
			if (i == tags.size() - 1)
				continue;
			s_tags += ",";

		}
		Connection con = null;
		PreparedStatement ps_getFile = null;
		PreparedStatement ps_getTag = null;
		PreparedStatement ps_getMatch = null;
		PreparedStatement ps_getFileName = null;

		String getFile = "create TEMPORARY table ttags "
				+ "ENGINE=MEMORY as (select id from eztags where name in (?))";
		String getTag = "create TEMPORARY table files "
				+ "ENGINE=MEMORY as (select storage_file_id "
				+ "from eztags_has_storage_file where tags_id in "
				+ "(select id from ttags) and user_id= ? )";
		String getMatch = "select tmp.id, ef.name  from (select id from ttags left join "
				+ "eztags_has_storage_file f on id = f.tags_id "
				+ "where f.storage_file_id='?') as tmp join ezstorage_file as ef on tmp.id = ef.id";
		String getFileName = "SELECT storage_file_id from files";

		ResultSet rs = null;
		ArrayList<fileNode> result = new ArrayList<fileNode>();
		try {
			con=DB.getConnection();
			ps_getFile = con.prepareStatement(getFile);
			ps_getTag = con.prepareStatement(getTag);
			ps_getMatch = con.prepareStatement(getMatch);
			ps_getFileName = con.prepareStatement(getFileName);

			ps_getFile.setString(1, s_tags);
			ps_getFile.executeUpdate();

			ps_getTag.setInt(1, user.getUserId());
			ps_getTag.executeUpdate();

			rs = ps_getFileName.executeQuery();
			fileNode tmp = null;
			int size = tags.size();
			rs.next();
			while (!rs.next()) {
				String id = rs.getString("storage_file_id");
				ps_getMatch.setString(1, id);
				ResultSet inter = ps_getMatch.executeQuery();

				inter.last();
				int m = inter.getRow();
				inter.beforeFirst();

				tmp = new fileNode();
				tmp.file_name = inter.getString("ef.name");// file이름 넣기
				tmp.id = id;
				tmp.rate = m / size;

				// test
				System.out.println(id);
				System.out.println(rs.getDouble("match"));
				result.add(tmp);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
		return result;
	}

	public String SearchCategory(User user, int id) throws Exception {

		Connection con = null;
		PreparedStatement ps_getCate = null;
		String getCate = "select name from ezcategories "
				+ "where users_id=? and id=?;";
		String result = null;
		ResultSet rs = null;
		try {
			con=DB.getConnection();
			ps_getCate = con.prepareStatement(getCate);
			ps_getCate.setInt(1, user.getUserId());
			ps_getCate.setInt(2, id);
			rs = ps_getCate.executeQuery();
			rs.next();
			result = rs.getString("name");
			System.out.println(result);
		} catch (Exception e) {

			System.out.println(e.getMessage());
		}
		return result;
	}

}
