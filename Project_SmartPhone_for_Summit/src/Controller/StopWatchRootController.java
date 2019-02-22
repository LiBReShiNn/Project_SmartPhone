package Controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;

public class StopWatchRootController implements Initializable{
	
	public Stage stopWatchStage;
	
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
	@FXML private Label stopWatchLblTime;
	@FXML private Label stopWatchLblTimeRunner;
	@FXML private Button stwchBtnStart;
	@FXML private Button stwchBtnPause;
	@FXML private Button stwchBtnStop;
	@FXML private Button stwchBtnRecord;
	@FXML private ListView<TimeRecoder> stopWatchListViewRecord; 
	@FXML private ImageView fmsIVBack; 
	@FXML private ImageView fmsIVHome;
	@FXML private ImageView fmsIVAllApps; 

	static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("hh:mm:ss a");
	int count = 0;

	private Timeline timeLine;
	private DoubleProperty timeSeconds = new SimpleDoubleProperty();
	private DoubleProperty timeSecondsRunner = new SimpleDoubleProperty();
	private Duration time = Duration.ZERO;
	private Duration timeRunner = Duration.ZERO;
	private ObservableList<TimeRecoder> obTimeRecoderList = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		setUpLocalTimeFSS();

		fmsIVBack.setOnMouseClicked(eBack2Main -> go2MainScreen());

		stwchBtnPause.setVisible(false);

		stwchBtnStart.setOnAction(eStart -> startRunning());

		stwchBtnPause.setOnAction(ePause -> pauseRunning());

		stwchBtnStop.setOnMouseClicked(eStop -> {
			if (eStop.getClickCount() == 1)
				resetRunning();
			else if (eStop.getClickCount() == 2)
				resetRunner();
		});

		stwchBtnRecord.setOnAction(eRecord -> recordWithCurrentTime());

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
			stopWatchStage.close();
			phoneStage.show();
		} catch (Exception e) {
			callAlert("화면 전환 오류 : SMART PHONE 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}
	}

	public void startRunning() {
		timeLine = new Timeline();
		stopWatchLblTime.textProperty().bind(timeSeconds.asString());
		stopWatchLblTimeRunner.textProperty().bind(timeSecondsRunner.asString());
		stwchBtnStart.setVisible(false);
		stwchBtnPause.setVisible(true);

		timeLine = new Timeline(new KeyFrame(Duration.millis(10), running -> {
			Duration duration = ((KeyFrame) running.getSource()).getTime();
			time = time.add(duration);
			timeSeconds.set(time.toSeconds());
			timeRunner = timeRunner.add(duration);
			timeSecondsRunner.set(timeRunner.toSeconds());
		}));
		timeLine.setCycleCount(Timeline.INDEFINITE);
		timeLine.play();
	}

	public void pauseRunning() {
		timeLine.stop();
		stwchBtnStart.setVisible(true);
		stwchBtnPause.setVisible(false);
	}

	private void resetRunning() {
		time = Duration.ZERO;
		timeSeconds.set(0);
		timeLine.jumpTo(time);
	}

	private void resetRunner() {
		timeRunner = Duration.ZERO;
		timeSecondsRunner.set(0);
		timeLine.jumpTo(timeRunner);
		time = Duration.ZERO;
		timeSeconds.set(0);
		timeLine.jumpTo(time);
	}

	private void recordWithCurrentTime() {

		SimpleDateFormat rt = new SimpleDateFormat("HH:mm:ss");
		String recordingTime = rt.format(new Date());
		obTimeRecoderList
				.add(new TimeRecoder(stopWatchLblTime.getText(), stopWatchLblTimeRunner.getText(), recordingTime));
		stopWatchListViewRecord.setItems(obTimeRecoderList);
		stopWatchListViewRecord.setCellFactory(new Callback<ListView<TimeRecoder>, ListCell<TimeRecoder>>() {
			@Override
			public ListCell<TimeRecoder> call(ListView<TimeRecoder> param) {
				return new ListCell<TimeRecoder>() {
					@Override
					protected void updateItem(TimeRecoder timerecoder, boolean empty) {
						super.updateItem(timerecoder, empty);

						if (timerecoder == null || empty) {
							setText(null);
							setGraphic(null);
						} else {

							HBox cellRoot = new HBox(10);
							cellRoot.setAlignment(Pos.CENTER_RIGHT);
							cellRoot.setPadding(new Insets(5));

							VBox vBox = new VBox(5);
							vBox.setAlignment(Pos.CENTER_LEFT);
							vBox.setPadding(new Insets(5));

							vBox.getChildren().addAll(
									new Label("CheckTime : " + timerecoder.getStopWatchLblTimeRunner()),
									new Label("RunningTime : " + timerecoder.getStopWatchLblTime()));

							cellRoot.getChildren().add(vBox);

							cellRoot.getChildren().add(new Separator(Orientation.VERTICAL));

							VBox vBox2 = new VBox(5);
							vBox2.setAlignment(Pos.CENTER);
							vBox2.setPadding(new Insets(5));

							vBox2.getChildren().addAll(new Label("RecordTime"),
									new Label(timerecoder.getSimpleDateFormat()));
							cellRoot.getChildren().add(vBox2);
							setGraphic(cellRoot);
						}
					}
				};
			}
		});
		stopWatchListViewRecord.scrollTo(obTimeRecoderList.size());
	}

	public static void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("알림창");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":") + 2));
		alert.showAndWait();
	}

	class TimeRecoder {

		String stopWatchLblTime;
		String stopWatchLblTimeRunner;
		String simpleDateFormat;

		public TimeRecoder(String stopWatchLblTime, String stopWatchLblTimeRunner, String simpleDateFormat) {
			super();
			this.stopWatchLblTime = stopWatchLblTime;
			this.stopWatchLblTimeRunner = stopWatchLblTimeRunner;
			this.simpleDateFormat = simpleDateFormat;
		}

		public String getStopWatchLblTime() {
			return stopWatchLblTime;
		}

		public void setStopWatchLblTime(String stopWatchLblTime) {
			this.stopWatchLblTime = stopWatchLblTime;
		}

		public String getStopWatchLblTimeRunner() {
			return stopWatchLblTimeRunner;
		}

		public void setStopWatchLblTimeRunner(String stopWatchLblTimeRunner) {
			this.stopWatchLblTimeRunner = stopWatchLblTimeRunner;
		}

		public String getSimpleDateFormat() {
			return simpleDateFormat;
		}

		public void setSimpleDateFormat(String simpleDateFormat) {
			this.simpleDateFormat = simpleDateFormat;
		}

	}

}
