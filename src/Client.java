import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import static utils.ClientMessages.*;

public class Client{

    private Socket socket;
    private String username;

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println(USAGE);
            System.exit(1);
        }

        try {
            System.out.println(CONNECTION_ATTEMPT);
            Client client = new Client(args[0], Integer.parseInt(args[1]));
            client.start();

        } catch (NumberFormatException ex) {
            System.out.printf(PORT_ERROR, args[1]);
            System.exit(1);
        }
    }

    public Client(String hostName, int serverPort) {

        try {
            socket = new Socket(hostName, serverPort);
            System.out.printf(CONNECTION_ESTABLISHED, socket);
        } catch (UnknownHostException ex) {
            System.out.printf(HOST_ERROR, ex.getMessage());
            System.exit(1);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
    }

    private void start() {

        try {

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            while (!socket.isClosed()) {

                String consoleMessage;
                try {
                    consoleMessage = bufferedReader.readLine();
                } catch (IOException ex) {
                    System.out.printf(CONSOLE_ERROR, ex.getMessage());
                    break;
                }

                if (consoleMessage == null || consoleMessage.equalsIgnoreCase(QUIT_MESSAGE)) {
                    socket.close();
                    break;
                }

                bufferedWriter.write(consoleMessage);
                bufferedWriter.newLine();
                bufferedWriter.flush();

                bufferedWriter.write(username +": " + consoleMessage);
                //System.out.printf(MESSAGE_SERVER_SENT, sockIn.readLine());

            }

            try {

                bufferedReader.close();
                bufferedWriter.close();
                socket.close();

            } catch (IOException ex) {
                System.out.printf(CONNECTION_CLOSING_ERROR, ex.getMessage());
            }

        } catch (IOException ex) {
            System.out.printf(MESSAGE_SENDIND_ERROR, ex.getMessage());
        }
    }
}
