package thompsonConstruction;

import javax.tools.Tool;

/**
 * @author Cherry
 * @date 2022/1/25
 * @time 23:11
 * @brief ���ڽ������������ʽ�Ĵʷ�����
 */

public class Lexer {
    public enum Token {
        EOS,            //������ʽĩβ
        ANY,            // . ͨ���
        AT_BOL,         //^ ��ͷƥ���
        AT_EOL,         //$ ĩβƥ���
        CCL_END,        //�ַ������β���� ]
        CCL_START,      //�ַ����࿪ʼ���� [
        CLOSE_CURLY,    // }
        CLOSE_PAREN,    //)
        CLOSURE,        //*
        DASH,           // -
        END_OF_INPUT,   //����������
        L,              //�ַ�����
        OPEN_CURLY,     // {
        OPEN_PAREN,     // (
        OPTIONAL,       // ?
        OR,             // |
        PLUS_CLOSE,
        JMP
    }

    private final int ASCII_COUNT = 128;                //ASCII �ַ��������һ��ֻ�� 128 ��
    private Token[] tokenMap = new Token[ASCII_COUNT];  //������¼ÿ�� ASCII �ַ��� Token ����
    private Token curToken = Token.EOS;                 //��¼��ǰ�ַ��� Token
    private RegularExpressionHandler regularExpressionHandler = null;
    private String curExpr = "";                        //��ǰ�������������ʽ
    private int expressionCount = 0;                    //��¼��ǰ���ڴ���ڼ���������ʽ
    private char curChar;                               //��¼��ǰ�ַ������ڴ�����ַ�
    private int curPos = 0;                             //��¼��ǰ�ַ������ڴ�����ַ���ָ��
    private boolean inQuoted = false;                   //�Ƿ���˫������
    private boolean sawEsc = false;                     //�Ƿ�����ת��� \

    public Lexer(RegularExpressionHandler regularExpressionHandler) {
        initTokenMap();
        this.regularExpressionHandler = regularExpressionHandler;
    }

    /**
     * �� Token �ĺ���������г�ʼ��
     */
    private void initTokenMap() {
        for (int i = 0; i < ASCII_COUNT; i++) {
            tokenMap[i] =Token.L;
        }

        tokenMap['.'] = Token.ANY;
        tokenMap['^'] = Token.AT_BOL;
        tokenMap['$'] = Token.AT_EOL;
        tokenMap[']'] = Token.CCL_END;
        tokenMap['['] = Token.CCL_START;
        tokenMap['}'] = Token.CLOSE_CURLY;
        tokenMap[')'] = Token.CLOSE_PAREN;
        tokenMap['*'] = Token.CLOSURE;
        tokenMap['-'] = Token.DASH;
        tokenMap['{'] = Token.OPEN_CURLY;
        tokenMap['('] = Token.OPEN_PAREN;
        tokenMap['?'] = Token.OPTIONAL;
        tokenMap['|'] = Token.OR;
        tokenMap['+'] = Token.PLUS_CLOSE;
    }

    /**
     * ���ڶ���һ���ַ����дʷ�����
     * @return ���ص�ǰ�����ַ��� Token
     */
    public Token advance() {

        //˵������������ʽ���������ˣ����� END_OF_INPUT
        if (MatchToken(Token.EOS) &&
                expressionCount >= regularExpressionHandler.getRegularExpressionCount()) {
            curToken = Token.END_OF_INPUT;
            return curToken;
        }

        //�����ǰ�д����꣬������һ��
        if (MatchToken(Token.EOS)) {
            curExpr = regularExpressionHandler.getRegularExpression(expressionCount);
            expressionCount++;
            curPos = 0;
        }

        if (curPos >= curExpr.length()) {
            curToken = Token.EOS;
            return curToken;
        }

        if (curExpr.charAt(curPos) == '"') {
            curPos++;
            inQuoted = !inQuoted;
        }
        sawEsc = (curExpr.charAt(curPos) == '\\');

        if (sawEsc && curExpr.charAt(curPos + 1) != '"' && !inQuoted) {
            curChar = (char)handleEsc();
        } else {
            curChar = curExpr.charAt(curPos);
            curPos++;
        }
        curToken = (inQuoted) ? Token.L : tokenMap[curChar];
        return curToken;
    }

    private int handleEsc() {
        /*��ת�Ʒ� \ ����ʱ���������������������ַ����ַ���һ����
         *���Ǵ����ת���ַ������¼�����ʽ
         * \b backspace
         * \f formfeed
         * \n newline
         * \r carriage return �س�
         * \s space �ո�
         * \t tab
         * \e ASCII ESC ('\033')
         * \DDD 3λ�˽�����
         * \xDDD 3λʮ��������
         * \^C C���κ��ַ��� ����^A, ^B ��Ascii ���ж��ж�Ӧ�����⺬��
         * ASCII �ַ���μ���
         * http://baike.baidu.com/pic/%E7%BE%8E%E5%9B%BD%E4%BF%A1%E6%81%AF%E4%BA%A4%E6%8D%A2%E6%A0%87%E5%87%86%E4%BB%A3%E7%A0%81/8950990/0/9213b07eca8065387d4c671896dda144ad348213?fr=lemma&ct=single#aid=0&pic=9213b07eca8065387d4c671896dda144ad348213
         */

        int rval = 0;
        String exprToUpper = curExpr.toUpperCase();
        curPos++;                                   //Խ��ת�Ʒ� \
        switch (exprToUpper.charAt(curPos)) {
            case '\0' :
                rval = '\\';
                break;
            case 'B':
                rval = '\b';
                break;
            case 'F':
                rval = '\f';
                break;
            case 'N' :
                rval = '\n';
                break;
            case 'R' :
                rval = '\r';
                break;
            case 'S':
                rval = ' ';
                break;
            case 'T':
                rval = '\t';
                break;
            case 'E' :
                rval = '\033';
                break;
            case '^':
                curPos++;
                /*
                 * ��˵�����^�������һ����ĸʱ����ʾ������ǿ����ַ�
                 * ^@ ��ASCII ���е���ֵΪ0��^A Ϊ1, �ַ�@��ASCII ������ֵΪ80�� �ַ�A��ASCII������ֵΪ81
                 * 'A' - '@' ����1 �Ͷ�Ӧ ^A �� ASCII ���е�λ��
                 * ����ɲο�ע�͸����� ASCII ͼ
                 */
                rval = (char) (curExpr.charAt(curPos) - '@');
                break;
            case 'X':
                /*
                 * \X ��ʾ������ŵ������ַ���ʾ�˽��ƻ�ʮ��������
                 */
                curPos++; //Խ��X
                if (isHexDigit(curExpr.charAt(curPos))) {
                    rval = hex2Bin(curExpr.charAt(curPos));
                    curPos++;
                }

                if (isHexDigit(curExpr.charAt(curPos))) {
                    rval <<= 4;
                    rval |= hex2Bin(curExpr.charAt(curPos));
                    curPos++;
                }

                if (isHexDigit(curExpr.charAt(curPos))) {
                    rval <<= 4;
                    rval |= hex2Bin(curExpr.charAt(curPos));
                    curPos++;
                }
                curPos--;                   //�����ں����ײ���� curPos++ ���������� --
                break;

            default:
                if (!isOctDigit(curExpr.charAt(curPos))) {
                    rval = curExpr.charAt(curPos);
                }
                else {
                    curPos++;
                    rval = oct2Bin(curExpr.charAt(curPos));
                    curPos++;
                    if (isOctDigit(curExpr.charAt(curPos))) {
                        rval <<= 3;
                        rval |= oct2Bin(curExpr.charAt(curPos));
                        curPos++;
                    }

                    if (isOctDigit(curExpr.charAt(curPos))) {
                        rval <<= 3;
                        rval |= oct2Bin(curExpr.charAt(curPos));
                        curPos++;
                    }

                    curPos--;               //�����ں����ײ���� curPos++ ���������� --
                }
        }

        curPos++;
        return rval;
    }

    private int hex2Bin(char c) {
        /*
         * ��ʮ����������Ӧ���ַ�ת��Ϊ��Ӧ����ֵ������
         * A ת��Ϊ10�� Bת��Ϊ11
         * �ַ�c ��������ʮ�������ַ��� 0123456789ABCDEF
         */
        return (Character.isDigit(c) ? (c) - '0' : (Character.toUpperCase(c) - 'A' + 10)) & 0xf;
    }

    private int oct2Bin(char c) {
        /*
         * ���ַ�c ת��Ϊ��Ӧ�İ˽�����
         * �ַ�c �����ǺϷ��İ˽����ַ�: 01234567
         */
        return ((c) - '0') & 0x7;
    }

    private boolean isHexDigit(char c) {
        return (Character.isDigit(c)|| ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F'));
    }

    private boolean isOctDigit(char c) {
        return ('0' <= c && c <= '7');
    }

    public boolean MatchToken(Token t) {
        return curToken == t;
    }

    public int getCurChar() {
        return curChar;
    }

    public String getCurExpr() {
        return curExpr;
    }

    public int getExpressionCount() {
        return expressionCount;
    }
}
