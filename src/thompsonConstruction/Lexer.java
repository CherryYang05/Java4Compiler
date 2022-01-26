package thompsonConstruction;

/**
 * @author Cherry
 * @date 2022/1/25
 * @time 23:11
 * @brief 用于解析后的正则表达式的词法分析
 */

public class Lexer {
    public enum Token {
        EOS,            //正则表达式末尾
        ANY,            // . 通配符
        AT_BOL,         //^ 开头匹配符
        AT_EOL,         //$ 末尾匹配符
        CCL_END,        //字符集类结尾括号 ]
        CCL_START,      //字符集类开始括号 [
        CLOSE_CURLY,    // }
        CLOSE_PAREN,    //)
        CLOSURE,        //*
        DASH,           // -
        END_OF_INPUT,   //输入流结束
        L,              //字符常量
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
     * 对 Token 的含义数组进行初始化
     */
    private void initTokenMap() {

    }
}
