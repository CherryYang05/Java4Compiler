/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:14
 * @brief һ��������ѧ���ʽ�Ĵʷ�������
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
     * @param c �ַ�
     * @return boolean
     */
    private boolean isAlnum(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c);
    }

    /**
     * �ʷ�������
     * @return �����ս������
     */
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

        for (int i = 0; i < current.length(); i++) {
            yyleng = 0;
            yytext = current.substring(0, 1);
            switch (current.charAt(i)) {
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
                    break;
                default:
                    if (!isAlnum(current.charAt(i))) {
                        return UNKNOWN_SYMBOL;
                    } else {
                        while (i < current.length() && isAlnum(current.charAt(i))) {
                            i++;
                            yyleng++;
                        } // while (isAlnum(current.charAt(i)))

                        yytext = current.substring(0, yyleng);
                        current = current.substring(yyleng);
                        return NUM_OR_ID;
                    }

            } //switch (current.charAt(i))
        }//  for (int i = 0; i < current.length(); i++)
        return 0;
    }//lex()

    /**
     * �ж��Ƿ���EOI
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
     * �������дʷ�����
     */
    public void advance() {
        lookAhead = lex();
    }

    public void runLexer() {
        while (!match(EOI)) {
            System.out.println("Token" + cnt++ + ": " + token() + ", Symbol: " + yytext);
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
        }
        return token;
    }
}
