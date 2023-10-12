import node.CompUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) throws IOException {
        String in = Files.readString(Paths.get("testfile.txt"));
        Lexer lexer = new Lexer(in);
        Parser parser = new Parser(lexer);
        CompUnit compUnit = parser.compUnit();
        compUnit.print();
    }
}