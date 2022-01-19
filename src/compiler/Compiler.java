package compiler;

/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:01
 * @brief ���������
 */

public class Compiler {
    public static void main(String[] args) {
        Lexer lexer = new Lexer();
        //�ʷ�����
        lexer.runLexer();

        //�﷨����
        //compiler.BasicParser basic_parser = new compiler.BasicParser(lexer);
        //basic_parser.statements();

        //�Ľ����﷨����
        ImprovedParser improvedParser = new ImprovedParser(lexer);
        improvedParser.statements();

        //��������
        Parser parser = new Parser(lexer);
        parser.statements();
    }
}

