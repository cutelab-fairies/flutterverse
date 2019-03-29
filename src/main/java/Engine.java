import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Engine {
    private String WINDOW_TITLE = "Flutterverse";

    private long window;

    private int windowWidth = 512;
    private int windowHeight = 512;

    private boolean windowFullscreen = false;
    private boolean windowWireframe = false;

    // glfw handling
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
                                    windowWidth, windowHeight, videoMode.refreshRate());
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
    private void onFrameBufferSize(long window, int width, int height) {
        windowWidth = width;
        windowHeight = height;
        glViewport(0,0,windowWidth,windowWidth);
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

        window = glfwCreateWindow(windowWidth, windowHeight, WINDOW_TITLE, NULL, NULL);

        if (window == NULL) {
            glfwTerminate();
            throw new IllegalStateException("Failed to create GLFW window");
        }

        glfwSetKeyCallback(window, this::onKey);
        glfwSetFramebufferSizeCallback(window, this::onFrameBufferSize);

        glfwMakeContextCurrent(window);

        GL.createCapabilities();

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

    // updating functions
    private void draw() {

    }

    private void update() throws IOException {
        // models
        Model vulpieModel = new Model(GL_STATIC_DRAW);
        vulpieModel.loadObj("models/vulpie.obj");
        vulpieModel.make();

        Vector3f vulpiePosition = new Vector3f(0,0,-3);
        float vulpieRotation = 0;

        // textures
        Texture vulpieTexture = new Texture("textures/vulpie.jpg");

        // shaders
        Shader shader = new Shader();
        shader.addShader("shaders/vertexShader.vs", GL_VERTEX_SHADER);
        shader.addShader("shaders/fragShader.fs", GL_FRAGMENT_SHADER);
        shader.createProgram();

        // loop
        double lastTime = glfwGetTime();

        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            showFPS();

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            vulpieTexture.bind();

            vulpieRotation += (float)(deltaTime*Math.toRadians(90));
            if (vulpieRotation >= 360) vulpieRotation -= 360;

            Matrix4f model = new Matrix4f();
            Matrix4f view = new Matrix4f();
            Matrix4f projection = new Matrix4f();

            model.translate(vulpiePosition);
            model.rotate(vulpieRotation,0,1,0);

            Vector3f camPos = new Vector3f(0,0.08f,0);
            Vector3f camTarget= new Vector3f(0,0,-1);
            Vector3f camUp = new Vector3f(0,1,0);

            view.lookAt(camPos, camTarget, camUp);

            projection.perspective((float)Math.toRadians(45),windowWidth/windowHeight,0.1f,100);
            
            shader.use();
            shader.setUniform("iTime", (float)currentTime);
            shader.setUniform("model", model);
            shader.setUniform("view", view);
            shader.setUniform("projection", projection);

            vulpieModel.draw();

            glfwSwapBuffers(window);
        }

        shader.cleanup();
        vulpieModel.cleanup();
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
