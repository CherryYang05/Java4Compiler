package thompsonConstruction;

import inputSystem.Input;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Cherry
 * @date 2022/1/23
 * @time 18:46
 * @brief 利用 Thompson构造法，将正则表达式转化成 NFA
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
     * 对正则表达式进行预处理，存入 list
     */
    public void runMacroExample() throws IOException {
        //System.out.println("请输入宏定义：");
        renewInputBuffer("src/macro");
        macroHandler = new MacroHandler(input);
        macroHandler.printMacro();
    }

    /**
     * 将输入的正则表达式进行宏展开
     */
    public void runMacroExpandExample() throws Exception {
        //System.out.println("\n请输入正则表达式：");
        renewInputBuffer("src/reg");

        System.out.println("\n解析后的正则表达式：");
        regularExpressionHandler = new RegularExpressionHandler(input, macroHandler);
        regularExpressionHandler.processRegularExpressions();

        for (int i = 0; i < regularExpressionHandler.getRegularExpressionCount(); i++) {
            System.out.println("Line " + (i + 1) + ": " + regularExpressionHandler.getRegularExpression(i));
        }
    }

    /**
     * 对解析后的正则表达式进行词法分析
     */
    public void runLexerExample() {
        lexer = new Lexer(regularExpressionHandler);
        int expressionCount = 0;
        System.out.println("\n当前解析的正则表达式为: " +
                regularExpressionHandler.getRegularExpression(lexer.getExpressionCount()));
        lexer.advance();
        while (!lexer.MatchToken(Lexer.Token.END_OF_INPUT)) {
            //如果到了当前正则表达式的末尾，则获取下一行
            if (lexer.MatchToken(Lexer.Token.EOS)) {
                System.out.print("第 " + lexer.getExpressionCount() + " 个正则表达式已解析完");
                if (lexer.getExpressionCount() == regularExpressionHandler.getRegularExpressionCount()) {
                    System.out.println("，全部正则表达式已解析完");
                } else {
                    System.out.println("，将解析下一个正则表达式");
                    System.out.println("\n当前解析的正则表达式为: " +
                            regularExpressionHandler.getRegularExpression(lexer.getExpressionCount()));
                }
            } else {
                printRegularExpressionResult();
            }
            lexer.advance();
        }
    }

    /**
     * 对输入系统的缓冲区进行初始化
     */
    public void renewInputBuffer(String fileName) throws IOException {
        input.ii_newFile(fileName); //从控制台获取输入
        input.ii_advance();
        input.ii_pushback();
    }

    /**
     *
     */
    private void printRegularExpressionResult() {
        System.out.println("当前处理的字符为: " + lexer.getCurChar() +
                " (" + (char)lexer.getCurChar() + ')');
        if (lexer.MatchToken(Lexer.Token.L)) {
            System.out.println("当前字符是普通字符常量\n");
        } else {
            printSpecialRegularExpressionResult();
        }
    }

    /**
     * 进行词法解析时解析每个单独的字符
     */
    private void printSpecialRegularExpressionResult() {
        String s = "";
        if (lexer.MatchToken(Lexer.Token.ANY)) {
            s = "当前字符是点通配符";
        }

        if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
            s = "当前字符是开头匹配符";
        }

        if (lexer.MatchToken(Lexer.Token.AT_EOL)) {
            s = "当前字符是末尾匹配符";
        }

        if (lexer.MatchToken(Lexer.Token.CCL_END)) {
            s = "当前字符是字符集类结尾括号";
        }

        if (lexer.MatchToken(Lexer.Token.CCL_START)) {
            s = "当前字符是字符集类的开始括号";
        }

        if (lexer.MatchToken(Lexer.Token.CLOSE_CURLY)) {
            s = "当前字符是结尾大括号";
        }

        if (lexer.MatchToken(Lexer.Token.CLOSE_PAREN)) {
            s = "当前字符是结尾圆括号";
        }

        if (lexer.MatchToken(Lexer.Token.DASH)) {
            s = "当前字符是横杠";
        }

        if (lexer.MatchToken(Lexer.Token.OPEN_CURLY)) {
            s = "当前字符是起始大括号";
        }

        if (lexer.MatchToken(Lexer.Token.OPEN_PAREN)) {
            s = "当前字符是起始圆括号";
        }

        if (lexer.MatchToken(Lexer.Token.OPTIONAL)) {
            s = "当前字符是单字符匹配符?";
        }

        if (lexer.MatchToken(Lexer.Token.OR)) {
            s = "当前字符是或操作符";
        }

        if (lexer.MatchToken(Lexer.Token.PLUS_CLOSE)) {
            s = "当前字符是正闭包操作符";
        }

        if (lexer.MatchToken(Lexer.Token.CLOSURE)) {
            s = "当前字符是闭包操作符";
        }

        System.out.println(s + '\n');
    }


    /**
     * 将正则表达式转化成 NFA
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
     * 用 NFA 解析输入的字符串
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
     * 用最小化的 DFA 解析输入的字符串
     */
    private void runMinimizedDFAInterpreter() {
        minimizeDFA.MinimizedDFAInterpreter();
    }

    public static void main(String[] args) throws Exception {
        //将输出重定向至文件
        //System.setOut(new PrintStream("src/out"));

        ThompsonConstruction thompsonConstruction = new ThompsonConstruction();

        //读取宏定义
        thompsonConstruction.runMacroExample();
        //解析宏定义并展开
        thompsonConstruction.runMacroExpandExample();
        //根据输入的正则表达式进行词法分析
        thompsonConstruction.runLexerExample();
        //将正则表达式转化成 NFA
        thompsonConstruction.runNFAMachineConstructorExample();
        //根据 NFA 识别输入的字符串
        thompsonConstruction.runNFAInterpreterExample();
        //将 NFA 转化成 DFA
        thompsonConstruction.runDFAConstructor();
        //最小化 DFA
        thompsonConstruction.runMinimizeDFA();
        //根据最小化 DFA 识别输入的字符串
        thompsonConstruction.runMinimizedDFAInterpreter();
    }
}
