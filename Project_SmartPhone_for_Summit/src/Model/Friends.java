package Model;

public class Friends {
	private String userID;
	private String counterID;

	public Friends(String userID, String counterID) {
		super();
		this.userID = userID;
		this.counterID = counterID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getCounterID() {
		return counterID;
	}

	public void setCounterID(String counterID) {
		this.counterID = counterID;
	}

	@Override
	public String toString() {
		return "Friends [UserID=" + userID + ", CounterID=" + counterID + "]";
	}

}