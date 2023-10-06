import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Compiler {
    public static void main(String[] args) throws IOException {
        String in = Files.readString(Paths.get("testfile.txt"));
        PrintWriter printWriter = new PrintWriter(new FileWriter("output.txt"));
        Lexer lexer = new Lexer(in);
        while (lexer.hasNext()) {
            printWriter.println(lexer.getSymbol() + ' ' + lexer.getToken());
        }
        printWriter.close();
    }
}