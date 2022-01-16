/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:14
 * @brief һ��������ѧ���ʽ�Ĵʷ�������
 * ���Լ��ո񣬽��ո��Թ�
 */

import java.util.Scanner;

public class Lexer {
    public static final int EOI = 0;                //end of input
    public static final int SEMI = 1;               //�ֺ�
    public static final int PLUS = 2;               //�Ӻ�
    public static final int MINUS = 3;              //����
    public static final int TIMES = 4;              //�˺�
    public static final int DIV = 5;                //����
    public static final int LP = 6;                 //������
    public static final int RP = 7;                 //������
    public static final int NUM_OR_ID = 8;          //���ֻ���ĸ
    public static final int UNKNOWN_SYMBOL = 9;     //δ֪�ַ�
    public static final int BLANK = 10;             //�ո�

    private int lookAhead = -1;

    public String yytext = "";                      //��ֵ�ÿ������
    public int yyleng = 0;                          //��ֵ�ÿ�����ŵĳ���
    public int yylineno = 0;                        //����ı��ʽ������

    private String input_buffer = "";               //���б��ʽ���ӵ�һ��
    private String current = "";                    //��ǰ��ʾ�ķ���(��12��+��*��)
    private int cnt = 1;                            //token����
    private boolean flag = true;                    //������ʽ���

    /**
     * �ж��Ƿ����ַ�������
     *
     * @param c �ַ�
     * @return boolean
     */
    private boolean isAlnum(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c);
    }

    /**
     * �ʷ�������
     *
     * @return �����ս������
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
            s.close();                              //����������Ҫ�ر�

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

        int pos = 0;            //ָ��ǰ���ڴ����λ��
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
                return lex();                //��������ո񣬺��Ե�ǰ�ַ������������ȡ
            default:
                if (!isAlnum(current.charAt(pos))) {
                    return UNKNOWN_SYMBOL;
                } else {
                    //������ֳ��ȳ���1����ѭ������ȫ����ȡ����
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
     * �ж��Ƿ���EOI
     *
     * @param token ����Ĳ���ʼ��ΪEOI
     * @return boolean
     */
    public boolean match(int token) {
        if (lookAhead == -1) {
            lookAhead = lex();
        }
        return token == lookAhead;
    }

    /**
     * �������дʷ������������һ���ַ�
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
