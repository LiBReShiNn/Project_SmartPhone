package Model;

public class Room {
	private String userID;
	private String roomName;
	private String friendID;
	
	public Room(String userID, String roomName, String friendID) {
		super();
		this.userID = userID;
		this.roomName = roomName;
		this.friendID = friendID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getFriendID() {
		return friendID;
	}
	public void setFriendID(String friendID) {
		this.friendID = friendID;
	}

}
