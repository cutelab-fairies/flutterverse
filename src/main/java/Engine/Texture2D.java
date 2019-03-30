package Engine;

import java.io.*;
import java.net.URISyntaxException;

import static org.lwjgl.opengl.GL33.*;

public class Texture2D {

    public int texture;
    private boolean generateMipMaps = true;

    public void loadTexture(String filename) throws IOException, URISyntaxException {
        ImageData image = new ImageData(filename);

        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(
            GL_TEXTURE_2D, 0, GL_RGBA,
            image.width, image.height, 0, GL_RGBA,
            GL_UNSIGNED_BYTE, image.data
        );

        if (generateMipMaps) glGenerateMipmap(GL_TEXTURE_2D);

        image.free();
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void bind() {
        //glActiveTexture(GL_TEXTURE0+texUnit);
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public Texture2D(String filename) throws IOException, URISyntaxException {
        loadTexture(filename);
    }
}
