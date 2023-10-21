import config.Config;
import error.ErrorHandler;
import node.CompUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) throws IOException {
        Config.init();//清空输出文件等
        String in = Files.readString(Paths.get(Config.inputPath));//input
        Lexer lexer = new Lexer(in);
        Parser parser = new Parser(lexer);
        CompUnit compUnit = parser.compUnit();
        if (Config.parserMessage) {
            compUnit.print();
        }
        if (Config.checkError) {
            compUnit.checkError();
            ErrorHandler.getInstance().logErrors();
        }
    }
}