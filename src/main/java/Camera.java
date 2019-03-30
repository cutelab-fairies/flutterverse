import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    public float speed = 4f;
    public float sensetivity = 12f;

    public Vector3f position = new Vector3f(0,1.8f,4f); // as tall as minecraft
    public Vector2f rotation = new Vector2f(0,0); // yaw pitch

    public Matrix4f view = new Matrix4f();
    public Matrix4f projection = new Matrix4f();

    private Map<Character, Boolean> keys = new HashMap<>();

    public Camera() {
        // keys
        keys.put('w', false);
        keys.put('a', false);
        keys.put('s', false);
        keys.put('d', false);

        // projection
        projection.perspective((float)Math.toRadians(45),1,0.1f,100);
    }

    public void onKey(int key, int action) {
        if (action == GLFW_PRESS) {
            if (key == GLFW_KEY_W) keys.put('w', true);
            if (key == GLFW_KEY_A) keys.put('a', true);
            if (key == GLFW_KEY_S) keys.put('s', true);
            if (key == GLFW_KEY_D) keys.put('d', true);
            if (key == GLFW_KEY_LEFT_SHIFT) speed = 8f;
        } else if (action == GLFW_RELEASE) {
            if (key == GLFW_KEY_W) keys.put('w', false);
            if (key == GLFW_KEY_A) keys.put('a', false);
            if (key == GLFW_KEY_S) keys.put('s', false);
            if (key == GLFW_KEY_D) keys.put('d', false);
            if (key == GLFW_KEY_LEFT_SHIFT) speed = 4f;
        }

    }

    private Vector2f lastCursor = new Vector2f(0);
    public void onCursorPosition(double x, double y) {
        float deltaX = (float)(lastCursor.x-x)*sensetivity*0.0001f;
        float deltaY = (float)(lastCursor.y-y)*sensetivity*0.0001f;

        rotation.x -= deltaX;
        rotation.y -= deltaY;

        lastCursor.set((float)x,(float)y);
    }

    public void update(float dt) {
        float moveSpeed = speed*dt;

        int forward = (keys.get('s')?1:0)-(keys.get('w')?1:0);
        int sideways = (keys.get('d')?1:0)-(keys.get('a')?1:0);

        float halfPI = (float)Math.PI/2f;
        if (forward!=0) {
            position.add(
                (float)Math.cos(rotation.x+halfPI)*moveSpeed*forward,
                0,
                (float)Math.sin(rotation.x+halfPI)*moveSpeed*forward
            );
        }
        if (sideways!=0) {
            position.add(
                (float)Math.cos(rotation.x)*moveSpeed*sideways,
                0,
                (float)Math.sin(rotation.x)*moveSpeed*sideways
            );
        }

        Matrix4f yaw = new Matrix4f().rotate(rotation.x,0,1,0);
        Matrix4f pitch = new Matrix4f().rotate(rotation.y,1,0,0);

        Matrix4f rotate = new Matrix4f().mul(pitch).mul(yaw);
        Matrix4f translate = new Matrix4f().translate(new Vector3f(position).negate());

        view = rotate.mul(translate);

//        position.add(
//            (keys.get('d')?move:0)-(keys.get('a')?move:0),
//            0,
//            (keys.get('s')?move:0)-(keys.get('w')?move:0)
//        );
//
//        Vector3f normalizedRotation = rotation;
//        Vector3f targetPos = new Vector3f(position).add(normalizedRotation);
//
//        System.out.println(normalizedRotation);
//
//        view = new Matrix4f().lookAt(position, targetPos, targetUp);
    }

//    public Matrix4f getViewMatrix() {
//        return new Matrix4f().lookAt(position, targetPos, up);
//    }
}
