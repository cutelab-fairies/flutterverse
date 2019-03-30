package Engine;

import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;

public class ImageData {
    public int width;
    public int height;
    public int components;
    public ByteBuffer data;

    public ImageData(String filename) throws IOException {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);

        ByteBuffer imageBuffer = Utils.loadFileByteBuffer(filename);

        if (!stbi_info_from_memory(imageBuffer, width, height, components))
            throw new IOException("Failed to read image information: "+stbi_failure_reason());

        this.data = stbi_load_from_memory(imageBuffer, width, height, components, STBI_rgb_alpha);
        if (this.data == null) throw new AssertionError("Could not load image!");

        this.width = width.get();
        this.height = height.get();
        this.components = components.get();
    }

    public void free() {
        stbi_image_free(data);
    }
}
