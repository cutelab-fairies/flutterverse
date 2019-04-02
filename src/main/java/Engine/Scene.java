package Engine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL46.GL_VERTEX_SHADER;

public class Scene {
    private List<Shader> shaders = new ArrayList<>();
    private List<List<Entity>> entities = new ArrayList<List<Entity>>();
    //private Map test = new HashMap<Shader, Entity>();

    public void addEntity(Entity entity, int shaderIndex) {
        entities.get(shaderIndex).add(entity);
    }
    public void addEntity(Entity entity) {
        addEntity(entity, 0);
    }

    public Entity createEntity(int shaderIndex) {
        Entity entity = new Entity();
        addEntity(entity);
        return entity;
    }

    public Entity createEntity() {
        return createEntity(0);
    }

    public List<Entity> findEntities(String search) {
        List<Entity> foundEntities = new ArrayList<>();

        for (List<Entity> entitiesInShader: entities) {
            for (Entity entity: entitiesInShader) {
                if (entity.name.equalsIgnoreCase(search)) {
                    foundEntities.add(entity);
                }
            }
        }

        return foundEntities;
    }

    public List<Entity> findEntities(int shaderIndex) {
        return entities.get(shaderIndex);
    }

    public List<Shader> findShaders(String search) {
        List<Shader> foundShaders = new ArrayList<>();

        for (Shader shader: shaders) {
            if (shader.name.equalsIgnoreCase(search)) {
                foundShaders.add(shader);
            }
        }

        return foundShaders;
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
