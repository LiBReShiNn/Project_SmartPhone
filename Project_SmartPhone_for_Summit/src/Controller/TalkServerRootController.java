package Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Model.User;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;

public class TalkServerRootController implements Initializable { 
	@FXML	private TextArea textArea;
	@FXML	private Button btnStartStop;
	private ServerSocket serverSocket;
	private List<ClientChatter> connectedClientList = new Vector<ClientChatter>();
	UserDAO userDAO = new UserDAO();
	ClientChatter client;
	private ExecutorService executorService;
	static int n=0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		btnStartStop.setOnAction(eOpenServer -> {
			if (btnStartStop.getText().equals("Start"))
				startServer();
			else
				stopServer();
		});
	} // initialize()

	private void startServer() {
		executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("192.168.0.209", 6114));
			textArea.appendText(new Date() + " : 서버가 시작되었습니다\n");
			btnStartStop.setText("Stop");
		} catch (IOException e) {
			if (!serverSocket.isClosed())
				stopServer();
			return;
		}

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Socket socket = serverSocket.accept();
						Platform.runLater(() -> {
							textArea.appendText("연결수락 : " + socket.getRemoteSocketAddress() + "\n");
						});

						client = new ClientChatter(socket);
						connectedClientList.add(client);
						client.start();
						Platform.runLater(() -> {
							textArea.appendText("연결개수 : " + connectedClientList.size() + "\n");
						});
					} catch (IOException e) {
						if (!serverSocket.isClosed())
							stopServer();
						break;
					} // accept-catch
				} // while
			}// run
		};
		executorService.submit(runnable);
	}// stratServer

	private void stopServer() {
		try {
			for (ClientChatter client : connectedClientList) {
				client.send("DISCONNECTED$");
				connectedClientList.remove(client);
				client.socket.close();
			}
			if (!serverSocket.isClosed() && serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e1) {
					Platform.runLater(() -> {
						textArea.appendText("서버소켓을 닫는 도중 오류발생\n");
					});
				} // close try-catch
			} // if
		} catch (IOException e) {
			Platform.runLater(() -> {
				textArea.appendText("클라이언트 삭제 도중 오류발생\n");
			});
		} // try-catch
		Platform.runLater(() -> {
			textArea.appendText("서버가 정지 되었습니다.\n");
			btnStartStop.setText("Start");
		});
	}

	public class ClientChatter extends Thread {

		private Socket socket;
		User user;
		User friend;
		UserDAO userDAO = new UserDAO();
		FriendsDAO friendsDAO = new FriendsDAO();
		RoomDAO roomDAO = new RoomDAO();

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}

		public User getFriend() {
			return friend;
		}

		public void setFriend(User friend) {
			this.friend = friend;
		}

		public ClientChatter(Socket socket) {
			this.socket = socket;
		}

		public void start() {
			receive();
		}

		protected void send(String recieveMessege) {
			PrintWriter pw;
			try {
				pw = new PrintWriter(socket.getOutputStream());
				pw.println(recieveMessege);
				pw.flush();
			} catch (IOException e) {
				Platform.runLater(() -> {
					textArea.appendText("OUTPUTSTREAM ERROR : " + socket.getRemoteSocketAddress() + "\n");
				});
				connectedClientList.remove(ClientChatter.this);
				try {
					socket.close();
				} catch (IOException e1) {
					Platform.runLater(() -> {
						textArea.appendText(
								"CLIENT'S SOCKET DIDN'T CLOSED : " + socket.getRemoteSocketAddress() + "\n");
					});
				} // close try-catch
			} // try-catch
		}// send

		private void receive() {
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						while (true) {
							BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							String recieveMessege = br.readLine();
							if (recieveMessege == null) {
								throw new IOException();
							} else if (recieveMessege.startsWith("LOGIN$")) {
								Platform.runLater(() -> {
									textArea.appendText(
											socket.getRemoteSocketAddress() + " : " + recieveMessege + "\n");
									String userID = recieveMessege.substring(recieveMessege.indexOf("$") + 1);
									User user = userDAO.setUpUserInfo(userID);
									setUser(user);
								});
							} else if (recieveMessege.startsWith("PRIVCHAT$")) {
								Platform.runLater(() -> {
									textArea.appendText(
											socket.getRemoteSocketAddress() + " : " + recieveMessege + "\n");
								});

								String friendID = recieveMessege.substring(recieveMessege.indexOf("$") + 1,
										recieveMessege.indexOf("#"));

								send(recieveMessege);

								for (int i = 0; i < connectedClientList.size(); i++) {
									if (connectedClientList.get(i).user.getId().equals(friendID))
										connectedClientList.get(i).send(recieveMessege);
								}
							} else if (recieveMessege.startsWith("GROUPCHAT$")) {
								Platform.runLater(() -> {
									textArea.appendText(
											socket.getRemoteSocketAddress() + " : " + recieveMessege + "\n");
								});
								String groupJoinnerID = recieveMessege.substring(recieveMessege.indexOf("$") + 1,
										recieveMessege.indexOf("#"));
								String[] joinnerID = groupJoinnerID.split(" ");
								for (int i = 0; i < connectedClientList.size(); i++) {
									for (int j = 0; j < joinnerID.length; j++) {
										if (connectedClientList.get(i).user.getId().equals(joinnerID[j]))
											connectedClientList.get(i).send(recieveMessege);
									}
								}
							} else if (recieveMessege.startsWith("DISCONNECTED$")) {
								Platform.runLater(() -> {
									textArea.appendText(
											socket.getRemoteSocketAddress() + " : " + recieveMessege + "\n");
								});
								send(recieveMessege);
							}
						} // while
					} catch (IOException e) {
						try {
							connectedClientList.remove(ClientChatter.this);
							Platform.runLater(
									() -> textArea.appendText("클라이언트가 서버를 나갔습니다 : " + socket.getRemoteSocketAddress()
											+ "\n연결개수 : " + connectedClientList.size() + "\n"));
							socket.close();
						} catch (IOException e1) {
							Platform.runLater(() -> textArea
									.appendText("클라이언트 비정상 종료 : " + socket.getRemoteSocketAddress() + "\n"));
							e1.printStackTrace();
						}
					} // try-catch
				}// run
			};
			executorService.submit(runnable);
		}// receive
	}// Client
	
	public static void callAlert(String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("알림창");
		alert.setHeaderText(contentText.substring(0, contentText.lastIndexOf(":")));
		alert.setContentText(contentText.substring(contentText.lastIndexOf(":") + 2));
		alert.showAndWait();
	}
}
