import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import static utils.ServerMessages.*;

public class Server {

    private int clientId;

    public static void main(String[] args) {
        int port = 8082;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        Server server = new Server();
        server.start(port);
    }

    public void start(int port) {
        try {
            System.out.printf(BINDING, port);
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.printf(SERVER_STARTED, serverSocket);

            BufferedReader inFromSocket;
            PrintWriter out;
            BufferedReader inFromConsole;

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.printf(CLIENT_ACCEPTED, ++clientId, clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);

                Thread thread = new Thread(clientHandler);
                thread.start();
                inFromSocket = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                inFromConsole = new BufferedReader(new InputStreamReader(System.in));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                while (!clientSocket.isClosed()) {
                    String line;
                    if ((line = readAndCheckConnection(inFromSocket, clientSocket)) == null) {
                        break;
                    }
                    System.out.printf(CLIENT_MESSAGE, clientId, line);
                    out.println(readAndCheckConnection(inFromConsole, clientSocket));

                }
            }

        } catch (IOException e) {
            System.out.printf(PORT_ERROR, port);
        }

    }

    private String readAndCheckConnection(BufferedReader in, Socket clientSocket) throws IOException {
        String line = null;
        try {
            line = in.readLine();
            if (line == null) {
                System.out.printf(CLOSED_CONNECTION, clientId);
                clientSocket.close();
            }
        } catch (SocketException e) {
            System.out.printf(CLOSED_CONNECTION, clientId);
            clientSocket.close();
        }

        return line;
    }


}