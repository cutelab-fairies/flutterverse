import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Engine {
    private String WINDOW_TITLE = "Flutterverse";
    private int WINDOW_WIDTH = 512;
    private int WINDOW_HEIGHT = 512;

    private long window;
    private boolean windowFullscreen = false;
    private boolean windowWireframe = false;

    // key handling
    private void onKey(long _window, int key, int scancode, int action, int mode) {
        if (action == GLFW_PRESS) {
            switch (key) {
                case GLFW_KEY_ESCAPE:
                    glfwSetWindowShouldClose(window, true);
                    break;

                case GLFW_KEY_F11:
                    windowFullscreen = !windowFullscreen;

                    long monitor = glfwGetPrimaryMonitor();
                    GLFWVidMode videoMode = glfwGetVideoMode(monitor);

                    //if (videoMode != null) {
                    if (false) {
                        if (windowFullscreen) {
                            glfwSetWindowMonitor(window, monitor, 0, 0,
                                    videoMode.width(), videoMode.height(), videoMode.refreshRate());
                        } else {
                            glfwSetWindowMonitor(window, monitor, 0, 0,
                                    WINDOW_WIDTH, WINDOW_HEIGHT, videoMode.refreshRate());
                        }
                    }
                    break;

                case GLFW_KEY_W:
                    windowWireframe = !windowWireframe;
                    if (windowWireframe) {
                        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                    } else {
                        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                    }
                    break;
            }
        }
    }

    // frame rate
    private double fpsPreviousSeconds = 0.0;
    private int fpsFrameCount = 0;
    private void showFPS() {
        double currentSeconds = glfwGetTime(); // seconds since started
        double elapsedSeconds = currentSeconds - fpsPreviousSeconds;

        // limit text update 4 times per second
        if (elapsedSeconds > 0.25) {
            fpsPreviousSeconds = currentSeconds;
            double fps = fpsFrameCount/elapsedSeconds;
            double msPerFrame = 1000.0/fps;

            glfwSetWindowTitle(window, WINDOW_TITLE+" - FPS: "+
                    String.format("%.2f", fps)+" - Frame Time: "+
                    String.format("%.2f", msPerFrame)+" (ms)");

            fpsFrameCount = 0;
        }

        fpsFrameCount++;
    }

    // initialization
    private void initOpenGL() {
        if (!glfwInit())
            throw new IllegalStateException("GLFW initialization failed");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);

        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE, NULL, NULL);

        if (window == NULL) {
            glfwTerminate();
            throw new IllegalStateException("Failed to create GLFW window");
        }

        glfwSetKeyCallback(window, this::onKey);
        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        glClearColor(1.0f, 0.0f, 0.5f, 0.0f);

        // disable anti aliasing
        glDisable(GL_DITHER);
        glDisable(GL_POINT_SMOOTH);
        glDisable(GL_LINE_SMOOTH);
        glDisable(GL_POLYGON_SMOOTH);
        glHint(GL_POINT_SMOOTH, GL_DONT_CARE);
        glHint(GL_LINE_SMOOTH, GL_DONT_CARE);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_DONT_CARE);
        glDisable(GL_MULTISAMPLE);
    }

    // updating functions
    private void draw() {

    }

    private void update() throws IOException {
        // make model
        Model triangle = new Model(GL_STATIC_DRAW);
        triangle.loadObj("models/sphere.obj");
        triangle.make();

        // shaders
        Shader shader = new Shader();
        shader.addShader("shaders/vertexShader.vs", GL_VERTEX_SHADER);
        shader.addShader("shaders/fragShader.fs", GL_FRAGMENT_SHADER);
        shader.createProgram();

        // loop
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            showFPS();

            glClear(GL_COLOR_BUFFER_BIT);

            shader.use();
            shader.setUniform("iTime", (float)glfwGetTime());

            triangle.draw();

            glfwSwapBuffers(window);
        }

        shader.cleanup();
        triangle.cleanup();
    }

    // de-initialization
    private void deinit() {
        glfwTerminate();
    }

    public void run(Engine engine) throws IOException {
        engine.initOpenGL();
        engine.update();
        engine.deinit();
    }
}
