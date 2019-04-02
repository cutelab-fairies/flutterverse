import Engine.Camera;
import Engine.Entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkClient extends Thread {
    private boolean active = true;

    private List<Entity> playerEntities;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Timer timer;

    public NetworkClient(Camera camera, List<Entity> playerEntities) throws IOException, URISyntaxException {
        this.playerEntities = playerEntities;

        // setup socket
        socket = new Socket("home.maki.cat", 25565); //8813
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // setup interval
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                out.println("pos "+
                    camera.position.x+","+camera.position.y+","+camera.position.z+","+camera.rotation.x
                );
                out.println("get");
            }
        };
        timer = new Timer("Timer");

        timer.scheduleAtFixedRate(repeatedTask, 0L, 1000L/60L);

        // setup while
        this.start();
    }

    public void cleanup() throws IOException {
        active = false;
        timer.cancel();
        socket.close();
    }

    public void run() {
        while (active) {
            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                return;
            }
            String[] input = line.split(" ");

            // add new player entity if not enough entities
//            if (input.length-1 > playerEntities.size()) {
//                int amountOfEntitiesToAdd = input.length-1-playerEntities.size();
//                for (int i=0; i<amountOfEntitiesToAdd; i++) {
//                    try {
//                        //addEntity();
//                    } catch (IOException | URISyntaxException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }

            for (int i=1; i<input.length; i++) {
                Entity entity = playerEntities.get(i);
                String[] info = input[i].split(",");

                entity.position.set(
                    Float.parseFloat(info[1]),
                    Float.parseFloat(info[2]),
                    Float.parseFloat(info[3])
                );

                //entity.rotation.setAngleAxis((float)Math.PI-Float.parseFloat(info[4]), 0,1,0);
            }
        }
    }
}
