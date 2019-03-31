package Engine;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL46.*;

public class Shader {
    public String name = "Shader";

    public int createShader(String filename, int type) throws IOException, URISyntaxException {
        String content = Utils.loadFile(filename);

        // fill in references
        Pattern referencePattern = Pattern.compile("// <reference path=\"(.*?)\"/>");
        Matcher matcher = referencePattern.matcher(content);

        while (matcher.find()) {
            content = matcher.replaceFirst(Utils.loadFile(matcher.group(1)));
        }

        // glsl things
        int shader = glCreateShader(type);
        glShaderSource(shader, content);
        glCompileShader(shader);

        int result = glGetShaderi(shader, GL_COMPILE_STATUS);
        if (result == 0) {
            String shaderLog = glGetShaderInfoLog(shader);
            if (shaderLog.trim().length() > 0) System.err.println(shaderLog);
            throw new AssertionError("Could not compile shader! "+filename);
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

    public void addShader(String filename, int type) throws IOException, URISyntaxException {
        int shader = createShader(filename, type);
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
    public void setUniform(String name, Matrix4f m) {
        FloatBuffer fb = BufferUtils.createFloatBuffer(16);
        glUniformMatrix4fv(getUniformLocation(name), false, m.get(fb));
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
