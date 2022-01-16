/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:01
 * @brief 编译器入口
 */

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        //词法分析
        //lexer.runLexer();

        //语法分析
        BasicParser basic_parser = new BasicParser(lexer);
        basic_parser.statements();

        //改进的语法分析
        //ImprovedParser improvedParser = new ImprovedParser(lexer);
        //improvedParser.statements();

        //代码生成
        //Parser parser = new Parser(lexer);
        //parser.statements();
    }
}

