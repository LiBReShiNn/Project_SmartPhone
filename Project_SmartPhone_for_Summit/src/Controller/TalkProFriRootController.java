package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import Model.Chatter;
import Model.Friends;
import Model.Room;
import Model.User;
import javafx.application.Platform;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class TalkProFriRootController implements Initializable{

	@FXML private Label fssLblTime;	
	static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("hh:mm:ss a");
	int count=0;

	static User user = TalkLoginRootController.user;
    User friend;
    User friendFromList;
	Friends friends;
	
	public Stage talkProFriStage;
	public Stage talkFriProfileStage;
	public Stage talkChattingRoomStage;
	public Stage talkSubEditUserStage;
	public Stage talkSubStageFindID;
	public Stage talkFindSubStage;
	public Stage talkSubChatListStage;
	
	@FXML private Button chLFriBtnFindFri;
	@FXML private Button chLFriBtnSettings;
	@FXML private HBox chLFriHBProf;
	@FXML private ImageView chLFriImgVUserPic;
	@FXML private Label chLFriLblUserNic;
	@FXML private ListView<User> chLFriListVFriend;
	@FXML private Button chLFriBtn2FriList;
	@FXML private Button chLFriBtn2ChatList;
	@FXML private Button chLFriBtnMakeGrp;
	
	UserDAO userDAO = new UserDAO();
	FriendsDAO friendsDAO = new FriendsDAO();
	RoomDAO roomDAO = new RoomDAO();
	static Chatter chatter=TalkLoginRootController.chatter;
	File file;
	ArrayList<User> joinner = new ArrayList<User>();
	ArrayList<User> groupChatJoinner = new ArrayList<User>();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		setUpLocalTimeFSS();

		// 00. 로그인시 서버와 연결
		user = userDAO.setUpUserInfo(user.getId());
		chatter = new Chatter(user);
		chatter.start();

		// 01. 사용자 이미지 닉네임 셋팅
		setUpUserPicNic();

		// 02. 자신의 이미지, 닉네임이 있는 HBox영역을 누르면 회원정보 수정창으로 변경
		chLFriHBProf.setOnMouseClicked(eEditProfile -> {
			if (eEditProfile.getClickCount() == 2)
				handleHboxEditProfileAction();
		});

		// 03. 친구추가
		chLFriBtnFindFri.setOnAction(eFindFriend -> handleChLFriBtnFindFri());

		// 04. 리스트뷰에 친구 띄우기
		setUpFriendList();

		chLFriListVFriend.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		// 05. 리스트뷰에서 더블클릭하면 친구의 프로필이 뜸
		chLFriListVFriend.setOnMouseClicked(eMakeNewChat -> {
			if (eMakeNewChat.getClickCount() == 2)
				handleClickedFriList();
		});

		// 06. 친구목록을 누르면 친구리스트를 갱신
		chLFriBtn2FriList.setOnAction(eGo2FriendList -> {
			setUpFriendList();
		});

		// 07. 채팅목록을 누르면 채팅 리스트를 팝업으로 띄움
		chLFriBtn2ChatList.setOnAction(eGo2ChattingList -> setUpChatList());

		// 08. 리스트뷰에서 다중선택 후 방만들기 버튼을 누르면 단체방이 만들어짐
		chLFriBtnMakeGrp.setOnAction(eMakeJoinRoom -> handleMakeJoinRoom());

		// 09. 로그아웃
		chLFriBtnSettings.setOnAction(eLogOut -> updateLogOutState());

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

	// 01. 사용자 이미지 닉네임 셋팅
	private void setUpUserPicNic() {
		chLFriLblUserNic.setText(user.getNicName());
		if (user.getImagePath() == null || user.getImagePath().equals("")) {
			//callAlert("이미지 없음 : 등록된 사진이 없습니다");
		} else if (user.getImagePath() != null && !user.getImagePath().equals("")) {
			try {
				FileInputStream fileInputStream = new FileInputStream(user.getImagePath());
				chLFriImgVUserPic.setImage(new Image(fileInputStream));
			} catch (Exception e) {
				callAlert("이미지 경로 오류 : 이미지 경로가 올바르지 않습니다");
			}
		}
	}

	// 02. 자신의 이미지, 닉네임이 있는 HBox영역을 누르면 회원정보 수정창으로 변경
	private void handleHboxEditProfileAction() {
		try {
			Stage talkSubEditUserStage = new Stage(StageStyle.UTILITY);
			this.talkSubEditUserStage = talkSubEditUserStage;
			FXMLLoader subEditUserLoader = new FXMLLoader(getClass().getResource("../View/talk_profile_edit.fxml"));
			Parent talkSubEditUserRoot = subEditUserLoader.load();
			Scene talkSubEditUserScene = new Scene(talkSubEditUserRoot);
			talkSubEditUserScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			talkSubEditUserStage.setScene(talkSubEditUserScene);
			talkSubEditUserStage.setResizable(false);
			talkSubEditUserStage.setTitle("Va Talk PROFILE EDIT");
			talkSubEditUserStage.show();

			ImageView pfeImgVUserPic = (ImageView) talkSubEditUserRoot.lookup("#pfeImgVUserPic");
			TextField pfeTfUserId = (TextField) talkSubEditUserRoot.lookup("#pfeTfUserId");
			PasswordField pfePwfPut = (PasswordField) talkSubEditUserRoot.lookup("#pfePwfPut");
			PasswordField pfePwfCheck = (PasswordField) talkSubEditUserRoot.lookup("#pfePwfCheck");
			TextField pfeTfPhNo = (TextField) talkSubEditUserRoot.lookup("#pfeTfPhNo");
			TextField pfeTfUserNic = (TextField) talkSubEditUserRoot.lookup("#pfeTfUserNic");
			Label pfeLblState = (Label) talkSubEditUserRoot.lookup("#pfeLblState");
			Button pfeBtnEdit = (Button) talkSubEditUserRoot.lookup("#pfeBtnEdit");
			Button pfeBtnExit = (Button) talkSubEditUserRoot.lookup("#pfeBtnExit");
			Label pfeLblDeleteUser = (Label) talkSubEditUserRoot.lookup("#pfeLblDeleteUser");

			pfeTfUserId.setText(user.getId());
			pfeTfUserId.setDisable(true);
			pfePwfPut.setText(user.getPw());
			pfePwfCheck.setText(user.getPw());
			pfeTfPhNo.setText(user.getPhoneNo());
			pfeTfPhNo.setDisable(true);
			pfeTfUserNic.setText(user.getNicName());

			if (user.getImagePath() == null || user.getImagePath().equals("")) {
				//callAlert("이미지 없음 : 등록된 사진이 없습니다");
			} else if (user.getImagePath() != null && !user.getImagePath().equals("")) {
				try {
					FileInputStream fileInputStream = new FileInputStream(user.getImagePath());
					pfeImgVUserPic.setImage(new Image(fileInputStream));
				} catch (Exception e) {
					callAlert("이미지 경로 오류 : 이미지 경로가 올바르지 않습니다");
				}
			}

			pfeImgVUserPic.setOnMouseClicked(eEidtPic -> {
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
						file = fileChooser.showOpenDialog(talkSubEditUserStage);
						if (file != null) {
							user.setImagePath(file.getPath());
							FileInputStream fileInputStream = new FileInputStream(user.getImagePath());
							pfeImgVUserPic.setImage(new Image(fileInputStream));
						}

					} catch (Exception e) {
						callAlert("이미지 파일 로딩 오류 : 이미지 파일을 확인하세요");
						e.printStackTrace();
					}
				}
			});

			talkSubEditUserStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent eEditSavePWNicImgPath) -> {
				if (eEditSavePWNicImgPath.getCode() == KeyCode.ENTER) {
					if (pfePwfPut.getText().trim().equals(null) || pfePwfCheck.getText().trim().equals(null)) {
						pfeLblState.setText("비밀번호를 입력하지 않으셨습니다");
					} else if (!((pfePwfPut.getText().trim().length() >= 8 && pfePwfPut.getText().trim().length() <= 16)
							|| (pfePwfCheck.getText().trim().length() >= 8
									&& pfePwfCheck.getText().trim().length() <= 16))) {
						pfeLblState.setText("비밀번호를 8자 이상 16자리 이내로 입력하세요");
					} else if (!pfePwfPut.getText().trim().equals(pfePwfCheck.getText().trim())) {
						pfeLblState.setText("비밀번호가 일치하지 않습니다");
						pfePwfCheck.setText(null);
					} else if (pfeTfUserNic.getText().trim().equals(null)) {
						pfeLblState.setText("닉네임을 입력하지 않으셨습니다");
					} else if (!pfeTfUserId.getText().trim().equals("") && !pfePwfPut.getText().trim().equals("")
							&& !pfePwfCheck.getText().trim().equals("") && !pfeTfPhNo.getText().trim().equals("")
							&& !pfeTfUserNic.getText().trim().equals("")) {

						user.setPw(pfePwfPut.getText().trim());
						user.setNicName(pfeTfUserNic.getText().trim());

						int flag = userDAO.updateUserEdit(user);
						if (flag == 1) {
							chatter.setUser(user);
							pfeLblState.setText("정보를 수정했습니다");
							//callAlert("DB 입력 성공 : 수정 된 정보를 DB에 저장했습니다");
						} else {
							pfeLblState.setText("정보 수정을 다시 진행 해주세요");
							callAlert("DB 입력 오류 : 수정 된 정보를 DB에 저장하지 못 했습니다");
						}
					}
				}
			});

			pfeBtnEdit.setOnAction(eEditSavePWNicImgPath -> {
				if (pfePwfPut.getText().trim().equals(null) || pfePwfCheck.getText().trim().equals(null)) {
					pfeLblState.setText("비밀번호를 입력하지 않으셨습니다");
				} else if (!((pfePwfPut.getText().trim().length() >= 8 && pfePwfPut.getText().trim().length() <= 16)
						|| (pfePwfCheck.getText().trim().length() >= 8
								&& pfePwfCheck.getText().trim().length() <= 16))) {
					pfeLblState.setText("비밀번호를 8자 이상 16자리 이내로 입력하세요");
				} else if (!pfePwfPut.getText().trim().equals(pfePwfCheck.getText().trim())) {
					pfeLblState.setText("비밀번호가 일치하지 않습니다");
					pfePwfCheck.setText(null);
				} else if (pfeTfUserNic.getText().trim().equals(null)) {
					pfeLblState.setText("닉네임을 입력하지 않으셨습니다");
				} else if (!pfeTfUserId.getText().trim().equals("") && !pfePwfPut.getText().trim().equals("")
						&& !pfePwfCheck.getText().trim().equals("") && !pfeTfPhNo.getText().trim().equals("")
						&& !pfeTfUserNic.getText().trim().equals("")) {

					user.setPw(pfePwfPut.getText().trim());
					user.setNicName(pfeTfUserNic.getText().trim());

					int flag = userDAO.updateUserEdit(user);
					if (flag == 1) {
						chatter.setUser(user);
						pfeLblState.setText("정보를 수정했습니다");
						//callAlert("DB 입력 성공 : 수정 된 정보를 DB에 저장했습니다");
					} else {
						pfeLblState.setText("정보 수정을 다시 진행 해주세요");
						callAlert("DB 입력 오류 : 수정 된 정보를 DB에 저장하지 못 했습니다");
					}
				}
			});

			pfeBtnExit.setOnAction(eBack2Profile -> talkSubEditUserStage.close());

			talkSubEditUserStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent eClose) -> {
				if (eClose.getCode() == KeyCode.ESCAPE)
					talkSubEditUserStage.hide();
			});

			pfeLblDeleteUser.setOnMouseClicked(eDeleteUser -> {
				if (eDeleteUser.getClickCount() == 2) {
					pfeLblState.setText("정말 탈퇴 하시겠습니까?\n탈퇴 하시려면 탈퇴하기를 한번 더 누르세요");
					pfeLblDeleteUser.setOnMouseClicked(eDelete -> {
						userDAO.deleteUser(user.getId());
						chatter.setSocket(null);
						chatter.setUser(null);
						try {
							Stage talkMainStage = new Stage(StageStyle.UTILITY);
							FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("../View/talk_login.fxml"));
							Parent talkMainRoot = loginLoader.load();
							TalkLoginRootController talkMainController = loginLoader.getController();
							talkMainController.talkLoginStage = talkMainStage;
							Scene talkMainScene = new Scene(talkMainRoot);
							talkMainScene.getStylesheets()
									.add(getClass().getResource("../css/talk_login.css").toString());
							talkMainStage.setScene(talkMainScene);
							talkMainStage.setResizable(false);
							talkMainStage.setTitle("Va Talk LOGIN");
							talkSubEditUserStage.close();
							talkMainStage.show();
							//callAlert("화면 전환 성공 : Va톡 화면으로 전환 되었습니다.");
						} catch (Exception e) {
							callAlert("화면 전환 오류 : Va톡 화면 전환에 문제가 있습니다. 검토바람");
							e.printStackTrace();
						}
					});
				}
			});

		} catch (Exception e) {
			callAlert("화면 전환 오류 : PROFILE EDIT 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}
	}

	// 03. 친구추가
	private void handleChLFriBtnFindFri() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../View/talk_select_howto_find.fxml"));
			Parent talkSubRoot = loader.load();
			Stage talkSubStage = new Stage(StageStyle.UNDECORATED);
			this.talkSubStageFindID = talkSubStage;
			talkSubStage.initModality(Modality.NONE);
			talkSubStage.initOwner(talkProFriStage);
			talkSubStage.setResizable(false);
			Scene talkSubScene = new Scene(talkSubRoot);
			talkSubScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			talkSubStage.setScene(talkSubScene);
			talkSubStage.show();

			AnchorPane imageAPane = (AnchorPane) talkSubRoot.lookup("#imageAPane");
			Button h2FindBtnAddrB = (Button) talkSubRoot.lookup("#h2FindBtnAddrB");
			Button h2FindBtnId = (Button) talkSubRoot.lookup("#h2FindBtnId");

			imageAPane.setOnMouseClicked(eExit -> {
				if (eExit.getClickCount() == 2)
					talkSubStage.close();
			});
			talkSubStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent eClose) -> {
				if (eClose.getCode() == KeyCode.ESCAPE)
					talkSubStage.close();
			});

			h2FindBtnAddrB.setOnAction(eFindByAddressBook -> {
				// 유저의 핸드폰 DB에서 값을 가져와서 서버의 DB와 비교
			});

			h2FindBtnId.setOnAction(eFindById -> {
				talkSubStage.close();
				Parent talkFindSubRoot = null;
				try {
					FXMLLoader findLoader = new FXMLLoader(getClass().getResource("../View/talk_find_friend_id.fxml"));
					talkFindSubRoot = findLoader.load();
					Stage talkFindSubStage = new Stage(StageStyle.UNDECORATED);
					this.talkFindSubStage = talkFindSubStage;
					talkFindSubStage.initModality(Modality.NONE);
					talkFindSubStage.initOwner(talkSubStage);
					talkFindSubStage.setResizable(false);
					Scene talkFindSubScene = new Scene(talkFindSubRoot);
					talkFindSubScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
					talkFindSubStage.setScene(talkFindSubScene);
					talkFindSubStage.show();

					AnchorPane findIdAPane = (AnchorPane) talkFindSubRoot.lookup("#findIdAPane");
					ImageView findFriImgVFri = (ImageView) talkFindSubRoot.lookup("#findFriImgVFri");
					Label findFriLblState = (Label) talkFindSubRoot.lookup("#findFriLblState");
					TextField findFriTfId = (TextField) talkFindSubRoot.lookup("#findFriTfId");
					Button findFriBtnFindId = (Button) talkFindSubRoot.lookup("#findFriBtnFindId");
					Button findFriBtnAddFri = (Button) talkFindSubRoot.lookup("#findFriBtnAddFri");

					findIdAPane.setOnMouseClicked(eFindExit -> {
						if (eFindExit.getClickCount() == 2) {
							talkSubStage.close();
							talkFindSubStage.close();
						}
					});

					talkFindSubStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent eFindClose) -> {
						if (eFindClose.getCode() == KeyCode.ESCAPE) {
							talkSubStage.close();
							talkFindSubStage.close();
						}
					});

					findFriTfId.setOnKeyPressed(eFindId -> {
						if (eFindId.getCode() == KeyCode.ENTER) {
							if (findFriTfId.getText().trim().equals("")) {
								findFriLblState.setText("검색 할 아이디를 입력하세요");
							} else if (!(findFriTfId.getText().contains("@") && findFriTfId.getText().contains("."))) {
								findFriLblState.setText("Use Email Address\n이메일 형식이 아닙니다");
							} else {

								User userFriend = userDAO.selectFindFriend(findFriTfId.getText().trim());
								if (userFriend != null) {
									if (userFriend.getImagePath() == null || userFriend.getImagePath().equals("")) {
										try {
											FileInputStream fileInputStream = new FileInputStream(
											"D:\\JAVA_PROJECT\\Project_SmartPhone\\src\\icons\\27. 친구\\친구 00-1.png");
											findFriImgVFri.setImage(new Image(fileInputStream));
										} catch (Exception e) {
											callAlert("이미지 경로 오류 : 이미지 경로가 올바르지 않습니다");
										}
									} else if (userFriend.getImagePath() != null
											&& !userFriend.getImagePath().equals("")) {
										try {
											FileInputStream fileInputStream = new FileInputStream(
													userFriend.getImagePath());
											findFriImgVFri.setImage(new Image(fileInputStream));
										} catch (Exception e) {
											callAlert("이미지 경로 오류 : 이미지 경로가 올바르지 않습니다");
										}
									}
									Friends userFriendCheck = friendsDAO.searchAddAlready(user.getId(),
											userFriend.getId());
									if (userFriendCheck != null)
										findFriLblState.setText("이미 친구인 회원입니다\n다른 친구를 찾아주세요");
									else {
										findFriLblState.setText("찾으시는 친구가 맞으면 추가를 눌러주세요");

										findFriBtnAddFri.setOnAction(eAddFriend -> {
											Friends friends = new Friends(user.getId(), userFriend.getId());
											int flag = friendsDAO.insertFriends(friends);
											if (flag == 1) {
												talkFindSubStage.close();
												//callAlert("친구 추가 성공 : DB에 친구를 추가 하였습니다");
												setUpFriendList();
												talkSubStage.close();
												talkFindSubStage.close();
											} else
												callAlert("친구 추가 실패 : DB에 친구를 추가하지 못 하였습니다");

										});
									}
								} else {
									findFriLblState.setText("찾으시는 회원이 없습니다");
									findFriTfId.clear();
								} // else 값 입력o
							} // else 값 입력x
						}
					});

					findFriBtnFindId.setOnAction(eFindId -> {

						if (findFriTfId.getText().trim().equals("")) {
							findFriLblState.setText("검색 할 아이디를 입력하세요");
						} else if (!(findFriTfId.getText().contains("@") && findFriTfId.getText().contains("."))) {
							findFriLblState.setText("Use Email Address\n이메일 형식이 아닙니다");
						} else {

							User userFriend = userDAO.selectFindFriend(findFriTfId.getText().trim());
							if (userFriend != null) {
								if (userFriend.getImagePath() == null || userFriend.getImagePath().equals("")) {
									try {
										FileInputStream fileInputStream = new FileInputStream(
										"D:\\JAVA_PROJECT\\Project_SmartPhone\\src\\icons\\27. 친구\\친구 00-1.png");
										findFriImgVFri.setImage(new Image(fileInputStream));
									} catch (Exception e) {
										callAlert("이미지 경로 오류 : 이미지 경로가 올바르지 않습니다");
									}
								} else if (userFriend.getImagePath() != null && !userFriend.getImagePath().equals("")) {
									try {
										FileInputStream fileInputStream = new FileInputStream(
												userFriend.getImagePath());
										findFriImgVFri.setImage(new Image(fileInputStream));
									} catch (Exception e) {
										callAlert("이미지 경로 오류 : 이미지 경로가 올바르지 않습니다");
									}
								}
								
								Friends userFriendCheck = friendsDAO.searchAddAlready(user.getId(), userFriend.getId());
								
								if (userFriendCheck != null)
									findFriLblState.setText("이미 친구인 회원입니다\n다른 친구를 찾아주세요");
								else {
									findFriLblState.setText("찾으시는 친구가 맞으면 추가를 눌러주세요");

									findFriBtnAddFri.setOnAction(eAddFriend -> {
										Friends friends = new Friends(user.getId(), userFriend.getId());
										int flag = friendsDAO.insertFriends(friends);
										if (flag == 1) {
											talkFindSubStage.close();
											//callAlert("친구 추가 성공 : DB에 친구를 추가 하였습니다");
											setUpFriendList();
											talkSubStage.close();
											talkFindSubStage.close();
										} else
											callAlert("친구 추가 실패 : DB에 친구를 추가하지 못 하였습니다");
									});
								}
							} else {
								findFriLblState.setText("찾으시는 회원이 없습니다");
								findFriTfId.clear();
							} // else 값 입력o
						} // else 값 입력x

					});
				} catch (Exception e) {
					callAlert("화면 전환 오류 : PROFILE EDIT 화면 전환에 문제가 있습니다. 검토바람");
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			callAlert("화면 전환 오류 : PROFILE EDIT 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}

	}

	// 04. 리스트뷰에 친구 띄우기 // 06. 친구목록을 누르면 친구리스트를 갱신
	private void setUpFriendList() {
		ObservableList<User> obFriList = FXCollections.observableArrayList();
		for (User friend : friendsDAO.setUpFriendsList(user.getId())) {
			obFriList.add(new User(friend.getId(), friend.getNicName(), friend.getImagePath(), friend.getLogOn()));
		}
		chLFriListVFriend.setItems(obFriList);
		chLFriListVFriend.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
			@Override
			public ListCell<User> call(ListView<User> param) {
				return new ListCell<User>() {
					@Override
					protected void updateItem(User user, boolean empty) {
						super.updateItem(user, empty);

						if (user == null || empty) {
							setText(null);
							setGraphic(null);
						} else {

							HBox cellRoot = new HBox(10);
							cellRoot.setAlignment(Pos.CENTER_LEFT);
							cellRoot.setPadding(new Insets(5));

							FileInputStream fileInputStream;
							try {
								if (user.getImagePath() == null || user.getImagePath().equals("")) {
									fileInputStream = new FileInputStream(
									"D:\\JAVA_PROJECT\\Project_SmartPhone\\src\\icons\\27. 친구\\친구 00-1.png");
									ImageView imgProfilePic = new ImageView(new Image(fileInputStream));
									imgProfilePic.setFitHeight(50);
									imgProfilePic.setFitWidth(50);

									cellRoot.getChildren().add(imgProfilePic);

									cellRoot.getChildren().add(new Separator(Orientation.VERTICAL));

									VBox vBox = new VBox(5);
									vBox.setAlignment(Pos.CENTER_LEFT);
									vBox.setPadding(new Insets(5));

									vBox.getChildren().addAll(new Label("On/Off : " + user.getLogOn()),
											new Label("NicName : " + user.getNicName()));

									cellRoot.getChildren().add(vBox);

									setGraphic(cellRoot);
								} else if (user.getImagePath() != null && !user.getImagePath().equals("")) {
									fileInputStream = new FileInputStream(user.getImagePath());
									ImageView imgProfilePic = new ImageView(new Image(fileInputStream));
									imgProfilePic.setFitHeight(50);
									imgProfilePic.setFitWidth(50);

									cellRoot.getChildren().add(imgProfilePic);

									cellRoot.getChildren().add(new Separator(Orientation.VERTICAL));

									VBox vBox = new VBox(5);
									vBox.setAlignment(Pos.CENTER_LEFT);
									vBox.setPadding(new Insets(5));

									vBox.getChildren().addAll(new Label("On/Off : " + user.getLogOn()),
											new Label("NicName : " + user.getNicName()));

									cellRoot.getChildren().add(vBox);

									setGraphic(cellRoot);
								}
							} catch (FileNotFoundException e) {
								callAlert("이미지 경로 오류 : 이미지 경로가 올바르지 않습니다");
								e.printStackTrace();
							}
						}
					}
				};
			}
		});
	}

	// 05. 리스트뷰에서 더블클릭하면 친구의 프로필이 뜸
	private void handleClickedFriList() {
		friend = chLFriListVFriend.getSelectionModel().getSelectedItem();
		try {
			Stage talkFriProfileStage = new Stage(StageStyle.UTILITY);
			this.talkFriProfileStage = talkFriProfileStage;
			FXMLLoader friProfileLoader = new FXMLLoader(getClass().getResource("../View/talk_friend_profile.fxml"));
			Parent talkFriProfileRoot = friProfileLoader.load();
			Scene talkFriProfileScene = new Scene(talkFriProfileRoot);
			talkFriProfileScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			talkFriProfileStage.setScene(talkFriProfileScene);
			talkFriProfileStage.setResizable(false);
			talkFriProfileStage.setTitle("Va Talk FRIEND PROFILE");
			talkFriProfileStage.show();

			ImageView fpImgVFriPic = (ImageView) talkFriProfileRoot.lookup("#fpImgVFriPic");
			TextField fpTfFriId = (TextField) talkFriProfileRoot.lookup("#fpTfFriId");
			TextField fpTfFriPhNo = (TextField) talkFriProfileRoot.lookup("#fpTfFriPhNo");
			TextField fpTfFriNic = (TextField) talkFriProfileRoot.lookup("#fpTfFriNic");
			Label fpLblState = (Label) talkFriProfileRoot.lookup("#fpLblState");
			Button fpBtnChat = (Button) talkFriProfileRoot.lookup("#fpBtnChat");
			Button fpBtnExit = (Button) talkFriProfileRoot.lookup("#fpBtnExit");
			Label fpLblDeleteFri = (Label) talkFriProfileRoot.lookup("#fpLblDeleteFri");

			friend = userDAO.setUpUserInfo(friend.getId());
			fpTfFriId.setText(friend.getId());
			fpTfFriId.setDisable(true);
			fpTfFriPhNo.setText(friend.getPhoneNo());
			fpTfFriPhNo.setDisable(true);
			fpTfFriNic.setText(friend.getNicName());
			fpTfFriNic.setDisable(true);

			if (friend.getImagePath() == null || friend.getImagePath().equals("")) {
				//callAlert("이미지 없음 : 등록된 사진이 없습니다");
			} else if (friend.getImagePath() != null && !friend.getImagePath().equals("")) {
				try {
					FileInputStream fileInputStream = new FileInputStream(friend.getImagePath());
					fpImgVFriPic.setImage(new Image(fileInputStream));
				} catch (Exception e) {
					callAlert("이미지 경로 오류 : 이미지 경로가 올바르지 않습니다");
				}
			}

			fpBtnChat.setOnAction(eGo2Chattingroom -> {
				try {
					Stage talkChattingStage = new Stage(StageStyle.UTILITY);
					this.talkChattingRoomStage = talkChattingStage;
					FXMLLoader ChattingLoader = new FXMLLoader(
							getClass().getResource("../View/talk_chatting_room.fxml"));
					Parent talkChattingRoot = ChattingLoader.load();
					TalkPrivChattingRoomController talkMainController = ChattingLoader.getController();
					talkMainController.setTalkChattingRoomStage(talkChattingStage);
					talkMainController.setFriend(friend);
					talkMainController.setFriendImagePath(friend.getImagePath());
					talkMainController.setFriendNicName(friend.getNicName());
					Scene talkChattingScene = new Scene(talkChattingRoot);
					talkChattingScene.getStylesheets()
							.add(getClass().getResource("../css/talk_chatting.css").toString());
					talkChattingStage.setScene(talkChattingScene);
					talkChattingStage.setResizable(false);
					talkChattingStage.setTitle("Va Talk CHATTING ROOM");
					talkFriProfileStage.close();
					talkChattingStage.show();
				} catch (Exception e) {
					e.printStackTrace();
				}

				Room room1 = roomDAO.searchRoomAlready(user.getId(), friend.getId());
				Room room2 = roomDAO.searchRoomAlready(friend.getId(), user.getId());
				if (room1 == null && room2 == null) {
					int makePrivRoom = roomDAO.makePrivRoom(user.getId(), friend.getId());
					if (makePrivRoom == 1) {
						//callAlert("방 만들기 성공 : DB에 채팅방이 업데이트 되었습니다.");
						talkFriProfileStage.close();
					} else
						callAlert("방 만들기 실패 : DB에 채팅방을 업데이트 하지 못 했습니다.");
				} else if (room1 != null && !room1.getRoomName().contains(",")) {
					int makePrivRoom = roomDAO.makePrivRoom(user.getId(), friend.getId());
					if (makePrivRoom == 1) {
						//callAlert("방 만들기 성공 : DB에 채팅방이 업데이트 되었습니다.");
						talkFriProfileStage.close();
					} else
						callAlert("방 만들기 실패 : DB에 채팅방을 업데이트 하지 못 했습니다.");
				} else if (room2 != null && !room2.getRoomName().contains(",")) {
					int makePrivRoom = roomDAO.makePrivRoom(user.getId(), friend.getId());
					if (makePrivRoom == 1) {
						//callAlert("방 만들기 성공 : DB에 채팅방이 업데이트 되었습니다.");
						talkFriProfileStage.close();
					} else
						callAlert("방 만들기 실패 : DB에 채팅방을 업데이트 하지 못 했습니다.");
				} else {
					callAlert("방 만들기 실패 : 이미 개설 된 방입니다");
				}
			});

			fpBtnExit.setOnAction(eBack2Profile -> talkFriProfileStage.close());

			talkFriProfileStage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent eClose) -> {
				if (eClose.getCode() == KeyCode.ESCAPE)
					talkFriProfileStage.hide();
			});

			fpLblDeleteFri.setOnMouseClicked(eDeleteUser -> {
				if (eDeleteUser.getClickCount() == 2) {
					fpLblState.setText("정말 친구를 삭제 하시겠습니까?\n친구삭제를 한번 더 누르면 삭제됩니다");
					fpLblDeleteFri.setOnMouseClicked(eDelete -> {
						friendsDAO.deleteFriend(user.getId(), friend.getId());
						setUpFriendList();
						talkFriProfileStage.close();
					});
				}
			});
		} catch (Exception e) {
			callAlert("화면 전환 오류 : FRIEND PROFILE 화면 전환에 문제가 있습니다. 검토바람");
			e.printStackTrace();
		}
	}

	// 07. 채팅목록을 누르면 채팅 리스트를 팝업으로 띄움
	private void setUpChatList() {
		try {
			Stage talkSubChatListStage = new Stage(StageStyle.UTILITY);
			this.talkSubChatListStage = talkSubChatListStage;
			FXMLLoader subChatListLoader = new FXMLLoader(getClass().getResource("../View/talk_chat_list.fxml"));
			Parent talkSubChatListRoot = subChatListLoader.load();
			Scene talkSubChatListScene = new Scene(talkSubChatListRoot);
			talkSubChatListScene.getStylesheets().add(getClass().getResource("../css/talk_login.css").toString());
			talkSubChatListStage.setScene(talkSubChatListScene);
			talkSubChatListStage.setResizable(false);
			talkSubChatListStage.setTitle("Va Talk CHATLIST");
			talkSubChatListStage.show();
			ListView<String> chLFriListVChatter = (ListView) talkSubChatListRoot.lookup("#chLFriListVChatter");
			Button chLFriBtn2FriList = (Button) talkSubChatListRoot.lookup("#chLFriBtn2FriList");

			chLFriBtn2FriList.setOnAction(eClose -> talkSubChatListStage.close());

			ObservableList<String> obChatRoomList = FXCollections.observableArrayList();
			for (String room : roomDAO.setUpChatRoomList(user.getId())) {
				obChatRoomList.add(room);
			}

			chLFriListVChatter.setItems(obChatRoomList);
			chLFriListVChatter.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
				@Override
				public ListCell<String> call(ListView<String> param) {
					return new ListCell<String>() {
						@Override
						protected void updateItem(String room, boolean empty) {
							super.updateItem(room, empty);

							if (room == null || empty) {
								setText(null);
								setGraphic(null);
							} else {
								HBox cellRoot = new HBox(10);
								cellRoot.setAlignment(Pos.CENTER_LEFT);
								cellRoot.setPadding(new Insets(5));
								FileInputStream fileInputStream;
								try {
									fileInputStream = new FileInputStream(
									"D:\\JAVA_PROJECT\\Project_SmartPhone\\src\\icons\\13. message, 문자 모아보기,chatting\\message 03.png");
									ImageView imgProfilePic = new ImageView(new Image(fileInputStream));
									imgProfilePic.setFitHeight(50);
									imgProfilePic.setFitWidth(50);
									cellRoot.getChildren().add(imgProfilePic);
									cellRoot.getChildren().add(new Separator(Orientation.VERTICAL));
									VBox vBox = new VBox(5);
									vBox.setAlignment(Pos.CENTER_LEFT);
									vBox.setPadding(new Insets(5));
									String category = null;

									if (room.contains(","))
										category = "Private Room";
									else
										category = "Join Room";
									vBox.getChildren().addAll(new Label(category), new Label(room));

									cellRoot.getChildren().add(vBox);
									setGraphic(cellRoot);
								} catch (FileNotFoundException e) {
									callAlert("이미지 경로 오류 : 이미지 경로가 올바르지 않습니다");
									e.printStackTrace();
								}
							}
						}
					};
				}
			});
			
			chLFriListVChatter.setOnMouseClicked(eChatting -> {

				String roomName = chLFriListVChatter.getSelectionModel().getSelectedItem();

				if (roomName.contains(",")) {
					Friends friends = roomDAO.findPrivRoomJoinner(roomName);
					if (friends.getUserID().equals(user.getId())) {
						friendFromList = userDAO.setUpUserInfo(friends.getCounterID());
					} else {
						friendFromList = userDAO.setUpUserInfo(friends.getUserID());
					}

					try {
						Stage talkChattingStage = new Stage(StageStyle.UTILITY);
						this.talkChattingRoomStage = talkChattingStage;
						FXMLLoader ChattingLoader = new FXMLLoader(
								getClass().getResource("../View/talk_chatting_room.fxml"));
						Parent talkChattingRoot = ChattingLoader.load();
						TalkPrivChattingRoomController talkMainController = ChattingLoader.getController();
						talkMainController.talkChattingRoomStage = talkChattingStage;
						talkMainController.setFriend(friendFromList);
						talkMainController.setFriendImagePath(friendFromList.getImagePath());
						talkMainController.setFriendNicName(friendFromList.getNicName());
						Scene talkChattingScene = new Scene(talkChattingRoot);
						talkChattingScene.getStylesheets()
								.add(getClass().getResource("../css/talk_chatting.css").toString());
						talkChattingStage.setScene(talkChattingScene);
						talkChattingStage.setResizable(false);
						talkChattingStage.setTitle("Va Talk CHATTING ROOM");
						talkSubChatListStage.close();
						talkChattingStage.show();

					} catch (Exception e) {
						e.printStackTrace();
					}

				} else if (!roomName.contains(",")) {

					try {
						Stage talkChattingStage = new Stage(StageStyle.UTILITY);
						this.talkChattingRoomStage = talkChattingStage;
						FXMLLoader ChattingLoader = new FXMLLoader(
								getClass().getResource("../View/talk_chatting_room2.fxml"));
						Parent talkChattingRoot = ChattingLoader.load();
						TalkGroupChattingRoomController talkMainController = ChattingLoader.getController();
						talkMainController.talkChattingRoomStage = talkChattingStage;
						talkMainController.setRoomName(roomName);
						Scene talkChattingScene = new Scene(talkChattingRoot);
						talkChattingScene.getStylesheets()
								.add(getClass().getResource("../css/talk_chatting.css").toString());
						talkChattingStage.setScene(talkChattingScene);
						talkChattingStage.setResizable(false);
						talkChattingStage.setTitle("Va Talk CHATTING ROOM");
						talkSubChatListStage.close();
						talkChattingStage.show();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 08. 리스트뷰에서 다중선택 후 방만들기 버튼을 누르면 단체방이 만들어짐
	private void handleMakeJoinRoom() {

		/************ 사용유저가 모두 같은 경우 중복으로 방이 만들어지는 경우 체크하는 로직 추가할 것 *************/

		joinner.addAll(chLFriListVFriend.getSelectionModel().getSelectedItems());
		String groupRoomName = null;
		for (User joinner : joinner) {
			groupRoomName += joinner.getId() + " ";
		}
		groupRoomName = groupRoomName.substring(4) + user.getId();
		for (User joinner : joinner) {
			roomDAO.makeJoinRoom(user.getId(), groupRoomName, joinner.getId());
		}
	}

	// 09. 로그아웃
	private void updateLogOutState() {

		handleSetLogOffAction();
		handleSetIpPortOffAction();

		talkProFriStage.close();
		if (this.talkFriProfileStage != null)
			talkFriProfileStage.close();
		if (this.talkChattingRoomStage != null)
			talkChattingRoomStage.close();
		if (this.talkSubEditUserStage != null)
			talkSubEditUserStage.close();
		if (this.talkSubStageFindID != null)
			talkSubStageFindID.close();
		if (this.talkFindSubStage != null)
			talkFindSubStage.close();
		if (this.talkSubChatListStage != null)
			talkSubChatListStage.close();
		chatter.stopClient();

	}

	private void handleSetLogOffAction() {
		int logOn = userDAO.updateUserLogOn(user, "off");
//		if (logOn == 1)
//			callAlert("로그오프 성공 : DB에 로그오프 상태로 업데이트 되었습니다.");
//		else
//			callAlert("로그오프 실패 : DB의 로그인 값이 변경되지 않았습니다.");
		if (logOn != 1)
			callAlert("로그오프 실패 : DB의 로그인 값이 변경되지 않았습니다.");
	}

	private void handleSetIpPortOffAction() {
		int updateIP = userDAO.updateIpPort(user.getId(), null, null);
//		if (updateIP == 1)
//			callAlert("INSERT 성공 : DB에 IP와 PORT가 업데이트 되었습니다.");
//		else
//			callAlert("INSERT 실패 : DB에 IP와 PORT 업데이트에 실패 하였습니다.");
		if (updateIP != 1)
			callAlert("INSERT 실패 : DB에 IP와 PORT 업데이트에 실패 하였습니다.");
	}

	public static void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("알림창");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":") + 1));
		alert.showAndWait();
	}

}
