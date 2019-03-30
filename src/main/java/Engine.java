import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Engine {
    private String WINDOW_TITLE = "Flutterverse: THE LIVING VULPIE SHADER MACHINE";

    private long window;

    private int windowWidth = 512;
    private int windowHeight = 512;

    private boolean windowFullscreen = false;
    private boolean windowWireframe = false;
    private boolean windowLocked = true;

    private Camera camera = new Camera();

    // glfw handling
    private void onKey(long window, int key, int scancode, int action, int mode) {
        if (windowLocked == false) return;

        camera.onKey(key, action);

        if (action == GLFW_PRESS) {
            switch (key) {
                case GLFW_KEY_ESCAPE:
                    if (windowLocked) {
                        windowLocked = false;
                        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                    } else {
                        glfwSetWindowShouldClose(window, true);
                    }
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
                                    windowWidth, windowHeight, videoMode.refreshRate());
                        }
                    }
                    break;

                case GLFW_KEY_F1:
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
    private void onFrameBufferSize(long window, int width, int height) {
        windowWidth = width;
        windowHeight = height;
        glViewport(0,0,windowWidth,windowWidth);
    }
    private void onCursorPosition(long window, double x, double y) {
        if (windowLocked == false) return;
        camera.onCursorPosition(x, y);
    }
    private void onMouseButton(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) {
            if (windowLocked == false) {
                windowLocked = true;

                glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                glfwFocusWindow(window);
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

            glfwSetWindowTitle(window, WINDOW_TITLE+" ("+String.format("%.2f", fps)+" fps)");
                //"Frame Time: "+ String.format("%.2f", msPerFrame)+" (ms)";

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

        window = glfwCreateWindow(windowWidth, windowHeight, WINDOW_TITLE, NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new IllegalStateException("Failed to create GLFW window");
        }

        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode videoMode = glfwGetVideoMode(monitor);

        glfwSetWindowPos(window,
            videoMode.width()/2-windowWidth/2,
            videoMode.height()/2-windowHeight/2
        );

        glfwSetCursorPos(window, 0, 0);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwFocusWindow(window);

        glfwSetKeyCallback(window, this::onKey);
        glfwSetFramebufferSizeCallback(window, this::onFrameBufferSize);
        glfwSetCursorPosCallback(window, this::onCursorPosition);
        glfwSetMouseButtonCallback(window, this::onMouseButton);

        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        glClearColor(1.0f, 0.0f, 0.5f, 0.0f);
        //glClearColor(0.611764706f, 0.654901961f, 0.658823529f, 0.0f);

        glEnable(GL_DEPTH_TEST);

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
    private void init() {
        initOpenGL();
    }

    // updating functions
    private void draw() {

    }

    private void update() throws IOException {
        // entities
        Entity vulpie = new Entity();
        vulpie.loadObjWithTexture("models/vulpie.obj", "textures/vulpie.jpg");

        Entity floor = new Entity();
        floor.loadObjWithTexture("models/floor.obj", "textures/floor.jpg");

        // shaders
        Shader standard = new Shader();
        standard.addShader("shaders/standard.vs", GL_VERTEX_SHADER);
        standard.addShader("shaders/standard.fs", GL_FRAGMENT_SHADER);
        standard.createProgram();

        Shader cute = new Shader();
        cute.addShader("shaders/standard.vs", GL_VERTEX_SHADER);
        cute.addShader("shaders/cute.fs", GL_FRAGMENT_SHADER);
        cute.createProgram();

        // loop
        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            double currentTime = glfwGetTime();
            double dt = currentTime - lastTime;
            lastTime = currentTime;

            showFPS();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            vulpie.position.set(
                (float)(Math.sin(currentTime*2)),
                (float)(Math.sin(currentTime*8)*0.1 + 0.1),
                (float)(Math.cos(currentTime*4)*0.1)
            );
            vulpie.rotation.set(
                0,
                (float)(Math.sin(currentTime*16)*0.25),
                (float)(Math.cos(currentTime*8)*0.25)
            );

            camera.update((float)dt);

            standard.use();
            standard.setUniform("iTime", (float)currentTime);
            standard.setUniform("view", camera.view);
            standard.setUniform("projection", camera.projection);
            vulpie.draw(standard);

            cute.use();
            cute.setUniform("iTime", (float)currentTime);
            cute.setUniform("view", camera.view);
            cute.setUniform("projection", camera.projection);
            floor.draw(cute);

            glfwSwapBuffers(window);
        }

        standard.cleanup();
        cute.cleanup();

        vulpie.cleanup();
        floor.cleanup();
    }

    // de-initialization
    private void deinit() {
        glfwTerminate();
    }

    public void run(Engine engine) throws IOException {
        engine.init();
        engine.update();
        engine.deinit();
    }
}
