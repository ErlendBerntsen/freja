package no.hvl;

import java.io.IOException;

public class Main {

    public static void main (String[] args) throws IOException {
        Configuration config = new Configuration("", "");
        Generator generator = new Generator(config);
        generator.generate();
    }

}