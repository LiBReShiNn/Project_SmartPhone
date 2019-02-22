package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Model.AddressBook;

public class AddressBookDAO {

	public int insertPhNewOne(AddressBook newOne) {
		int num = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into phaddrbooktb ");
		sql.append("(phName, phEmail, phNo, phImagePath) ");
		sql.append("values(?,?,?,?)");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, newOne.getPhName());
			pstmt.setString(2, newOne.getPhEmail());
			pstmt.setString(3, newOne.getPhNo());
			pstmt.setString(4, newOne.getPhImagePath());

			int i = pstmt.executeUpdate();
			if (i == 1) {
				num = 1;
			} else {
				num = 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return num;
	}

	public String searchExistedPnNo(String phNo) {
		String phNoExisted = null;
		String command = "select phNo from phaddrbooktb where phNo=" + "'" + phNo + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				phNoExisted = rs.getString("phNo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return phNoExisted;
	}

}
