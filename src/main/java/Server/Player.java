package Server;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Player extends Thread {
    public String username = "Fairy";
    public Vector3f position = new Vector3f();
    public float rotation = 0f;

    public Socket socket;
    public List<Player> players;

    public Player(Socket socket, List<Player> players){
        this.socket = socket;
        this.players = players;
    }

    public String getInfo() {
        return username+","+
            position.x+","+position.y+","+position.z+","+rotation;
    }

    public void log(String out) {
        String address = socket.getInetAddress().getHostAddress();
        System.out.println(username+" ("+address+"): "+out);
    }

    public void run() {
        log("Connected!");

        BufferedReader in;
        PrintWriter out;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            return;
        }

        while (true) {
            String line;
            try {
                line = in.readLine();
            } catch (IOException e) {
               return;
            }

            if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
                players.remove(this);
                log("Left!");
                return;
            }

            String[] input = line.split(" ");
            switch(input[0]) {
                case "username":
                    String newUsername = input[1];
                    log("Changed username: "+newUsername);
                    username = newUsername;
                    break;

                case "pos":
                    String[] position = input[1].split(",");
                    this.position.set(
                        Float.valueOf(position[0]),
                        Float.valueOf(position[1]),
                        Float.valueOf(position[2])
                    );
                    this.rotation = Float.valueOf(position[3]);
                    break;

                case "get":
                    StringBuilder output = new StringBuilder("get ");

                    for (Player p: players) {
                        output.append(p.getInfo()+" ");
                    }
                    output.deleteCharAt(output.length()-1);

                    out.println(output);
                    out.flush();
                    break;
            }
        }
    }
}
