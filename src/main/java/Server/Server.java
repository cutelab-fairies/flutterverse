package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private boolean running = true;
    private List<Player> players = new ArrayList<>();

    private ServerSocket serverSocket;

    public void start(int port) throws IOException {
        System.out.println("Server open on port: "+port);
        serverSocket = new ServerSocket(port);
        Socket socket;

        while (running) {
            socket = serverSocket.accept();
            Player player = new Player(socket, players);
            players.add(player);
            player.start();
        }
    }

    public void stop() throws IOException {
        serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start(25565);
        server.stop();
    }
}
