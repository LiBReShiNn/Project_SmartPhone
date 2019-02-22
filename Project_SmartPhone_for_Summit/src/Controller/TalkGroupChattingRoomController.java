package Controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Model.User;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class TalkGroupChattingRoomController implements Initializable{

	@FXML private Label fssLblTime;	
	static final DateFormat CLOCK_FORMAT = new SimpleDateFormat("hh:mm:ss a");
	int count=0;

	public Stage talkChattingRoomStage;
	public void setTalkChattingRoomStage(Stage talkChattingRoomStage) {
		this.talkChattingRoomStage = talkChattingRoomStage;
	}
	Socket socket = TalkProFriRootController.chatter.getSocket();
	@FXML private Button chRmBtn2Back;
	@FXML private Button chRmBtnDeleteGrpRm;
	@FXML private ImageView chRmImgVCounterPic;
	@FXML private Label chRmLblCounterNic;
	@FXML private TextArea chRmTa;
	@FXML private Button chRmBtnGame;
	@FXML private Button chRmBtnSltPic;
	@FXML private Button chRmBtnSltFile;
	@FXML private Button chRmBtnSltAddr;
	@FXML private TextField chRmTf;
	@FXML private Button chRmBtnSend;
	private ExecutorService executorService;
	UserDAO userDAO = new UserDAO();
	FriendsDAO friendsDAO = new FriendsDAO();
	RoomDAO roomDAO = new RoomDAO();
	String roomName;
	User user = TalkProFriRootController.user;
	ArrayList<User> groupChatJoinner = new ArrayList<User>();

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		setUpLocalTimeFSS();
		
		chRmLblCounterNic.setOnMouseClicked(eSetFriend->setUpFriendPicNic());	
		
		chRmTf.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				send("GROUPCHAT$"+roomName+"#"+chRmTf.getText()+"&"+user.getNicName());
				chRmTf.clear();
			}
		});
		chRmBtnSend.setOnAction(event -> {
			send("GROUPCHAT$"+roomName+"#"+chRmTf.getText()+"&"+user.getNicName());
			chRmTf.clear();
		});
		
		chRmBtn2Back.setOnAction(eClose->talkChattingRoomStage.close());
		
		chRmBtnDeleteGrpRm.setOnAction(eDeleteGroupRoom->DeleteGroupRoomAction());
		
		receive();
		
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
	
	private void setUpFriendPicNic() {
		
		String[] joinnerID = roomName.split(" ");
		for (int i = 0; i < joinnerID.length; i++) {
			groupChatJoinner.add(userDAO.setUpUserInfo(joinnerID[i]));
		}
		
		String groupRoomName = null;
		for(User joinner : groupChatJoinner) {
			groupRoomName+=joinner.getNicName()+" ";	
		}
		groupRoomName = groupRoomName.substring(4);
		chRmLblCounterNic.setText(groupRoomName);
		
		try {
			FileInputStream fileInputStream = new FileInputStream(
			"D:\\JAVA_PROJECT\\Project_SmartPhone\\src\\icons\\13. message, 문자 모아보기,chatting\\group_chat_maker02.png");
			chRmImgVCounterPic.setImage(new Image(fileInputStream));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void DeleteGroupRoomAction() {

		String[] joinnerID = roomName.split(" ");
		for (int i = 0; i < joinnerID.length; i++) {
			groupChatJoinner.add(userDAO.setUpUserInfo(joinnerID[i]));
		}
		
		for (int i = 0; i < groupChatJoinner.size(); i++) {
			if(groupChatJoinner.get(i).getId().equals(user.getId())) {
				groupChatJoinner.remove(i);
			};
		}
		
		roomDAO.deleteUserFromRoom(roomName);
		
		String roomNameRe = null;
		for(User joinner : groupChatJoinner) {
			roomNameRe+=joinner.getId()+" ";
		}
		roomNameRe = roomNameRe.substring(4);
		for (int i = 1; i < groupChatJoinner.size(); i++) {
			roomDAO.makeJoinRoom(groupChatJoinner.get(0).getId(), roomNameRe, groupChatJoinner.get(i).getId());
		}
		
		talkChattingRoomStage.close();
	}
		
	private void stopClient() {
		if (!socket.isClosed() && socket != null) {
			try {
				socket.close();
				Platform.runLater(() -> callAlert("채팅서버 닫기 성공 : VaTalk Server를 닫았습니다."));
			} catch (IOException e) {
				Platform.runLater(() -> callAlert("채팅서버 닫기 실패 : 프로그램을 종료합니다."));
				return;
			}
		}
	}

	private void send(String sendMessage) {
		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			pw.println(sendMessage);
			pw.flush();
		} catch (IOException e) {
			Platform.runLater(() -> chRmTa
					.appendText("\n전송실패 : " + sendMessage.substring(sendMessage.lastIndexOf(":") + 2) + "\n"));
			stopClient();
		}
	}
	
	private void receive() {
		
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());		
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
						String receiveMessege = br.readLine();
						
						if (receiveMessege.startsWith("GROUPCHAT$")) {
						
							String senderNic = receiveMessege.substring(receiveMessege.indexOf("&")+1);
							String msg = receiveMessege.substring(receiveMessege.indexOf("#")+1, receiveMessege.indexOf("&"));
							Platform.runLater(() -> chRmTa.appendText(senderNic + " : " +msg + "\n"));
							
						}else if (receiveMessege.startsWith("DISCONNECTED$"))
							throw new IOException();
						
					} catch (IOException e) {
						Platform.runLater(() -> chRmTa.appendText("서버와 통신이 불가능합니다"));
						stopClient();
						break;
					} // try-catch
				} // while
			}
		};
		executorService.submit(runnable);
	}
	
	public static void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("알림창");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":") + 1));
		alert.showAndWait();
	}
}
