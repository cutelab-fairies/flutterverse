import Engine.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class Flutterverse {
    private Engine engine;

    public Flutterverse() throws IOException, URISyntaxException {
        engine = new Engine();
        engine.run();
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        new Flutterverse();
    }
}
