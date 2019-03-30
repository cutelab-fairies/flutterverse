package Engine;

import org.lwjgl.BufferUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;

public class Utils {
    public static ClassLoader getMainClass() {
        ClassLoader classLoader = new Engine().getClass().getClassLoader();
        return classLoader;
    }

    public static String loadFile(String filename) throws IOException {
        InputStream stream = getMainClass().getResourceAsStream(filename);

        int ch;
        StringBuilder builder = new StringBuilder();

        while((ch = stream.read()) != -1)
            builder.append((char)ch);

        return builder.toString();
    }

    public static ByteBuffer loadFileByteBuffer(String filename) throws IOException {
        InputStream stream = getMainClass().getResourceAsStream(filename);

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        int ch;
        byte[] buffer = new byte[1024];

        while ((ch = stream.read(buffer)) != -1) {
            output.write(buffer, 0, ch);
        }

        byte[] byteArray = output.toByteArray();
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(byteArray.length);
        byteBuffer.put(byteArray);
        byteBuffer.flip();

        return byteBuffer;
    }

    public static ByteBuffer loadImage(String filename) throws IOException {
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);
        ByteBuffer imageBuffer = Utils.loadFileByteBuffer(filename);

        if (!stbi_info_from_memory(imageBuffer, width, height, components))
            throw new IOException("Failed to read image information: "+stbi_failure_reason());

        ByteBuffer imageData = stbi_load_from_memory(imageBuffer, width, height, components, STBI_rgb_alpha);
        if (imageData == null) throw new AssertionError("Could not load image!");

        return imageData;
    }
}
