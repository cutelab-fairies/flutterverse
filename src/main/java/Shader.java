import org.lwjgl.system.Struct;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

public class Shader {
    public int createShader(String location, int type) throws IOException {
        String content = new String(Flutterverse.class.getResourceAsStream(location).readAllBytes());

        int shader = glCreateShader(type);
        glShaderSource(shader, content);
        glCompileShader(shader);

        int result = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (result == 0) {
            String shaderLog = glGetShaderInfoLog(shader);
            if (shaderLog.trim().length() > 0) System.err.println(shaderLog);
            throw new AssertionError("Could not compile shader! "+location);
        }

        return shader;
    }
    public int createProgram(int[] shaders) {
        int program = glCreateProgram();
        for (int shader: shaders) {
            //System.out.println(shader);
            glAttachShader(program, shader);
        }
        glLinkProgram(program);

        int result = glGetProgrami(program, GL_LINK_STATUS);
        if (result == 0) {
            String shaderLog = glGetProgramInfoLog(program);
            if (shaderLog.trim().length() > 0) System.err.println(shaderLog);
            throw new AssertionError("Could not link program!");
        }

        return program;
    }

    public int program;
    private List<Integer> shaders = new ArrayList<>();

    public void addShader(String location, int type) throws IOException, URISyntaxException {
        int shader = createShader(location, type);
        shaders.add(shader);
    }

    private void cleanupShaders() {
        for (int shader: shaders) {
            glDeleteShader(shader);
        }
        shaders.clear();
    }

    public void createProgram() {
        // converting ArrayList to Array
        int[] shaders = new int[this.shaders.size()];
        for (int i=0; i<this.shaders.size(); i++) {
            shaders[i] = this.shaders.get(i);
        }

        program = createProgram(shaders);
        cleanupShaders();
    }

    public void use() {
        glUseProgram(program);
    }

    public void cleanup() {
        cleanupShaders();
        if (program!=0) glDeleteProgram(program);
    }

    public Shader() { }
}
