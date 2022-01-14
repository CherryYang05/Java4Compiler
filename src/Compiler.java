/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:01
 * @brief
 */

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        //lexer.runLexer();
        //BasicParser basic_parser = new BasicParser(lexer);
        //basic_parser.statements();
        ImprovedParser improvedParser = new ImprovedParser(lexer);
        improvedParser.statements();
    }
}

