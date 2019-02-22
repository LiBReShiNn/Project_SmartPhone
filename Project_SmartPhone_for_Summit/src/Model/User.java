package Model;

public class User {
	private String id;
	private String pw;
	private String phoneNo;
	private String nicName;
	private String imagePath;
	private String logOn;
	

	public User(String id, String nicName, String imagePath) {
		super();
		this.id = id;
		this.nicName = nicName;
		this.imagePath = imagePath;
	}

	public User(String id, String nicName, String imagePath, String logOn) {
		super();
		this.id = id;
		this.nicName = nicName;
		this.imagePath = imagePath;
		this.logOn = logOn;
	}
	public User(String id, String pw, String phoneNo, String nicName, String imagePath, String logOn) {
		super();
		this.id = id;
		this.pw = pw;
		this.phoneNo = phoneNo;
		this.nicName = nicName;
		this.imagePath = imagePath;
		this.logOn = logOn;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPw() {
		return pw;
	}
	public void setPw(String pw) {
		this.pw = pw;
	}
	public String getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getNicName() {
		return nicName;
	}
	public void setNicName(String nicName) {
		this.nicName = nicName;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getLogOn() {
		return logOn;
	}
	public void setLogOn(String logOn) {
		this.logOn = logOn;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", pw=" + pw + ", phoneNo=" + phoneNo + ", nicName=" + nicName + ", imagePath="
				+ imagePath + ", logOn=" + logOn + "]";
	}
}