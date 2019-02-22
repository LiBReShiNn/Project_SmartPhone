package Model;

public class AddressBook {
	String phName;
	String phEmail;
	String phNo;
	String phImagePath;
	public AddressBook(String phName, String phEmail, String phNo, String phImagePath) {
		super();
		this.phName = phName;
		this.phEmail = phEmail;
		this.phNo = phNo;
		this.phImagePath = phImagePath;
	}
	public String getPhName() {
		return phName;
	}
	public void setPhName(String phName) {
		this.phName = phName;
	}
	public String getPhEmail() {
		return phEmail;
	}
	public void setPhEmail(String phEmail) {
		this.phEmail = phEmail;
	}
	public String getPhNo() {
		return phNo;
	}
	public void setPhNo(String phNo) {
		this.phNo = phNo;
	}
	public String getPhImagePath() {
		return phImagePath;
	}
	public void setPhImagePath(String phImagePath) {
		this.phImagePath = phImagePath;
	}
		
}
