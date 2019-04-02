package Engine;

import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Engine {
    public long window;

    private String windowTitle = "Flutterverse: THE VULPIE VORTEX SHADER MACHINE";
    private int windowWidth = 1280;
    private int windowHeight = 720;

    private boolean windowFullscreen = false;
    private boolean windowWireframe = false;
    private boolean windowLocked = true;

    public Camera camera = new Camera(windowWidth, windowHeight);

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
        glViewport(0,0,windowWidth,windowHeight);
        camera.onFrameBufferSize(window, windowWidth, windowHeight);
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
                glfwSetCursorPos(window, windowWidth/2, windowHeight/2);
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
            //double msPerFrame = 1000.0/fps;

            glfwSetWindowTitle(window, windowTitle+" ("+String.format("%.2f", fps)+" fps)");
                //"Frame Time: "+ String.format("%.2f", msPerFrame)+" (ms)";

            fpsFrameCount = 0;
        }

        fpsFrameCount++;
    }

    // initialization
    private void initOpenGL() throws IOException {
        if (!glfwInit())
            throw new IllegalStateException("GLFW initialization failed");

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(windowWidth, windowHeight, windowTitle, NULL, NULL);
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

        // hides and focues
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPos(window, windowWidth/2, windowHeight/2);
        glfwFocusWindow(window);

        glfwSetKeyCallback(window, this::onKey);
        glfwSetFramebufferSizeCallback(window, this::onFrameBufferSize);
        glfwSetCursorPosCallback(window, this::onCursorPosition);
        glfwSetMouseButtonCallback(window, this::onMouseButton);

        GLFWImage icon = GLFWImage.malloc();
        GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
        ImageData iconData = new ImageData("images/icon.png");
        icon.set(iconData.width, iconData.height, iconData.data);
        iconBuffer.put(0, icon);
        glfwSetWindowIcon(window, iconBuffer);

        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        glViewport(0,0,windowWidth,windowHeight);
        glClearColor(1.0f, 0.0f, 0.5f, 0.0f);
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

    private void init() throws IOException, URISyntaxException {
        initOpenGL();
        onInit();
    }

    // updating functions
    private void update() {
        // networking
//        Scene networkScene = new Scene();
//        List<Entity> playerEntities = new ArrayList<>();
//        for (int i=0; i<4; i++) {
//            Entity playerEntity = networkScene.createEntity();
//            playerEntity.loadObjWithTexture("models/vulpie.obj", "textures/vulpie.jpg");
//            playerEntities.add(playerEntity);
//        }
//        NetworkClient networkClient = new NetworkClient(camera, playerEntities);

        // loop
        float lastTime = (float)glfwGetTime();
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            float time = (float)glfwGetTime();
            float dt = time - lastTime;
            lastTime = time;

            showFPS();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            onUpdate(time, dt);
            camera.update(dt);

            glfwSwapBuffers(window);
        }
    }


    // de-initialization
    private void deinit() {
        onDeinit();
        glfwTerminate();
    }

    // handlers
    public void onInit() throws IOException, URISyntaxException { }
    public void onUpdate(float time, float dt) { }
    public void onDeinit() { }

    public void run() throws IOException, URISyntaxException {
        System.out.println("Starting the Flutterverse engine!");
        init();
        update();
        deinit();
    }

    public Engine() {

    }
}
