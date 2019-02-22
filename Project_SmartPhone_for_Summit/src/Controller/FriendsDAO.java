package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Friends;
import Model.User;

public class FriendsDAO { 
	
	UserDAO userDAO = new UserDAO();
	
	public List<User> setUpFriendsList(String UserId) {
		List<User> friendsList = new ArrayList<>();
		String command = "select counterID from chattertb where UserID=" + "'" + UserId + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				User user = userDAO.setUpUserInfo(rs.getString("counterID"));
				friendsList.add(user);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return friendsList;
	}

	public int insertFriends(Friends friends) {
		int num = 0;
		StringBuffer  sql = new StringBuffer();
		sql.append("insert into chattertb ");
		sql.append("(userID, counterID) ");
		sql.append("values(?,?) ");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, friends.getUserID());
			pstmt.setString(2, friends.getCounterID());
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
	
	public Friends searchAddAlready(String userID, String findId) {
		Friends friends=null;
		String command = "select * from chattertb where userID=" + "'" + userID + "' and counterID='"+ findId + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				friends = new Friends(rs.getString("userID"), rs.getString("counterID")); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return friends;
	}
	
	public int deleteFriend(String userID, String counterID) {
		String command = "delete from chattertb where userID = ? and counterID = ? ";
		Connection con = null;
		PreparedStatement pstmt = null;
		int num=0;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(command);
			pstmt.setString(1, userID);
			pstmt.setString(2, counterID);
			num = pstmt.executeUpdate();
			if(num==1){
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
	
}
