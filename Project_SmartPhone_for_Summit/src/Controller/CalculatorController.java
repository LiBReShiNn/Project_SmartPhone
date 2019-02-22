package Controller;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import Model.AddressBook;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class CalculatorController implements Initializable{
	public Stage calculatorStage;
	
	//Second Screen
	@FXML private Button scsBtnWifi;	
	@FXML private Button scsBtnVolume;
	@FXML private Button scsBtnMemo;
	@FXML private Button scsBtnCamera;
	@FXML private Button scsBtnMusic;
	
	//Frist Second Screen
	@FXML private Label fssLblStateAgency; 
	@FXML private ImageView fssIVStateVolMuVal;
	@FXML private ImageView fssIVStateChat; 
	@FXML private ImageView fssIVStateAlret; 
	@FXML private ImageView fssIVStateMessage; 
	@FXML private Label fssLblStateBattery; 
	@FXML private ProgressBar fssProgBarBattery; 
	@FXML private Label fssLblTime;
	
	@FXML private ImageView fmsIVBack; 
	@FXML private ImageView fmsIVHome;
	@FXML private ImageView fmsIVAllApps;

	@FXML private Button dialBtnBackSpace;
	@FXML private Label dialLblPutNum; 
	@FXML private Button dialBtnNo1;
	@FXML private Button dialBtnNo2;
	@FXML private Button dialBtnNo3;
	@FXML private Button dialBtnNo4;
	@FXML private Button dialBtnNo5;
	@FXML private Button dialBtnNo6;
	@FXML private Button dialBtnNo7;
	@FXML private Button dialBtnNo8;
	@FXML private Button dialBtnNo9;
	@FXML private Button dialBtnClear;
	@FXML private Button dialBtnNo0;
	@FXML private Button dialBtnResult;
	@FXML private Button dialBtnPlus;
	@FXML private Button dialBtnMinus;
	@FXML private Button dialBtnCross;
	@FXML private Button dialBtnDivide;

	static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("hh:mm:ss a");
	int count = 0;

	private Button[] btns = new Button[16];
	private String[] btnLabels = {
			"7", "8", "9", "+", "4", "5", "6", "-", "1", "2", "3", "x", "C", "0", "=", "/" };
	private int result = 0; 
	private String inStr = "0"; 
	private char lastOperator = ' ';

	File file;
	AddressBook newOneAddr;
	AddressBookDAO addressBookDAO = new AddressBookDAO();
	String imagePath = null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		setUpLocalTimeFSS();

		setUpDialAction();

		fmsIVBack.setOnMouseClicked(eBack2Main -> go2MainScreen());

		dialBtnBackSpace.setOnAction(eRemove -> {
			if (inStr.length() != 0) {
				inStr = inStr.substring(0, inStr.length() - 1);
				dialLblPutNum.setText(inStr);
			}
		});

	}

	private void setUpLocalTimeFSS() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					count = 0;
					while (true) {
						count++;
						SimpleDateFormat dy = new SimpleDateFormat("HH:mm:ss");
						String strDT = dy.format(new Date());
						Platform.runLater(() -> {
							fssLblTime.setText(strDT);
						});
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					Platform.runLater(() -> {
						callAlert("시간 오류 : 현재 시간을 알 수 없습니다");
					});
				}
				return null;
			}
		};
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	private void setUpDialAction() {

		btns[0] = dialBtnNo7;
		btns[1] = dialBtnNo8;
		btns[2] = dialBtnNo9;
		btns[3] = dialBtnPlus;
		btns[4] = dialBtnNo4;
		btns[5] = dialBtnNo5;
		btns[6] = dialBtnNo6;
		btns[7] = dialBtnMinus;
		btns[8] = dialBtnNo1;
		btns[9] = dialBtnNo2;
		btns[10] = dialBtnNo3;
		btns[11] = dialBtnCross;
		btns[12] = dialBtnClear;
		btns[13] = dialBtnNo0;
		btns[14] = dialBtnResult;
		btns[15] = dialBtnDivide;

		for (int i = 0; i < btns.length; ++i) {
			btns[i].setText(btnLabels[i]);
			btns[i].setOnAction(handler); 
		}
	}
	
	EventHandler handler = evt -> {
		String currentBtnLabel = ((Button) evt.getSource()).getText();
		switch (currentBtnLabel) {
		case "0":
		case "1":
		case "2":
		case "3":
		case "4":
		case "5":
		case "6":
		case "7":
		case "8":
		case "9":
			if (inStr.equals("0")) {
				inStr = currentBtnLabel;
			} else {
				inStr += currentBtnLabel;
			}
			dialLblPutNum.setText(inStr);

			if (lastOperator == '=') {
				result = 0;
				lastOperator = ' ';
			}
			break;
		case "+":
			compute();
			lastOperator = '+';
			break;
		case "-":
			compute();
			lastOperator = '-';
			break;
		case "x":
			compute();
			lastOperator = '*';
			break;
		case "/":
			compute();
			lastOperator = '/';
			break;
		case "=":
			compute();
			lastOperator = '=';
			break;
		case "C":
			result = 0;
			inStr = "0";
			lastOperator = ' ';
			dialLblPutNum.setText("0");
			break;
		}
	};
	
	private void go2MainScreen() {
		try {
			Stage phoneStage = new Stage(StageStyle.UTILITY);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/phone_exterior.fxml"));
			Parent rootPhoneExterior = loader.load();
			PhoneRootController phoneRootController = loader.getController();
			phoneRootController.phoneStage = phoneStage;
			Scene scenePhoneExterior = new Scene(rootPhoneExterior);
			scenePhoneExterior.getStylesheets().add(getClass().getResource("../css/phone_exterior.css").toString());
			phoneStage.setScene(scenePhoneExterior);
			phoneStage.setResizable(false);
			phoneStage.setTitle("Smart Phone");
			calculatorStage.close();
			phoneStage.show();
		} catch (Exception e) {
			callAlert("화면 전환 오류 : SMART PHONE 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}
	}
	

	private void compute() {
		int inNum = Integer.parseInt(inStr);
		inStr = "0";
		if (lastOperator == ' ') {
			result = inNum;
		} else if (lastOperator == '+') {
			result += inNum;
		} else if (lastOperator == '-') {
			result -= inNum;
		} else if (lastOperator == '*') {
			result *= inNum;
		} else if (lastOperator == '/') {
			result /= inNum;
		} else if (lastOperator == '=') {
			
		}
		dialLblPutNum.setText(result + "");
	}

	public static void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("알림창");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":") + 2));
		alert.showAndWait();
	}
}
