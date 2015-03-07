import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServer {

	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		Socket socket = null;
		DataInputStream dIn = null;
		DataOutputStream dOut = null;
		

		try {
			serverSocket = new ServerSocket(8888);
			System.out.println("listening : 8888");

		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				socket = serverSocket.accept();
				dIn = new DataInputStream(socket.getInputStream());
				dOut = new DataOutputStream(socket.getOutputStream());
				System.out.println("ip: " + socket.getInetAddress());
				System.out.println("State: " + dIn.readUTF());
				dOut.writeUTF("Hello!");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				// close socket,socket server, input and output streams.
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (dIn != null) {
					try {
						dIn.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				if (dOut != null) {
					try {
						dOut.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}

		}

	}
}
