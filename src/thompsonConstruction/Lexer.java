package thompsonConstruction;

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
        OPTIONAL,       //?
        OR,             // |
        PLUS_CLOSE
    }

    private final int ASCII_COUNT = 128;
    private Token[] tokenMap = new Token[ASCII_COUNT];
    private RegularExpressionHandler regularExpressionHandler = null;

    public Lexer(RegularExpressionHandler regularExpressionHandler) {
        initTokenMap();
        this.regularExpressionHandler = regularExpressionHandler;
    }

    /**
     * �� Token �ĺ���������г�ʼ��
     */
    private void initTokenMap() {

    }
}
