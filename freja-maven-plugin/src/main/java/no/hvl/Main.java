package no.hvl;

public class Main {

    public static void main (String[] args) throws Exception {
        Configuration config = new Configuration("", "");
        Generator generator = new Generator(config);
        generator.generate();
    }

}