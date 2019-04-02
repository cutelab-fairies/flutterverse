package Engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    public float speed = 4f;
    public float friction = 0.95f;
    public float sensetivity = 12f;

    public Vector2f rotation = new Vector2f(0,0); // yaw pitch
    public Vector3f position = new Vector3f(0,0f,4f);
    public Vector3f velocity = new Vector3f(0,0,0);

    public float cameraHeight = 1.8f; // as tall as minecraft

    public Matrix4f view = new Matrix4f();
    public Matrix4f projection = new Matrix4f();

    //private Map<Character, Boolean> keys = new HashMap<>();
    boolean[] keyDown = new boolean[GLFW_KEY_LAST+1];

    private int width;
    private int height;

    public Camera(int width, int height) {
        this.width = width;
        this.height = height;

        // projection
        float aspectRatio = (float)width/(float)height;
        projection.perspective((float)Math.toRadians(90),aspectRatio,0.01f,1000);
    }

    public void onKey(int key, int action) {
        if (action == GLFW_PRESS) {
            keyDown[key] = true;
        } else if (action == GLFW_RELEASE) {
            keyDown[key] = false;
        }

    }

    public void onFrameBufferSize(long window, int width, int height) {
        this.width = width;
        this.height = height;

        // projection
        float aspectRatio = (float)width/(float)height;
        projection = new Matrix4f().perspective((float)Math.toRadians(90),aspectRatio,0.1f,100);
    }

    private Vector2f lastCursor = new Vector2f(0);
    public void onCursorPosition(double x, double y) {
        x = x/width;
        y = y/height;

        //System.out.println(x+);

        float deltaX = (float)(lastCursor.x-x)*sensetivity*0.1f;
        float deltaY = (float)(lastCursor.y-y)*sensetivity*0.1f;

        rotation.x -= deltaX;
        rotation.y -= deltaY;

        lastCursor.set((float)x,(float)y);
    }

    public void update(float dt) {
        float moveSpeed = speed*dt;

        velocity.mul(friction);

        Vector3f dir = new Vector3f();
        if (keyDown[GLFW_KEY_W])     dir.add( 0, 0,-1);
        if (keyDown[GLFW_KEY_S])     dir.add( 0, 0, 1);
        if (keyDown[GLFW_KEY_A])     dir.add(-1, 0, 0);
        if (keyDown[GLFW_KEY_D])     dir.add( 1, 0, 0);
        //if (keyDown[GLFW_KEY_C])     dir.add( 0,-1, 0);
        //if (keyDown[GLFW_KEY_SPACE]) dir.add( 0, 1, 0);
        if (dir.length()>0) {
            dir.normalize(1);
            dir.rotateAxis(rotation.x, 0,-1,0);
            if (keyDown[GLFW_KEY_LEFT_SHIFT]) dir.mul(2f);

            dir.mul(moveSpeed);
            velocity.set(dir);
        }


        position.add(velocity);

        Matrix4f yaw = new Matrix4f().rotate(rotation.x,0,1,0);
        Matrix4f pitch = new Matrix4f().rotate(rotation.y,1,0,0);

        Matrix4f rotate = new Matrix4f().mul(pitch).mul(yaw);
        Matrix4f translate = new Matrix4f().translate(new Vector3f(position).add(0,cameraHeight,0).negate());

        view = rotate.mul(translate);
    }
}
