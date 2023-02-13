import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = br.readLine();

            clientHandlers.add(this);
            sendToAll("SERVER: " + username + " has entered the room");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

public void sendToAll(String message){
    for (ClientHandler ch :
            clientHandlers) {
        try {
            if(!ch.username.equals(username)){
                ch.bw.write(message);
                ch.bw.newLine();
                ch.bw.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

    @Override
    public void run() {
        String clientMessage;

        while (socket.isConnected()) {

            try {
                clientMessage = br.readLine();
                sendToAll(clientMessage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

