package Controller;

import java.io.File;
import java.io.FileInputStream;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DialRootController implements Initializable{
	
	public Stage dialStage;
	
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

	@FXML private Button dialBtnAddPhNo;
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
	@FXML private Button dialBtnNoStar;
	@FXML private Button dialBtnNo0;
	@FXML private Button dialBtnNoShap;
	@FXML private Button dialBtnMsg;
	@FXML private Button dialBtnCall;
	@FXML private Button dialBtnFaceCall;

	static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("hh:mm:ss a");
	int count = 0;

	private Button[] btns = new Button[12];
	private String[] btnLabels = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "*", "0", "#" };
	private String inStr = "";

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

		dialBtnAddPhNo.setOnAction(eAddNewPhoneNo -> addNewPhoneNo());
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

		btns[0] = dialBtnNo1;
		btns[1] = dialBtnNo2;
		btns[2] = dialBtnNo3;
		btns[3] = dialBtnNo4;
		btns[4] = dialBtnNo5;
		btns[5] = dialBtnNo6;
		btns[6] = dialBtnNo7;
		btns[7] = dialBtnNo8;
		btns[8] = dialBtnNo9;
		btns[9] = dialBtnNoStar;
		btns[10] = dialBtnNo0;
		btns[11] = dialBtnNoShap;

		for (int i = 0; i < btns.length; ++i) {
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
			inStr += currentBtnLabel; 
			dialLblPutNum.setText(inStr);
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
			dialStage.close();
			phoneStage.show();
		} catch (Exception e) {
			callAlert("화면 전환 오류 : SMART PHONE 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}
	}

	private void addNewPhoneNo() {
		try {
			FXMLLoader addPhNoLoader = new FXMLLoader(getClass().getResource("../View/dial_add_phno.fxml"));
			Parent addPhNoRoot = addPhNoLoader.load();
			Stage addPhNoStage = new Stage(StageStyle.UNDECORATED);
			addPhNoStage.initModality(Modality.NONE);
			addPhNoStage.initOwner(dialStage);
			addPhNoStage.setResizable(false);
			Scene addPhNoScene = new Scene(addPhNoRoot);
			addPhNoScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			addPhNoStage.setScene(addPhNoScene);
			addPhNoStage.show();

			ImageView dialAddPhnoImgV = (ImageView) addPhNoRoot.lookup("#dialAddPhnoImgV");
			TextField dialAddPhnoTfName = (TextField) addPhNoRoot.lookup("#dialAddPhnoTfName");
			TextField dialAddPhnoTfeMail = (TextField) addPhNoRoot.lookup("#dialAddPhnoTfeMail");
			TextField dialAddPhnoTf = (TextField) addPhNoRoot.lookup("#dialAddPhnoTf");
			Button dialAddPhnoExit = (Button) addPhNoRoot.lookup("#dialAddPhnoExit");
			Button dialAddPhnoAdd = (Button) addPhNoRoot.lookup("#dialAddPhnoAdd");

			dialAddPhnoExit.setOnAction(eClose -> addPhNoStage.close());

			dialAddPhnoImgV.setOnMouseClicked(eEidtPic -> {
				if (eEidtPic.getClickCount() == 2) {
					try {
						FileChooser fileChooser = new FileChooser();
						fileChooser.getExtensionFilters().add(new ExtensionFilter("JPG", "*.jpg"));
						fileChooser.getExtensionFilters().add(new ExtensionFilter("JPEG", "*.jpeg"));
						fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG", "*.png"));
						fileChooser.getExtensionFilters().add(new ExtensionFilter("BMP", "*.bmp"));
						fileChooser.getExtensionFilters().add(new ExtensionFilter("GIF", "*.gif"));
						fileChooser.getExtensionFilters().add(new ExtensionFilter("TIF", "*.tif"));
						fileChooser.getExtensionFilters().add(new ExtensionFilter("모든파일", "*.*"));
						file = fileChooser.showOpenDialog(addPhNoStage);
						if (file != null) {
							imagePath = file.getPath();
							FileInputStream fileInputStream = new FileInputStream(imagePath);
							dialAddPhnoImgV.setImage(new Image(fileInputStream));
						}

					} catch (Exception e) {
						callAlert("이미지 파일 로딩 오류 : 이미지 파일을 확인하세요");
						e.printStackTrace();
					}
				}
			});

			dialAddPhnoTf.setText(inStr);

			dialAddPhnoAdd.setOnAction(eAddNewOne -> {

				if (dialAddPhnoTfName.getText() == null || dialAddPhnoTfName.getText() == ""
						|| dialAddPhnoTf.getText() == null || dialAddPhnoTf.getText() == "") {
					callAlert("입력 값 오류 : 이름과 전화번호는 필수입력사항입니다");
					return;
				} else if (dialAddPhnoTf.getText().length() > 20) {
					callAlert("입력 값 오류 : 전화번호는 20자리 이하로 입력하세요");
					return;
				}
	
				else if ((dialAddPhnoTfeMail.getText() != null || dialAddPhnoTfeMail.getText() != "")
						&& (imagePath != null || imagePath != "")) {
					if (!dialAddPhnoTf.getText().equals(addressBookDAO.searchExistedPnNo(dialAddPhnoTf.getText()))) {
						int flag = addressBookDAO.insertPhNewOne(new AddressBook(dialAddPhnoTfName.getText(),
								dialAddPhnoTfeMail.getText(), dialAddPhnoTf.getText(), imagePath));
						if (flag == 1) {
							addPhNoStage.close();
							//callAlert("AddressBook DB 입력 성공 : 새 연락처를 DB에 저장했습니다");
						} else {
							callAlert("AddressBook DB 입력 오류 : 새 연락처를 DB에 저장하지 못 했습니다");
						}
					} else {
						callAlert("AddressBook DB 입력 오류 : 이미 저장되어있는 번호입니다");
					}
				}
			
				else if ((dialAddPhnoTfeMail.getText() == null || dialAddPhnoTfeMail.getText() == "")
						&& (imagePath != null || imagePath != "")) {
					if (!dialAddPhnoTf.getText().equals(addressBookDAO.searchExistedPnNo(dialAddPhnoTf.getText()))) {
						int flag = addressBookDAO.insertPhNewOne(
								new AddressBook(dialAddPhnoTfName.getText(), null, dialAddPhnoTf.getText(), imagePath));
						if (flag == 1) {
							addPhNoStage.close();
							//callAlert("AddressBook DB 입력 성공 : 새 연락처를 DB에 저장했습니다");
						} else {
							callAlert("AddressBook DB 입력 오류 : 새 연락처를 DB에 저장하지 못 했습니다");
						}
					} else {
						callAlert("AddressBook DB 입력 오류 : 이미 저장되어있는 번호입니다");
					}
				}
			
				else if ((dialAddPhnoTfeMail.getText() != null || dialAddPhnoTfeMail.getText() != "")
						&& (imagePath == null || imagePath == "")) {
					if (!dialAddPhnoTf.getText().equals(addressBookDAO.searchExistedPnNo(dialAddPhnoTf.getText()))) {
						int flag = addressBookDAO.insertPhNewOne(new AddressBook(dialAddPhnoTfName.getText(),
								dialAddPhnoTfeMail.getText(), dialAddPhnoTf.getText(), null));
						if (flag == 1) {
							addPhNoStage.close();
							//callAlert("AddressBook DB 입력 성공 : 새 연락처를 DB에 저장했습니다");
						} else {
							callAlert("AddressBook DB 입력 오류 : 새 연락처를 DB에 저장하지 못 했습니다");
						}
					} else {
						callAlert("AddressBook DB 입력 오류 : 이미 저장되어있는 번호입니다");
					}
				}
		
				else if ((dialAddPhnoTfeMail.getText() == null || dialAddPhnoTfeMail.getText() == "")
						&& (imagePath == null || imagePath == "")) {
					if (!dialAddPhnoTf.getText().equals(addressBookDAO.searchExistedPnNo(dialAddPhnoTf.getText()))) {
						int flag = addressBookDAO.insertPhNewOne(
								new AddressBook(dialAddPhnoTfName.getText(), null, dialAddPhnoTf.getText(), null));
						if (flag == 1) {
							addPhNoStage.close();
							//callAlert("AddressBook DB 입력 성공 : 새 연락처를 DB에 저장했습니다");
						} else {
							callAlert("AddressBook DB 입력 오류 : 새 연락처를 DB에 저장하지 못 했습니다");
						}
					} else {
						callAlert("AddressBook DB 입력 오류 : 이미 저장되어있는 번호입니다");
					}
				}
			});

		} catch (Exception e) {
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
