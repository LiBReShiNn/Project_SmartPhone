package Controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PhoneRootController implements Initializable{
	
	public Stage phoneStage;
	
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
	
	// First Main Screen
	@FXML private AnchorPane onOffAPane;
	@FXML private Label fmsLblYMD;
	@FXML private Label fmsLblTime;
	@FXML private VBox fmsVBTalk;
	@FXML private VBox fmsVBCalender;
	@FXML private VBox fmsVBMemo;
	@FXML private VBox fmsVBScheduler;
	@FXML private VBox fmsVBCalculator;
	@FXML private VBox fmsVBSettings;
	@FXML private VBox fmsVBGame;
	@FXML private VBox fmsVBGallery;
	@FXML private VBox fmsVBVideo;
	@FXML private VBox fmsVBMusic;
	@FXML private VBox fmsVBeMail;
	@FXML private ImageView fmsIVCalling; 
	@FXML private ImageView fmsIVAddrBook; 
	@FXML private ImageView fmsIVMessage; 
	@FXML private ImageView fmsIVAlarm; 
	@FXML private ImageView fmsIVFolder; 
	@FXML private ImageView fmsIVBack; 
	@FXML private ImageView fmsIVHome;
	@FXML private ImageView fmsIVAllApps; 
	
	static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("hh:mm:ss a");
	 int count=0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		setUpLocalTimeFSS();

		setUpLocalTime();

		onOffAPane.setOnMouseClicked(eOn -> {
			if (eOn.getClickCount() == 2)
				onOffAPane.setVisible(false);
		});

		fmsVBTalk.setOnMouseClicked(eTalk -> handleFmsVBTalkOpenAction());

		fmsIVAlarm.setOnMouseClicked(eStopWatch -> handleStopWatch());

		fmsIVCalling.setOnMouseClicked(eStopWatch -> handleCalling());

		fmsVBCalculator.setOnMouseClicked(eStopWatch -> handleCalculate());
	
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

	private void setUpLocalTime() {

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					count = 0;
					while (true) {
						count++;
						SimpleDateFormat ydm = new SimpleDateFormat("yyyy-MM-dd");
						SimpleDateFormat dy = new SimpleDateFormat("HH:mm:ss");
						String strYDM = ydm.format(new Date());
						String strDT = dy.format(new Date());
						Platform.runLater(() -> {
							fmsLblYMD.setText(strYDM);
							fmsLblTime.setText(strDT);
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

	private void handleFmsVBTalkOpenAction() {
		try {
			Stage talkVaTalkStage = new Stage(StageStyle.UTILITY);
			FXMLLoader vaTalkLoader = new FXMLLoader(getClass().getResource("../View/talk_login.fxml"));
			Parent talkVaTalkRoot = vaTalkLoader.load();
			TalkLoginRootController talkVaTalkController = vaTalkLoader.getController();
			talkVaTalkController.talkLoginStage = talkVaTalkStage;
			Scene talkVaTalkScene = new Scene(talkVaTalkRoot);
			talkVaTalkScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			talkVaTalkStage.setScene(talkVaTalkScene);
			talkVaTalkStage.setResizable(false);
			talkVaTalkStage.setTitle("Va Talk LOGIN");
			phoneStage.close();
			talkVaTalkStage.show();
			//callAlert("화면 전환 성공 : Va톡 화면으로 전환 되었습니다.");
		} catch (Exception e) {
			callAlert("화면 전환 오류 : Va톡 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}
	}

	private void handleStopWatch() {
		try {
			Stage talkStopWatchStage = new Stage(StageStyle.UTILITY);
			FXMLLoader stopWatchLoader = new FXMLLoader(getClass().getResource("../View/stop_watch.fxml"));
			Parent talkStopWatchRoot = stopWatchLoader.load();
			StopWatchRootController talkStopWatchController = stopWatchLoader.getController();
			talkStopWatchController.stopWatchStage = talkStopWatchStage;
			Scene talkStopWatchScene = new Scene(talkStopWatchRoot);
			talkStopWatchScene.getStylesheets().add(getClass().getResource("../css/stop_watch.css").toString());
			talkStopWatchStage.setScene(talkStopWatchScene);
			talkStopWatchStage.setResizable(false);
			talkStopWatchStage.setTitle("STOP WATCH");
			phoneStage.close();
			talkStopWatchStage.show();
			//callAlert("화면 전환 성공 : STOP WATCH 화면으로 전환 되었습니다.");
		} catch (Exception e) {
			callAlert("화면 전환 오류 : STOP WATCH 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}
	}

	private void handleCalling() {
		try {
			Stage talkDialStage = new Stage(StageStyle.UTILITY);
			FXMLLoader dialLoader = new FXMLLoader(getClass().getResource("../View/dial.fxml"));
			Parent talkDialRoot = dialLoader.load();
			DialRootController talkDialController = dialLoader.getController();
			talkDialController.dialStage = talkDialStage;
			Scene talkDialScene = new Scene(talkDialRoot);
			talkDialScene.getStylesheets().add(getClass().getResource("../css/dial.css").toString());
			talkDialStage.setScene(talkDialScene);
			talkDialStage.setResizable(false);
			talkDialStage.setTitle("DIAL");
			phoneStage.close();
			talkDialStage.show();
		} catch (Exception e) {
			callAlert("화면 전환 오류 : DIAL 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}
	}

	private void handleCalculate() {
		try {
			Stage calculatorStage = new Stage(StageStyle.UTILITY);
			FXMLLoader calculatorLoader = new FXMLLoader(getClass().getResource("../View/calculator.fxml"));
			Parent CalculatorRoot = calculatorLoader.load();
			CalculatorController CalculatorController = calculatorLoader.getController();
			CalculatorController.calculatorStage = calculatorStage;
			Scene CalculatorScene = new Scene(CalculatorRoot);
			CalculatorScene.getStylesheets().add(getClass().getResource("../css/stop_watch.css").toString());
			calculatorStage.setScene(CalculatorScene);
			calculatorStage.setResizable(false);
			calculatorStage.setTitle("CALCULATOR");
			phoneStage.close();
			calculatorStage.show();
		} catch (Exception e) {
			callAlert("화면 전환 오류 : CALCULATOR 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}
	}

	public static void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("알림창");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":") + 2));
		alert.showAndWait();
	}
}
