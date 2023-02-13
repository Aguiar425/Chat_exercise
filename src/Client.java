import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import static utils.ClientMessages.*;

public class Client{

    private Socket socket;
    private BufferedReader br;
    private BufferedWriter bw;
    private String username;

    public Client(Socket socket, String username) {
        try{
            this.socket = socket;
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = input.nextLine();

        Socket socket = new Socket("localhost", 8083);
        Client client = new Client(socket, username);

        client.checkMessage();
        client.sendMessage();
    }

    public void sendMessage(){
        try{
            bw.write(username);
            bw.newLine();
            bw.flush();

            Scanner input = new Scanner(System.in);
            while (socket.isConnected()){
                String message = input.nextLine();
                bw.write(username + ": " + message);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkMessage(){
        new Thread((new Runnable() {
            @Override
            public void run() {
                String messageFromChat;

                while (socket.isConnected()){
                    try{
                        messageFromChat = br.readLine();
                        System.out.println(messageFromChat);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        })).start();
    }
}
