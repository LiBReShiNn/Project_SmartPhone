package Controller;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import Model.User;

public class UserDAO {

	public User selectLoginUser(String id, String pw) {
		String command = "select ID, nicName, imagePath, logOn from usertb where ID like ";
		command += "'" + id + "' and PW like '" + pw + "'";
		Connection con = null;
		Statement stmt = null;
		User user = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				user = new User(rs.getString("id"), rs.getString("nicName"), rs.getString("imagePath"),
						rs.getString("logOn"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public int updateIpPort(String id, InetAddress inetAddress) {
	
		int num = 0;
		StringBuffer  sql = new StringBuffer();
		sql.append("update usertb set ");
		sql.append("ipAddress=? where ID='"+id+"'");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setObject(1, inetAddress.toString());
			int i = pstmt.executeUpdate();
			if(i==1){
				num = 1;
			}else{
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
	
	public int updateIpPort(String id, Object ip, Object port) {
		int num = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("update usertb set ");
		sql.append("ipAddress=? , port=? where ID='" + id + "'");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setObject(1, ip);
			pstmt.setObject(2, port);
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

	public int updateUserLogOn(User user, String onoff) {
		int num = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("update usertb set ");
		sql.append("logOn=? where ID='" + user.getId() + "'");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, onoff);
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

	public String searchExistedUserId(String id) {
		String ID = null;
		String command = "select ID from usertb where ID=" + "'" + id + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				ID = rs.getString("ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ID;
	}

	public String newSelectUserPhNo(String uiPhNo) {
		String dbPhNo = null;
		String command = "select phoneNo from usertb where phoneNo=" + "'" + uiPhNo + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				dbPhNo = rs.getString("phoneNo");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbPhNo;
	}

	public int insertSignUpUser(User newUser) {
		int num = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into usertb ");
		sql.append("(ID, PW, phoneNo, nicName, imagePath, logOn)");
		sql.append("values(?,?,?,?,?,?)");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, newUser.getId());
			pstmt.setString(2, newUser.getPw());
			pstmt.setString(3, newUser.getPhoneNo());
			pstmt.setString(4, newUser.getNicName());
			pstmt.setString(5, newUser.getImagePath());
			pstmt.setString(6, "off");
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

	public String searchUserIdByPhNo(String uiPhNo) {
		String ID = null;
		String command = "select ID from usertb where phoneNo=" + "'" + uiPhNo + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				ID = rs.getString("ID");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ID;
	}

	public String searchUserPwById(String uiId) {
		String PW = null;
		String command = "select PW from usertb where ID=" + "'" + uiId + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				PW = rs.getString("PW");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return PW;
	}

	public User setUpUserInfo(String id) {
		String command = "select ID, PW, phoneNo, nicName, imagePath, logOn from usertb where ID=" + "'" + id + "'";
		Connection con = null;
		Statement stmt = null;
		User user = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				user = new User(rs.getString("id"), rs.getString("pw"), rs.getString("phoneNo"),
						rs.getString("nicName"), rs.getString("imagePath"), rs.getString("logOn"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

	public int updateUserEdit(User editUser) {
		int num = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("update usertb set ");
		sql.append("PW=?, nicName=?, imagePath=? where ID='" + editUser.getId() + "'");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, editUser.getPw());
			pstmt.setString(2, editUser.getNicName());
			pstmt.setString(3, editUser.getImagePath());
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

	public int deleteUser(String id) {
		String command = "delete from usertb where id = ? ";
		Connection con = null;
		PreparedStatement pstmt = null;
		int num = 0;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(command);
			pstmt.setString(1, id);
			num = pstmt.executeUpdate();
			if (num == 1) {
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

	public User selectFindFriend(String id) {
		String command = "select ID, nicName, imagePath from usertb where ID like ";
		command += "'" + id + "'";
		Connection con = null;
		Statement stmt = null;
		User userFriend = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				userFriend = new User(rs.getString("id"), rs.getString("nicName"), rs.getString("imagePath"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userFriend;
	}
}
