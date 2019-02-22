package Controller;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TalkSignUpRootController implements Initializable{
	
	@FXML private Label fssLblTime;	
	static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("hh:mm:ss a");
	int count=0;

	public Stage talkSignUpStage;
	
	@FXML private TextField suTfUserId;
	@FXML private Button suBtnFindId;
	@FXML private PasswordField suPwfPut;
	@FXML private PasswordField suPwfCheck;
	@FXML private TextField suTfPhNo;
	@FXML private Button suBtnFindPhNo;
	@FXML private TextField suTfUserNic;
	@FXML private Label suLblState;
	@FXML private Button suBtnSign;
	@FXML private Button suBtnCancel;
	@FXML private Label suLblFindId;
	@FXML private Label suLblFindPw;
	UserDAO userDAO = new UserDAO();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		setUpLocalTimeFSS();
		
		suPwfPut.setDisable(true);
		suPwfCheck.setDisable(true);
		suTfPhNo.setDisable(true);
		suBtnFindPhNo.setDisable(true);
		suTfUserNic.setDisable(true);

		// 01. 아이디 체크 버튼
		suTfUserId.setOnKeyPressed(eFindId -> {
			if (eFindId.getCode() == KeyCode.ENTER)
				handleSuBtnFindIdAction();
		});
		suBtnFindId.setOnAction(eFindId -> handleSuBtnFindIdAction());

		// 02. 전화번호 확인
		suTfPhNo.setOnKeyPressed(eFindPw -> {
			if (eFindPw.getCode() == KeyCode.ENTER)
				handleSuBtnFindPhNoAction();
		});
		suBtnFindPhNo.setOnAction(eFindPw -> handleSuBtnFindPhNoAction());

		// 03. 회원가입 버튼. 항목 null값 확인, PW확인.
		suBtnSign.setOnAction(eSignUp -> handleSuBtnSignAction());
		suTfUserNic.setOnKeyPressed(eSignUp -> {
			if (eSignUp.getCode() == KeyCode.ENTER)
				handleSuBtnSignAction();
		});

		// 04. 아이디 찾기 라벨 hover
		suLblFindId.setOnMouseClicked(eSearchId -> handleSuLblFindIdAction());

		// 05. 패스워드 찾기 라벨 hover
		suLblFindPw.setOnMouseClicked(eSearchPw -> handleSuLblFindPwAction());

		// 06. 취소버튼
		suBtnCancel.setOnAction(eGo2Login -> handleSuBtnCancelAction());

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

	// 01. 아이디 체크 버튼
	private void handleSuBtnFindIdAction() {
		if (suTfUserId.getText().trim().equals("")) {
			suLblState.setText("이메일을 입력하세요");
			return;
		}
		String id = userDAO.searchExistedUserId(suTfUserId.getText());
		if (!(suTfUserId.getText().contains("@") && suTfUserId.getText().contains(".")))
			suLblState.setText("Use Email Address\n이메일 형식이 아닙니다");
		else if (id != null) {
			suLblState.setText("Please Use other ID\n이미 사용 중인 ID입니다");
		} else {
			suPwfPut.setDisable(false);
			suPwfCheck.setDisable(false);
			suTfPhNo.setDisable(false);
			suBtnFindPhNo.setDisable(false);
			suTfUserNic.setDisable(true);
			suLblState.setText("We Checked Your ID\n사용 가능한 ID입니다");
		}
	}

	// 02. 전화번호 확인
	private void handleSuBtnFindPhNoAction() {
		if (suTfPhNo.getText().trim().equals("")) {
			suLblState.setText("전화번호를 입력하세요");
			return;
		}
		String dbPhNo = userDAO.newSelectUserPhNo(suTfPhNo.getText());
		if (!(suTfPhNo.getText().contains("-") && (suTfPhNo.getText().length() == 13)))
			suLblState.setText("Use PhoneNo Format\n전화번호 형식이 맞지 않습니다");
		else if (suTfPhNo.getText().contains("-") && suTfPhNo.getText().length() == 13 && dbPhNo != null) {
			String[] dbPhoneNo = dbPhNo.split("-");
			String[] inputPhoneNo = suTfPhNo.getText().split("-");
			if ((inputPhoneNo[0] + inputPhoneNo[1] + inputPhoneNo[2])
					.equals(dbPhoneNo[0] + dbPhoneNo[1] + dbPhoneNo[2]))
				suLblState.setText("이미 가입되어 있는 전화번호 입니다");
		} else {
			suLblState.setText("사용가능한 전화번호 입니다");
			suPwfPut.setDisable(false);
			suPwfCheck.setDisable(false);
			suTfPhNo.setDisable(false);
			suBtnFindPhNo.setDisable(false);
			suTfUserNic.setDisable(false);
		}

	}

	// 03. 가입버튼. 항목 null값 확인, PW확인.
	private void handleSuBtnSignAction() {
		if (suTfUserId.getText().trim().equals(null)) {
			suLblState.setText("아이디를 입력하지 않으셨습니다");
		} else if (suPwfPut.getText().trim().equals(null) || suPwfCheck.getText().trim().equals(null)) {
			suLblState.setText("비밀번호를 입력하지 않으셨습니다");
		} else if (!((suPwfPut.getText().trim().length() >= 8 && suPwfPut.getText().trim().length() <= 16)
				|| (suPwfCheck.getText().trim().length() >= 8 && suPwfCheck.getText().trim().length() <= 16))) {
			suLblState.setText("비밀번호를 8자 이상 16자리 이내로 입력하세요");
		} else if (!suPwfPut.getText().trim().equals(suPwfCheck.getText().trim())) {
			suLblState.setText("비밀번호가 일치하지 않습니다");
			suPwfCheck.setText(null);
		} else if (suTfPhNo.getText().trim().equals(null)) {
			suLblState.setText("전화번호를 입력하지 않으셨습니다");
		} else if (!((suTfPhNo.getText().length() >= 11) && (suTfPhNo.getText().length() <= 13))) {
			suLblState.setText("전화번호 형식을 맞추어 주세요");
		} else if (suTfUserNic.getText().trim().equals(null)) {
			suLblState.setText("닉네임을 입력하지 않으셨습니다");
		} else if (!suTfUserId.getText().trim().equals("") && !suPwfPut.getText().trim().equals("")
				&& !suPwfCheck.getText().trim().equals("") && !suTfPhNo.getText().trim().equals("")
				&& !suTfUserNic.getText().trim().equals("")) {

			User newUser = new User(suTfUserId.getText().trim(), suPwfPut.getText().trim(), suTfPhNo.getText().trim(),
					suTfUserNic.getText().trim(), null, null);
			int num = userDAO.insertSignUpUser(newUser);
			if (num == 1) {
				//callAlert("회원가입에 성공하였습니다 : VaTalk 회원이 되셨습니다.\n로그인 해주세요");
				try {
					Stage talkMainStage = new Stage(StageStyle.UTILITY);
					FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/talk_login.fxml"));
					Parent talkMainRoot = loader.load();
					TalkLoginRootController talkMainController = loader.getController();
					talkMainController.talkLoginStage = talkMainStage;
					Scene talkMainScene = new Scene(talkMainRoot);
					talkMainScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
					talkMainStage.setScene(talkMainScene);
					talkMainStage.setResizable(false);
					talkMainStage.setTitle("Va Talk LOGIN");
					talkSignUpStage.close();
					talkMainStage.show();
					//callAlert("화면 전환 성공 : Va톡 화면으로 전환 되었습니다.");
				} catch (Exception e) {
					callAlert("화면 전환 오류 : Va톡 화면 전환에 문제가 있습니다. 검토바람");
					e.printStackTrace();
				}
			} else if (num == 0) {
				suLblState.setText("회원가입을 다시 진행 해주세요");
				callAlert("회원가입 실패 : 회원가입에 실패 하였습니다");
			}
		}
	}

	// 04. 아이디 찾기 라벨 hover
	private void handleSuLblFindIdAction() {
		Parent searchIdRoot = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/talk_search_id.fxml"));
			searchIdRoot = loader.load();
			Stage srchIdStage = new Stage(StageStyle.UTILITY);
			srchIdStage.initModality(Modality.NONE);
			srchIdStage.initOwner(talkSignUpStage);
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

	// 05. 패스워드 찾기 라벨 hover
	private void handleSuLblFindPwAction() {
		Parent searchPwRoot = null;
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/talk_search_pw.fxml"));
			searchPwRoot = loader.load();
			Stage srchPwStage = new Stage(StageStyle.UTILITY);
			srchPwStage.initModality(Modality.NONE);
			srchPwStage.initOwner(talkSignUpStage);
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
			e.printStackTrace();
		}
	}

	// 06. 취소버튼
	private void handleSuBtnCancelAction() {
		try {
			Stage talkMainStage = new Stage(StageStyle.UTILITY);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/talk_login.fxml"));
			Parent talkMainRoot = loader.load();
			TalkLoginRootController talkMainController = loader.getController();
			talkMainController.talkLoginStage = talkMainStage;
			Scene talkMainScene = new Scene(talkMainRoot);
			talkMainScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			talkMainStage.setScene(talkMainScene);
			talkMainStage.setResizable(false);
			talkMainStage.setTitle("Va Talk LOGIN");
			talkSignUpStage.close();
			talkMainStage.show();
			//callAlert("화면 전환 성공 : Va톡 화면으로 전환 되었습니다.");
		} catch (Exception e) {
			callAlert("화면 전환 오류 : Va톡 화면 전환에 문제가 있습니다. 검토바람");
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
