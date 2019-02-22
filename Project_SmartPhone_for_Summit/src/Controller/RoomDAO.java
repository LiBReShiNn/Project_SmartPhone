package Controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.Friends;
import Model.Room;
import Model.User;

public class RoomDAO { 
	
	UserDAO userDAO = new UserDAO();
	
	public int makePrivRoom(String id, String friendID) {
		int num = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into roomtb ");
		sql.append("(userID, roomName, counterID)");
		sql.append("values(?,?,?)");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, id);
			pstmt.setString(2, id + "," + friendID);
			pstmt.setString(3, friendID);
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

	public List<String> setUpChatRoomList(String UserId) {
		List<String> chatRoomList = new ArrayList<>();
		String command = "select distinct roomName from roomtb where UserID=" + "'" + UserId + "' or counterID= '" + UserId + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				chatRoomList.add(rs.getString("roomName"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chatRoomList;
	}

	public Room searchRoomAlready(String userID, String friendID) {
		Room room = null;
		String command = "select * from roomtb where userID=" + "'" + userID + "' and counterID='" + friendID + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				room = new Room(rs.getString("userID"), rs.getString("roomName"), rs.getString("counterID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return room;
	}

	public Friends findPrivRoomJoinner(String roomName) {
		Friends privChatters = null;
		String command = "select userID, counterID from roomtb where roomName ='" + roomName + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				privChatters = new Friends(rs.getString("userID"), rs.getString("counterID"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return privChatters;
	}

	public int makeJoinRoom(String userID, String roomName, String friendID) {
		int num = 0;
		StringBuffer sql = new StringBuffer();
		sql.append("insert into roomtb ");
		sql.append("(userID, roomName, counterID)");
		sql.append("values(?,?,?)");
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(1, userID);
			pstmt.setString(2, roomName);
			pstmt.setString(3, friendID);
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
	
	public ArrayList<User> findGroupRoomJoinner(String roomName) {
		ArrayList<User> groupChatters = new ArrayList<User>();
		String command = "select userID, counterID from roomtb where roomName ='" + roomName + "'";
		Connection con = null;
		Statement stmt = null;
		try {
			con = DBUtility.getConnection();
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(command);
			while (rs.next()) {
				groupChatters.add(userDAO.setUpUserInfo(rs.getString("counterID")));
			}
			groupChatters.add(userDAO.setUpUserInfo(rs.getString("userID")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return groupChatters;
	}
	
	public int deleteUserFromRoom(String roomName) {
		String command = "delete from roomtb where roomName = ? ";
		Connection con = null;
		PreparedStatement pstmt = null;
		int num = 0;
		try {
			con = DBUtility.getConnection();
			pstmt = con.prepareStatement(command);
			pstmt.setString(1, roomName);
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
	
}
