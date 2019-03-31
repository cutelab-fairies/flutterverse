package Engine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL46.GL_VERTEX_SHADER;

public class Scene {
    private List<Shader> shaders = new ArrayList<Shader>();
    private List<List<Entity>> entities = new ArrayList<List<Entity>>();

    public void addEntity(Entity entity) {
        entities.get(0).add(entity);
    }

    public void addEntity(Entity entity, int shaderIndex) {
        entities.get(shaderIndex).add(entity);
    }

    public int addShader(Shader shader) {
        shaders.add(shader);
        entities.add(new ArrayList<>());
        return shaders.size()-1;
    }

    public void update(Camera camera, float time) {
        for (int shaderIndex=0; shaderIndex<entities.size(); shaderIndex++) {
            Shader shader = shaders.get(shaderIndex);
            //System.out.println("Using shader: "+shader.name);

            shader.use();
            shader.setUniform("iTime", time);
            shader.setUniform("view", camera.view);
            shader.setUniform("projection", camera.projection);

            // draw each entity with that shader index
            for (Entity entity: entities.get(shaderIndex)) {
                //System.out.println("\t- "+entity.name);
                entity.draw(shader);
            }
        }
    }

    public void cleanup() {
        for (int shaderIndex=0; shaderIndex<entities.size(); shaderIndex++) {
            // cleanup shader
            shaders.get(shaderIndex).cleanup();

            // cleanup all entities attached to shader
            for (Entity entity: entities.get(shaderIndex)) {
                entity.cleanup();
            }
        }

        shaders.clear();
        entities.clear();
    }

    public Scene() throws IOException, URISyntaxException {
        // initialize standard shader
        Shader standard = new Shader();
        standard.name = "Standard";
        standard.addShader("shaders/standard.vs", GL_VERTEX_SHADER);
        standard.addShader("shaders/standard.fs", GL_FRAGMENT_SHADER);
        standard.createProgram();
        addShader(standard);
    }
}
