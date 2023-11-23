package config;

import java.io.File;

public class Config {
    public static String inputPath = "testfile.txt";
    public static String outputPath = "output.txt";
    public static String errorPath = "error.txt";
    public static String llvmPath = "llvm_ir.txt";
    public static String mipsPath = "mips.txt";
    public static boolean parserMessage = false;
    public static boolean checkError = false;
    public static boolean genLLVM = true;
    public static boolean genMIPS = true;

    public static void init() {
        clearFile(outputPath);
        clearFile(errorPath);
        clearFile(llvmPath);
        clearFile(mipsPath);
    }

    private static void clearFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
