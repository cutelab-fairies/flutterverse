package Engine;

import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.GltfModels;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.GltfAssetReader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

import de.javagl.jgltf.*;

import javax.rmi.CORBA.Util;

public class Mesh {
    private int bufferUsage;

    // open gl stuff
    private int vao = glGenVertexArrays();

    private int posVbo = glGenBuffers();
    private int uvVbo = glGenBuffers();

    private FloatBuffer posVboBuffer;
    private FloatBuffer uvVboBuffer;

    // my model stuff
    private int vertexCount;
    List<Vector3f> pos = new ArrayList<>();
    List<Vector2f> uv = new ArrayList<>();

    public void addVertex(Vector3f pos, Vector2f uv) {
        this.pos.add(pos);
        this.uv.add(uv);
    }

    public void addTriangle(
        Vector3f v1, Vector2f uv1,
        Vector3f v2, Vector2f uv2,
        Vector3f v3, Vector2f uv3
    ) {
        addVertex(v1, uv1);
        addVertex(v2, uv2);
        addVertex(v3, uv3);
    }

    public void addQuad(
        Vector3f v1, Vector2f uv1,
        Vector3f v2, Vector2f uv2,
        Vector3f v3, Vector2f uv3,
        Vector3f v4, Vector2f uv4
    ) {
        addTriangle(
            v1, uv1,
            v2, uv2,
            v3, uv3
        );
        addTriangle(
            v1, uv1,
            v3, uv3,
            v4, uv4
        );
    }

    public void loadObj(String filename) throws IOException {
        // definitely not the best way lol, but assimp is a little tricky to use.
        // ill have to look into it another time once i have scenes.

        String obj = Utils.loadFile(filename);
        BufferedReader reader = new BufferedReader(new StringReader(obj));

        List<Vector3f> pos = new ArrayList<>();
        List<Vector2f> uv = new ArrayList<>();

        String line = null;
        while((line=reader.readLine()) != null) {
            String attr[] = line.split(" "); // attributes?
            switch (attr[0]) {
                case "v":
                    pos.add(new Vector3f(
                        Float.valueOf(attr[1]),
                        Float.valueOf(attr[2]),
                        Float.valueOf(attr[3])
                    ));
                    break;
                case "vt":
                    uv.add(new Vector2f(
                        Float.valueOf(attr[1]),
                        Float.valueOf(attr[2])
                    ));
                    break;
                case "f":
                    // there are 3 sets of 3 indicies for pos, uv and normal
                    // f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3

                    // each v array is: pos, uv, normal
                    String iv1[] = attr[1].split("/");
                    int v1[] = {
                        Integer.valueOf(iv1[0])-1,
                        Integer.valueOf(iv1[1])-1,
                        //Integer.valueOf(iv1[2])-1,
                    };

                    String iv2[] = attr[2].split("/");
                    int v2[] = {
                        Integer.valueOf(iv2[0])-1,
                        Integer.valueOf(iv2[1])-1,
                        //Integer.valueOf(iv2[2])-1,
                    };

                    String iv3[] = attr[3].split("/");
                    int v3[] = {
                        Integer.valueOf(iv3[0])-1,
                        Integer.valueOf(iv3[1])-1,
                        //Integer.valueOf(iv3[2])-1,
                    };

                    addTriangle(
                        pos.get(v1[0]), uv.get(v1[1]),
                        pos.get(v2[0]), uv.get(v2[1]),
                        pos.get(v3[0]), uv.get(v3[1])
                    );
                break;
            }
        }

        System.out.println("Loaded obj with "+pos.size()+" verticies, "+uv.size()+" uvs");
    }

    public void make() {
        if (pos.size() != uv.size()) return;

        // verticies to vbo buffer
        vertexCount = pos.size();

        posVboBuffer = BufferUtils.createFloatBuffer(pos.size()*3);
        uvVboBuffer = BufferUtils.createFloatBuffer(uv.size()*2);

        pos.forEach(pos -> posVboBuffer.put(pos.x).put(pos.y).put(pos.z));
        uv.forEach(uv -> uvVboBuffer.put(uv.x).put(uv.y));

        posVboBuffer.flip();
        uvVboBuffer.flip();

        // opengl
        glBindVertexArray(vao);

        // positions
        glBindBuffer(GL_ARRAY_BUFFER, posVbo);
        glBufferData(GL_ARRAY_BUFFER, posVboBuffer, bufferUsage);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0L); // position
        glEnableVertexAttribArray(0);

        // uv coordinates
        glBindBuffer(GL_ARRAY_BUFFER, uvVbo);
        glBufferData(GL_ARRAY_BUFFER, uvVboBuffer, bufferUsage);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0L); // position
        glEnableVertexAttribArray(1);

        // cleanup
        glBindVertexArray(0);
    }

    public void draw() {
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(posVbo);
        glDeleteBuffers(uvVbo);
    }

    public Mesh(int bufferUsage) {
        this.bufferUsage = bufferUsage;
    }
}
