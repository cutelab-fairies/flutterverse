import Engine.*;
import org.joml.Vector3f;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL46.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL46.GL_VERTEX_SHADER;

public class Flutterverse extends Engine {
    Scene scene;

    // main functions
    @Override
    public void onInit() throws IOException, URISyntaxException {
        scene = new Scene();

        // vulpie
        Mesh vulpieMesh = new Mesh(GL_STATIC_DRAW);
        vulpieMesh.loadObj("models/vulpie.obj");
        vulpieMesh.make();
        Texture2D vulpieTexture = new Texture2D("textures/vulpie.jpg");

        for (int i=0; i<256; i++) {
            Entity vulpie = scene.createEntity();
            vulpie.name = "Vulpie";
            vulpie.mesh = vulpieMesh;
            vulpie.texture = vulpieTexture;

            vulpie.rotation.y = (float)(Math.random()*Math.PI*2);
            vulpie.scale.set((float)(Math.random()*0.5+0.25));
        }

//        TimerTask task  = new TimerTask() {
//            public void run() {
//                for (Entity vulpie: scene.findEntities("Vulpie")) {
//                    //vulpie.rotation.y += (float)(Math.random());
//                }
//            }
//        };
//        Timer timer = new Timer("Timer");
//        timer.scheduleAtFixedRate(task, 0L, 2000L);

        // floor
        Shader cute = new Shader();
        cute.name = "Cute";
        cute.addShader("shaders/standard.vs", GL_VERTEX_SHADER);
        cute.addShader("shaders/cute.fs", GL_FRAGMENT_SHADER);
        cute.createProgram();

        Entity floor = new Entity();
        floor.name = "Floor";
        floor.loadObj("models/floor.obj");
        scene.addEntity(floor, scene.addShader(cute));
    }

    @Override
    public void onUpdate(float time, float dt) {
        for (Entity vulpie: scene.findEntities(0)) {
            float speed = (1-vulpie.scale.x)*8*dt;

            vulpie.rotation.y += (float)(Math.random()*0.2*speed);

            Vector3f dir = new Vector3f(0,0,1);
            dir.rotateAxis(vulpie.rotation.y,0,1,0);
            vulpie.position.add(dir.mul(speed));
        }

        scene.update(this.camera, time);
    }

    @Override
    public void onDeinit() {
        scene.cleanup();
    }

    // initializers
    public Flutterverse() {}
    public static void main(String[] args) throws IOException, URISyntaxException {
        new Flutterverse().run();
        System.exit(-1);
    }
}
