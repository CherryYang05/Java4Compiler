/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:14
 * @brief 一个简易数学表达式的词法分析器
 * 可以检测空格，将空格略过
 */

import java.util.Scanner;

public class Lexer {
    public static final int EOI = 0;                //end of input
    public static final int SEMI = 1;               //分号
    public static final int PLUS = 2;               //加号
    public static final int MINUS = 3;              //减号
    public static final int TIMES = 4;              //乘号
    public static final int DIV = 5;                //除号
    public static final int LP = 6;                 //左括号
    public static final int RP = 7;                 //右括号
    public static final int NUM_OR_ID = 8;          //数字或字母
    public static final int UNKNOWN_SYMBOL = 9;     //未知字符
    public static final int BLANK = 10;             //空格

    private int lookAhead = -1;

    public String yytext = "";                      //拆分的每个符号
    public int yyleng = 0;                          //拆分的每个符号的长度
    public int yylineno = 0;                        //输入的表达式的行数

    private String input_buffer = "";               //所有表达式连接到一起
    private String current = "";                    //当前表示的符号(如12，+，*等)
    private int cnt = 1;                            //token计数
    private boolean flag = true;                    //读入表达式标记

    /**
     * 判断是否是字符和数字
     *
     * @param c 字符
     * @return boolean
     */
    private boolean isAlnum(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c);
    }

    /**
     * 词法分析器
     *
     * @return 返回终结符类型
     */
    //static int type;
    private int lex() {
        while (flag) {                              //current == ""
            Scanner s = new Scanner(System.in);
            while (true) {
                String line = s.nextLine();
                if (line.equals("end")) {
                    flag = false;
                    break;
                }
                input_buffer += line;
            }
            s.close();                              //输入流用完要关闭

            if (input_buffer.length() == 0) {
                current = "";
                return EOI;
            }
            current = input_buffer;
            yylineno++;
            current = current.trim();
        }//while (flag)

        if (current.isEmpty()) {
            return EOI;
        }

        int pos = 0;            //指向当前正在处理的位置
        yyleng = 0;
        yytext = current.substring(0, 1);
        switch (current.charAt(pos)) {
            case ';':
                current = current.substring(1);
                return SEMI;
            case '+':
                current = current.substring(1);
                return PLUS;
            case '-':
                current = current.substring(1);
                return MINUS;
            case '*':
                current = current.substring(1);
                return TIMES;
            case '/':
                current = current.substring(1);
                return DIV;
            case '(':
                current = current.substring(1);
                return LP;
            case ')':
                current = current.substring(1);
                return RP;
            case '\n':
            case '\t':
            case ' ':
                current = current.substring(1);
                return lex();                //如果遇到空格，忽略当前字符，继续往后读取
            default:
                if (!isAlnum(current.charAt(pos))) {
                    return UNKNOWN_SYMBOL;
                } else {
                    //如果数字长度超过1，用循环将其全部提取出来
                    while (pos < current.length() && isAlnum(current.charAt(pos))) {
                        pos++;
                        yyleng++;
                    }
                    yytext = current.substring(0, yyleng);
                    current = current.substring(yyleng);
                    return NUM_OR_ID;
                }
        } //switch (current.charAt(i))
        //return 0;
    }//lex()

    /**
     * 判断是否是EOI
     *
     * @param token 传入的参数始终为EOI
     * @return boolean
     */
    public boolean match(int token) {
        if (lookAhead == -1) {
            lookAhead = lex();
        }
        return token == lookAhead;
    }

    /**
     * 继续进行词法分析，获得下一个字符
     */
    public void advance() {
        lookAhead = lex();
    }

    public void runLexer() {
        while (!match(EOI)) {
            //if (!match(BLANK)) {
            System.out.println("Token " + cnt++ + ": " + token() + ", Symbol: " + yytext);
            //}
            advance();
        }
    }

    private String token() {
        String token = "";
        switch (lookAhead) {
            case EOI:
                token = "EOI";
                break;
            case PLUS:
                token = "PLUS";
                break;
            case MINUS:
                token = "MINUS";
                break;
            case TIMES:
                token = "TIMES";
                break;
            case DIV:
                token = "DIV";
                break;
            case NUM_OR_ID:
                token = "NUM_OR_ID";
                break;
            case SEMI:
                token = "SEMI";
                break;
            case LP:
                token = "LP";
                break;
            case RP:
                token = "RP";
                break;
            case BLANK:
                token = "BLANK";
                break;
        }
        return token;
    }
}
