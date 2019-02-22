package Controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import Model.Chatter;
import Model.User;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TalkLoginRootController implements Initializable {

	@FXML private Label fssLblTime;	
	static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("hh:mm:ss a");
	int count=0;

	public Stage talkLoginStage;
	@FXML public Button scsBtnWifi;	
	@FXML public Button scsBtnVolume;
	@FXML public Button scsBtnMemo;
	@FXML public Button scsBtnCamera;
	@FXML public Button scsBtnMusic;
	@FXML private ImageView logImgVLogo;
	@FXML private TextField logTfId;
	@FXML private PasswordField logPwf;
	@FXML private Label logLblState;
	@FXML private Button logBtnLogin;
	@FXML private Label logLblSignUp;
	@FXML private Label logLblFindID;
	@FXML private Label logLblFindPw;
	static Chatter chatter;
	UserDAO userDAO = new UserDAO();
	static User user=null;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		setUpLocalTimeFSS();

		// 00. 디버깅용 id,pw set
		scsBtnWifi.setOnAction(eAuto -> {
			logTfId.setText("test1@naver.com");
			logPwf.setText("asdf1234");
		});
		scsBtnVolume.setOnAction(eAuto -> {
			logTfId.setText("test2@naver.com");
			logPwf.setText("asdf1234");
		});
		scsBtnMemo.setOnAction(eAuto -> {
			logTfId.setText("test3@naver.com");
			logPwf.setText("asdf1234");
		});
		scsBtnCamera.setOnAction(eAuto -> {
			logTfId.setText("test4@naver.com");
			logPwf.setText("asdf1234");
		});
		scsBtnMusic.setOnAction(eAuto -> {
			logTfId.setText("test5@naver.com");
			logPwf.setText("asdf1234");
		});
		
		// 01. 로그인 버튼
		logBtnLogin.setOnAction(eLogin -> handleLogBtnLoginAction());
		logPwf.setOnKeyPressed(eFindId -> {
			if (eFindId.getCode() == KeyCode.ENTER)
				handleLogBtnLoginAction();
		});

		// 02. 회원가입 라벨 hover
		logLblSignUp.setOnMouseClicked(eSignUp -> handleLogLblSignUpAction());

		// 03. 아이디 찾기 라벨 hover
		logLblFindID.setOnMouseClicked(eSearchId -> handleLogLblFindIDAction());

		// 04. 패스워드 찾기 라벨 hover
		logLblFindPw.setOnMouseClicked(eSearchPw -> handleLogLblFindPWAction());

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
	
	// 01. 로그인 버튼
	private void handleLogBtnLoginAction() {
		if (logTfId.getText().trim().equals("") || logPwf.getText().trim().equals("")) {
			logLblState.setText("아이디, 비밀번호를 입력하세요");
		} else if (!(logTfId.getText().contains("@") && logTfId.getText().contains("."))) {
			logLblState.setText("Use Email Address\n이메일 형식이 아닙니다");
		} else if (logPwf.getText().trim().equals(null)) {
			logLblState.setText("비밀번호를 입력하지 않으셨습니다");
		} else if (!((logPwf.getText().trim().length() >= 8 && logPwf.getText().trim().length() <= 16))) {
			logLblState.setText("비밀번호를 8자 이상 16자리 이내로 입력하세요");
		} else {
			user = userDAO.selectLoginUser(logTfId.getText().trim(), logPwf.getText().trim());
			if (user != null) {
				int logOn = userDAO.updateUserLogOn(user, "on");
//				if (logOn == 1)
//					callAlert("로그인 성공 : DB에 로그인 상태로 업데이트 되었습니다.");
//				else
//					callAlert("로그인 실패 : DB의 로그인 값이 변경되지 않았습니다.");
				if (logOn != 1)
					callAlert("로그인 실패 : DB의 로그인 값이 변경되지 않았습니다.");
				try {
					Stage talkMainStage = new Stage(StageStyle.UTILITY);
					FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/talk_profile_friend.fxml"));
					Parent talkMainRoot = loader.load();
					TalkProFriRootController talkMainController = loader.getController();
					talkMainController.talkProFriStage = talkMainStage;
					Scene talkMainScene = new Scene(talkMainRoot);
					talkMainScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
					talkMainStage.setScene(talkMainScene);
					talkMainStage.setTitle("PROFILE and FRIEND");
					talkMainStage.setResizable(false);
					talkLoginStage.close();
					talkMainStage.show();
					callAlert("Login Succeeded : " + user.getNicName() + " 님 환영합니다.");
				} catch (Exception e) {
					callAlert("Login Failed : 아이디, 패스워드를 확인하세요");
					e.printStackTrace();
				}

			} else {
				logLblState.setText("올바른 아이디, 비밀번호를 입력하세요");
				callAlert("Can't found : 회원이 아니시면 회원가입을 해주세요");
				logPwf.clear();
			} // else 값 입력o
		} // else 값 입력x
	}// handlelogBtnLoginAction

	// 02. 회원가입을 눌렀을 때
	private void handleLogLblSignUpAction() {
		try {
			Stage talkMainStage = new Stage(StageStyle.UTILITY);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/talk_sign_up.fxml"));
			Parent talkMainRoot = loader.load();
			TalkSignUpRootController talkMainController = loader.getController();
			talkMainController.talkSignUpStage = talkMainStage;
			Scene talkMainScene = new Scene(talkMainRoot);
			talkMainScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			talkMainStage.setScene(talkMainScene);
			talkMainStage.setResizable(false);
			talkMainStage.setTitle("Va Talk SIGN UP");
			talkLoginStage.close();
			talkMainStage.show();
			callAlert("Sign Up : VaTalk의 회원이 되어보세요 \nThank U for Join Us");
		} catch (Exception e) {
			callAlert("Connect Failed : 서버와 연결 할 수 없습니다.");
			e.printStackTrace();
		}
	}

	// 03. 아이디 찾기 라벨 hover
	private void handleLogLblFindIDAction() {
		Parent searchIdRoot = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/talk_search_id.fxml"));
			searchIdRoot = loader.load();
			Stage srchIdStage = new Stage(StageStyle.UTILITY);
			srchIdStage.initModality(Modality.NONE);
			srchIdStage.initOwner(talkLoginStage);
			srchIdStage.setTitle("ID SEARCH");
			srchIdStage.setResizable(false);
			Scene srchIdScene = new Scene(searchIdRoot);
			srchIdScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			srchIdStage.setScene(srchIdScene);
			srchIdStage.show();
			Label srchLblState = (Label) searchIdRoot.lookup("#srchLblState");
			TextField srchTfPhNo = (TextField) searchIdRoot.lookup("#srchTfPhNo");
			Button srchBtnFindId = (Button) searchIdRoot.lookup("#srchBtnFindId");
			Button srchBtnCancel = (Button) searchIdRoot.lookup("#srchBtnCancel");

			srchBtnFindId.setOnAction(ePhNoForId -> {
				if (srchTfPhNo.getText().trim().equals("")) {
					srchLblState.setText("전화번호를 입력하세요");
					return;
				}
				if (!(srchTfPhNo.getText().contains("-") && (srchTfPhNo.getText().length() == 13))) {
					srchLblState.setText("Use PhoneNo Format\n전화번호 형식이 맞지 않습니다");
					return;
				} else {
					String dbPhNo = userDAO.newSelectUserPhNo(srchTfPhNo.getText());
					if (dbPhNo != null) {
						String[] dbPhoneNo = dbPhNo.split("-");
						String[] inputPhoneNo = srchTfPhNo.getText().split("-");
						if ((inputPhoneNo[0] + inputPhoneNo[1] + inputPhoneNo[2])
								.equals(dbPhoneNo[0] + dbPhoneNo[1] + dbPhoneNo[2])) {
							String dbID = userDAO.searchUserIdByPhNo(srchTfPhNo.getText());
							if (dbID != null) {
								srchLblState.setText("찾으신 아이디는\n" + dbID + " 입니다");
								callAlert("회원확인 : 이미 회원이십니다");
							} else {
								srchLblState.setText("해당 아이디가 존재하지 않습니다");
							}
						}
					}
				}
			});
			srchTfPhNo.setOnKeyPressed(ePhNoForId -> {
				if (ePhNoForId.getCode() == KeyCode.ENTER) {
					if (srchTfPhNo.getText().trim().equals("")) {
						srchLblState.setText("전화번호를 입력하세요");
						return;
					}
					if (!(srchTfPhNo.getText().contains("-") && (srchTfPhNo.getText().length() == 13))) {
						srchLblState.setText("Use PhoneNo Format\n전화번호 형식이 맞지 않습니다");
						return;
					} else {
						String dbPhNo = userDAO.newSelectUserPhNo(srchTfPhNo.getText());
						if (dbPhNo != null) {
							String[] dbPhoneNo = dbPhNo.split("-");
							String[] inputPhoneNo = srchTfPhNo.getText().split("-");
							if ((inputPhoneNo[0] + inputPhoneNo[1] + inputPhoneNo[2])
									.equals(dbPhoneNo[0] + dbPhoneNo[1] + dbPhoneNo[2])) {
								String dbID = userDAO.searchUserIdByPhNo(srchTfPhNo.getText());
								if (dbID != null) {
									srchLblState.setText("찾으신 아이디는\n" + dbID + " 입니다");
									callAlert("회원확인 : 이미 회원이십니다");
								} else {
									srchLblState.setText("해당 아이디가 존재하지 않습니다");
								}
							}
						}
					} 
				}
			});

			srchBtnCancel.setOnAction(eClose -> srchIdStage.hide());
			srchBtnCancel.setOnKeyPressed(eClose -> {
				if (eClose.getCode() == KeyCode.ENTER)
					srchIdStage.hide();
			});
			srchIdStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent eClose) -> {
				if (eClose.getCode() == KeyCode.ESCAPE)
					srchIdStage.hide();
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 04. 패스워드 찾기 라벨 hover
	private void handleLogLblFindPWAction() {
		Parent searchPwRoot = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/talk_search_pw.fxml"));
			searchPwRoot = loader.load();
			Stage srchPwStage = new Stage(StageStyle.UTILITY);
			srchPwStage.initModality(Modality.NONE);
			srchPwStage.initOwner(talkLoginStage);
			srchPwStage.setTitle("PW SEARCH");
			srchPwStage.setResizable(false);
			Scene srchPwScene = new Scene(searchPwRoot);
			srchPwScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			srchPwStage.setScene(srchPwScene);
			srchPwStage.show();
			Label srchLblState = (Label) searchPwRoot.lookup("#srchLblState");
			TextField srchTfId = (TextField) searchPwRoot.lookup("#srchTfId");
			Button srchBtnFindPW = (Button) searchPwRoot.lookup("#srchBtnFindPW");
			Button srchBtnCancel = (Button) searchPwRoot.lookup("#srchBtnCancel");

			srchBtnFindPW.setOnAction(eIdForPw -> {
				if (srchTfId.getText().trim().equals("")) {
					srchLblState.setText("이메일을 입력하세요");
					return;
				}
				if (!(srchTfId.getText().contains("@") && srchTfId.getText().contains("."))) {
					srchLblState.setText("Use Email Address\n이메일 형식이 아닙니다");
					return;
				} else {
					String dbId = userDAO.searchExistedUserId(srchTfId.getText().trim());

					if (dbId != null) { 
						String dbPW = userDAO.searchUserPwById(srchTfId.getText());
						if (dbPW != null) {
							srchLblState.setText("찾으신 패스워드는 " + dbPW + " 입니다");
						} else {
							/*********************** 이메일로 비밀번호 랜덤전송 ***************************/
							srchLblState.setText("관리자를 통해 비밀번호를 재설정 해주세요");
						}
					} else {
						srchLblState.setText("Can't find ID\n해당 아이디가 없습니다");
						return;
					}
				}
			});

			srchTfId.setOnKeyPressed(eIdForPw -> {
				if (eIdForPw.getCode() == KeyCode.ENTER) {
					if (srchTfId.getText().trim().equals("")) {
						srchLblState.setText("이메일을 입력하세요");
						return;
					}
					if (!(srchTfId.getText().contains("@") && srchTfId.getText().contains("."))) {
						srchLblState.setText("Use Email Address\n이메일 형식이 아닙니다");
						return;
					} else {
						String dbId = userDAO.searchExistedUserId(srchTfId.getText().trim());

						if (dbId != null) { 
							String dbPW = userDAO.searchUserPwById(srchTfId.getText());
							if (dbPW != null) {
								srchLblState.setText("찾으신 패스워드는 " + dbPW + " 입니다");
							} else {
								/*********************** 이메일로 비밀번호 랜덤전송 ***************************/
								srchLblState.setText("관리자를 통해 비밀번호를 재설정 해주세요");
							}
						} else {
							srchLblState.setText("Can't find ID\n해당 아이디가 없습니다");
							return;
						}
					}
				}
			});

			srchBtnCancel.setOnAction(eClose -> srchPwStage.hide());
			srchBtnCancel.setOnKeyPressed(eClose -> {
				if (eClose.getCode() == KeyCode.ENTER)
					srchPwStage.hide();
			});
			srchPwStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent eClose) -> {
				if (eClose.getCode() == KeyCode.ESCAPE)
					srchPwStage.hide();
			});

		} catch (Exception e) {
		}
	}

	// 기타 오류처리 함수 "오류정보 : 오류 상세 메세지"
	public static void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("알림창");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":") + 2));
		alert.showAndWait();
	}
}
