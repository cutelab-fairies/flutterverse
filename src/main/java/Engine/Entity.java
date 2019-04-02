package Engine;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.opengl.GL46.GL_STATIC_DRAW;

public class Entity {

    public String name = "Entity";

    public Mesh mesh;
    //public Engine.Shader shader;
    public Texture2D texture;

    public Vector3f scale = new Vector3f(1,1,1);
    public Vector3f position = new Vector3f(0,0,0);
    public Vector3f rotation = new Vector3f(0,0,0);
    //public Quaternionf rotation = new Quaternionf(0,0,0,1);

    public void loadObjWithTexture(String objFilename, String texFilename) throws IOException, URISyntaxException {
        mesh = new Mesh(GL_STATIC_DRAW);
        mesh.loadObj(objFilename);
        mesh.make();

        texture = new Texture2D(texFilename);
    }

    public void loadObj(String objFilename) throws IOException, URISyntaxException {
        mesh = new Mesh(GL_STATIC_DRAW);
        mesh.loadObj(objFilename);
        mesh.make();
    }

    public void draw(Shader shader) {
        if (mesh == null) throw new AssertionError("Mesh not assigned to Entity");
        //if (shader == null) throw new AssertionError("Shader not assigned to Entity");
        //if (texture == null) throw new AssertionError("Texture2D not assigned to Entity");

        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.scale(scale);
        modelMatrix.translate(position);
        //modelMatrix.rotate(rotation);
        modelMatrix.rotateX(rotation.x);
        modelMatrix.rotateY(rotation.y);
        modelMatrix.rotateY(rotation.z);
        shader.setUniform("model", modelMatrix);

        if (texture!=null) texture.bind();
        mesh.draw();
    }

    public void cleanup() {
        mesh.cleanup();
    }
}
