import org.lwjgl.BufferUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

    public int texture;
    private boolean generateMipMaps = true;

    public void loadTexture(String filename) throws IOException {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);

        // put image buffer into the memory, not heap
        byte[] imageBufferArray = Flutterverse.class.getResourceAsStream(filename).readAllBytes();
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(imageBufferArray.length);
        imageBuffer.put(imageBufferArray);
        imageBuffer.flip();

        if (!stbi_info_from_memory(imageBuffer, width, height, components))
            throw new IOException("Failed to read image information: "+stbi_failure_reason());

        ByteBuffer imageData = stbi_load_from_memory(imageBuffer, width, height, components, STBI_rgb_alpha);
        if (imageData == null) throw new AssertionError("Could not load image!");

        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGBA,
            width.get(), height.get(), 0, GL_RGBA,
            GL_UNSIGNED_BYTE, imageData
        );

        if (generateMipMaps) glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(imageData);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind() {
        //glActiveTexture(GL_TEXTURE0+texUnit);
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public Texture(String filename) throws IOException {
        loadTexture(filename);
    }
}
