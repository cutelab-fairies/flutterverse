import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL33.*;

public class Shader {
    public int createShader(String location, int type) throws IOException {
        String content = new String(Flutterverse.class.getResourceAsStream(location).readAllBytes());

        // fill in references
        Pattern referencePattern = Pattern.compile("// <reference path=\"(.*?)\"/>");
        content = referencePattern.matcher(content).replaceAll((match) -> {
            try {
                return new String(Flutterverse.class.getResourceAsStream(match.group(1)).readAllBytes());
            } catch (IOException e) {
                return "";
            }
        });

        // glsl things
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

    public void addShader(String location, int type) throws IOException {
        int shader = createShader(location, type);
        shaders.add(shader);
    }

    private Map<String, Integer> uniformLocations = new HashMap<>();
    private int getUniformLocation(String name) {

        if (uniformLocations.containsKey(name)) {
            return uniformLocations.get(name);

        } else {
            int loc = glGetUniformLocation(program, name);
            uniformLocations.put(name, loc);
            return loc;
        }
    }

    public void setUniform(String name, float v)    { glUniform1f(getUniformLocation(name), v); }
    public void setUniform(String name, Vector2f v) { glUniform2f(getUniformLocation(name), v.x, v.y); }
    public void setUniform(String name, Vector3f v) { glUniform3f(getUniformLocation(name), v.x, v.y, v.z); }

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
