package Controller;

import java.sql.Connection;
import java.sql.DriverManager;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class DBUtility {

	public static Connection getConnection() {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost/serverUserDB", "root", "123456");
			//con = DriverManager.getConnection("jdbc:mysql://192.168.0.209/serveruserdb", "root", "123456");
		} catch (Exception e) {
			callAlert("연결 실패 : DB 연결에 실패하였습니다.\n점검바람 : " + e.getMessage());
			e.printStackTrace();             
			return null;
		}
		return con;
	}
	
	public static void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("알림창");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":")+2));
		alert.showAndWait();
	}
}

