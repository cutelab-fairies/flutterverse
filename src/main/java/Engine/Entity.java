package Engine;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.opengl.GL46.GL_STATIC_DRAW;

public class Entity {

    public String name = "Entity";

    public Mesh model;
    //public Engine.Shader shader;
    public Texture2D texture;

    public Vector3f position = new Vector3f(0,0,0);
    public Vector3f rotation = new Vector3f(0,0,0);
    //public Quaternionf rotation = new Quaternionf(0,0,0,0);

    public void loadObjWithTexture(String objFilename, String texFilename) throws IOException, URISyntaxException {
        model = new Mesh(GL_STATIC_DRAW);
        model.loadObj(objFilename);
        model.make();

        texture = new Texture2D(texFilename);
    }

    public void loadObj(String objFilename) throws IOException, URISyntaxException {
        model = new Mesh(GL_STATIC_DRAW);
        model.loadObj(objFilename);
        model.make();
    }

    public void draw(Shader shader) {
        if (model == null) throw new AssertionError("Engine.Mesh not assigned to Engine.Entity");
        //if (shader == null) throw new AssertionError("Engine.Shader not assigned to Engine.Entity");
        //if (texture == null) throw new AssertionError("Engine.Texture2D not assigned to Engine.Entity");

        Matrix4f modelMatrix = new Matrix4f();
        modelMatrix.translate(position);
        modelMatrix.rotate(rotation.x,1,0,0);
        modelMatrix.rotate(rotation.y,0,1,0);
        modelMatrix.rotate(rotation.z,0,0,1);
        shader.setUniform("model", modelMatrix);

        if (texture!=null) texture.bind();
        model.draw();
    }

    public void cleanup() {
        model.cleanup();
    }
}
