import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static utils.ServerMessages.*;

public class Server {

    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8083);
        Server server = new Server(serverSocket);
        server.startServer();
    }

    public void startServer(){

        try{
            while(!serverSocket.isClosed()){

                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException ioe){

        }
    }

    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}