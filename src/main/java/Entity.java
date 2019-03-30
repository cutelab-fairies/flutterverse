import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class Entity {

    public Model model;
    //public Shader shader;
    public Texture texture;

    public Vector3f position = new Vector3f(0,0,0);
    public Vector3f rotation = new Vector3f(0,0,0);
    //public Quaternionf rotation = new Quaternionf(0,0,0,0);

    public void loadObjWithTexture(String objFilename, String texFilename) throws IOException {
        model = new Model(GL_STATIC_DRAW);
        model.loadObj(objFilename);
        model.make();

        texture = new Texture(texFilename);
    }

    public void loadObj(String objFilename) throws IOException {
        model = new Model(GL_STATIC_DRAW);
        model.loadObj(objFilename);
    }

    public void draw(Shader shader) {
        if (model == null) throw new AssertionError("Model not assigned to Entity");
        //if (shader == null) throw new AssertionError("Shader not assigned to Entity");
        //if (texture == null) throw new AssertionError("Texture not assigned to Entity");

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
