package config;

import java.io.File;

public class Config {
    public static String inputPath = "testfile.txt";
    public static String outputPath = "output.txt";
    public static String errorPath = "error.txt";
    public static boolean parserMessage = false;
    public static boolean checkError = true;

    public static void init() {
        clearFile(outputPath);
        clearFile(errorPath);
    }

    private static void clearFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
