package thompsonConstruction;

import inputSystem.Input;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Cherry
 * @date 2022/1/23
 * @time 18:46
 * @brief ���� Thompson���취����������ʽת���� NFA
 */

public class ThompsonConstruction {

    private Input input = new Input();
    private MacroHandler macroHandler = null;
    private RegularExpressionHandler regularExpressionHandler = null;
    private Lexer lexer = null;
    private NFAMachineConstructor nfaMachineConstructor = null;
    private NFAPrinter nfaPrinter = new NFAPrinter();
    private NFAPair pair = null;
    private NFAInterpreter nfaInterpreter;
    private DFAConstructor dfaConstructor = null;
    private MinimizeDFA minimizeDFA = null;

    /**
     * ��������ʽ����Ԥ�������� list
     */
    public void runMacroExample() throws IOException {
        //System.out.println("������궨�壺");
        renewInputBuffer("src/macro");
        macroHandler = new MacroHandler(input);
        macroHandler.printMacro();
    }

    /**
     * �������������ʽ���к�չ��
     */
    public void runMacroExpandExample() throws Exception {
        //System.out.println("\n������������ʽ��");
        renewInputBuffer("src/reg");

        System.out.println("\n�������������ʽ��");
        regularExpressionHandler = new RegularExpressionHandler(input, macroHandler);
        regularExpressionHandler.processRegularExpressions();

        for (int i = 0; i < regularExpressionHandler.getRegularExpressionCount(); i++) {
            System.out.println("Line " + (i + 1) + ": " + regularExpressionHandler.getRegularExpression(i));
        }
    }

    /**
     * �Խ������������ʽ���дʷ�����
     */
    public void runLexerExample() {
        lexer = new Lexer(regularExpressionHandler);
        int expressionCount = 0;
        System.out.println("\n��ǰ������������ʽΪ: " +
                regularExpressionHandler.getRegularExpression(lexer.getExpressionCount()));
        lexer.advance();
        while (!lexer.MatchToken(Lexer.Token.END_OF_INPUT)) {
            //������˵�ǰ������ʽ��ĩβ�����ȡ��һ��
            if (lexer.MatchToken(Lexer.Token.EOS)) {
                System.out.print("�� " + lexer.getExpressionCount() + " ��������ʽ�ѽ�����");
                if (lexer.getExpressionCount() == regularExpressionHandler.getRegularExpressionCount()) {
                    System.out.println("��ȫ��������ʽ�ѽ�����");
                } else {
                    System.out.println("����������һ��������ʽ");
                    System.out.println("\n��ǰ������������ʽΪ: " +
                            regularExpressionHandler.getRegularExpression(lexer.getExpressionCount()));
                }
            } else {
                printRegularExpressionResult();
            }
            lexer.advance();
        }
    }

    /**
     * ������ϵͳ�Ļ��������г�ʼ��
     */
    public void renewInputBuffer(String fileName) throws IOException {
        input.ii_newFile(fileName); //�ӿ���̨��ȡ����
        input.ii_advance();
        input.ii_pushback();
    }

    /**
     *
     */
    private void printRegularExpressionResult() {
        System.out.println("��ǰ������ַ�Ϊ: " + lexer.getCurChar() +
                " (" + (char)lexer.getCurChar() + ')');
        if (lexer.MatchToken(Lexer.Token.L)) {
            System.out.println("��ǰ�ַ�����ͨ�ַ�����\n");
        } else {
            printSpecialRegularExpressionResult();
        }
    }

    /**
     * ���дʷ�����ʱ����ÿ���������ַ�
     */
    private void printSpecialRegularExpressionResult() {
        String s = "";
        if (lexer.MatchToken(Lexer.Token.ANY)) {
            s = "��ǰ�ַ��ǵ�ͨ���";
        }

        if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
            s = "��ǰ�ַ��ǿ�ͷƥ���";
        }

        if (lexer.MatchToken(Lexer.Token.AT_EOL)) {
            s = "��ǰ�ַ���ĩβƥ���";
        }

        if (lexer.MatchToken(Lexer.Token.CCL_END)) {
            s = "��ǰ�ַ����ַ������β����";
        }

        if (lexer.MatchToken(Lexer.Token.CCL_START)) {
            s = "��ǰ�ַ����ַ�����Ŀ�ʼ����";
        }

        if (lexer.MatchToken(Lexer.Token.CLOSE_CURLY)) {
            s = "��ǰ�ַ��ǽ�β������";
        }

        if (lexer.MatchToken(Lexer.Token.CLOSE_PAREN)) {
            s = "��ǰ�ַ��ǽ�βԲ����";
        }

        if (lexer.MatchToken(Lexer.Token.DASH)) {
            s = "��ǰ�ַ��Ǻ��";
        }

        if (lexer.MatchToken(Lexer.Token.OPEN_CURLY)) {
            s = "��ǰ�ַ�����ʼ������";
        }

        if (lexer.MatchToken(Lexer.Token.OPEN_PAREN)) {
            s = "��ǰ�ַ�����ʼԲ����";
        }

        if (lexer.MatchToken(Lexer.Token.OPTIONAL)) {
            s = "��ǰ�ַ��ǵ��ַ�ƥ���?";
        }

        if (lexer.MatchToken(Lexer.Token.OR)) {
            s = "��ǰ�ַ��ǻ������";
        }

        if (lexer.MatchToken(Lexer.Token.PLUS_CLOSE)) {
            s = "��ǰ�ַ������հ�������";
        }

        if (lexer.MatchToken(Lexer.Token.CLOSURE)) {
            s = "��ǰ�ַ��Ǳհ�������";
        }

        System.out.println(s + '\n');
    }


    /**
     * ��������ʽת���� NFA
     * @throws Exception Exception
     */
    public void runNFAMachineConstructorExample() throws Exception {
        lexer = new Lexer(regularExpressionHandler);
        nfaMachineConstructor = new NFAMachineConstructor(lexer);
        pair = new NFAPair();

        //nfaMachineConstructor.constructNFAForCharacterSet(pair);
        //nfaMachineConstructor.constructNFAForSingleCharacter(pair);
        //nfaMachineConstructor.constructNFAForDot(pair);

        //nfaMachineConstructor.constructStarClosure(pair);
        //nfaMachineConstructor.cat_expr(pair);
        nfaMachineConstructor.expr(pair);
        nfaPrinter.printNFA(pair.startNode);
    }

    /**
     * �� NFA ����������ַ���
     */
    private void runNFAInterpreterExample() throws IOException {
        System.out.println("Input String:");
        renewInputBuffer(null);
        nfaInterpreter = new NFAInterpreter(pair, input);
        nfaInterpreter.interpret();
    }

    private void runDFAConstructor() {
        System.out.println("\n=============== DFA to NFA ===============");
        dfaConstructor = new DFAConstructor(pair, nfaInterpreter);
        dfaConstructor.convertNFAToDFA();
        dfaConstructor.printDFA();
    }

    private void runMinimizeDFA() {
        System.out.println("\n=================== Minimize DFA ===================");
        minimizeDFA = new MinimizeDFA(dfaConstructor, input);
        minimizeDFA.minimize();
        System.out.println("\n================= Minimize DFA End =================");
    }

    /**
     * ����С���� DFA ����������ַ���
     */
    private void runMinimizedDFAInterpreter() {
        minimizeDFA.MinimizedDFAInterpreter();
    }

    public static void main(String[] args) throws Exception {
        //������ض������ļ�
        //System.setOut(new PrintStream("src/out"));

        ThompsonConstruction thompsonConstruction = new ThompsonConstruction();

        //��ȡ�궨��
        thompsonConstruction.runMacroExample();
        //�����궨�岢չ��
        thompsonConstruction.runMacroExpandExample();
        //���������������ʽ���дʷ�����
        thompsonConstruction.runLexerExample();
        //��������ʽת���� NFA
        thompsonConstruction.runNFAMachineConstructorExample();
        //���� NFA ʶ��������ַ���
        thompsonConstruction.runNFAInterpreterExample();
        //�� NFA ת���� DFA
        thompsonConstruction.runDFAConstructor();
        //��С�� DFA
        thompsonConstruction.runMinimizeDFA();
        //������С�� DFA ʶ��������ַ���
        thompsonConstruction.runMinimizedDFAInterpreter();
    }
}
