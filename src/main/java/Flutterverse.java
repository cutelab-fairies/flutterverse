import Engine.Engine;

import java.io.IOException;
import java.net.URISyntaxException;

public class Flutterverse {

    public static void main(String[] args) throws IOException, URISyntaxException, NoSuchMethodException {
        System.out.println("Starting the Flutterverse engine!");

        Engine engine = new Engine();
        engine.run(engine);


    }
}
