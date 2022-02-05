package thompsonConstruction;

import inputSystem.Input;

import java.util.Set;

/**
 * @author Cherry
 * @date 2022/1/29
 * @time 13:51
 * @brief 构造 NFA 结点，通过读入正则表达式来构造状态机
 */

public class NFAMachineConstructor {

    public Lexer lexer = null;
    public NFAManger nfaManger = null;


    NFAMachineConstructor(Lexer lexer) throws Exception {
        this.lexer = lexer;
        nfaManger = new NFAManger();
        //lexer 初始化时 curToken 为 EOS
        if (lexer.MatchToken(Lexer.Token.EOS)) {
            lexer.advance();
        }
    }


    /**
     * 单字符匹配
     *
     * @param pairOut 定义首尾两个 NFA 结点的类
     * @return true
     */
    public boolean constructNFAForSingleCharacter(NFAPair pairOut) throws Exception {
        if (!lexer.MatchToken(Lexer.Token.L)) {
            return false;
        }
        //为首尾 NFA 分配可用的结点
        NFA start = allocFirstAndEndNodeForNFA(pairOut);
        //设置边
        start.setEdge(lexer.getCurChar());
        lexer.advance();
        return true;
    }

    /**
     * 匹配任意单字符 "."
     *
     * @param pairOut pairOut
     * @return true
     */
    public boolean constructNFAForDot(NFAPair pairOut) throws Exception {
        if (!lexer.MatchToken(Lexer.Token.ANY)) {
            return false;
        }
        //为首尾 NFA 分配可用的结点
        NFA start = allocFirstAndEndNodeForNFA(pairOut);
        //设置边和字符集，并求补
        start.setEdge(NFA.CCL);
        //除了回车换行两个字符外，接收其他字符
        start.addToSet((byte) '\n');
        start.addToSet((byte) '\r');
        start.setComplement();
        lexer.advance();
        return true;
    }

    /**
     * 匹配字符集，可能有取反操作例如 [abcd] (取反的例如：[^0-9])
     * @return true
     */
    public boolean constructNFAForCharacterSet(NFAPair pairOut) throws Exception {
        //字符集不是以 '[' 开头则返回 false
        if (!lexer.MatchToken(Lexer.Token.CCL_START)) {
            return false;
        }
        lexer.advance();
        //取反标记^
        boolean negative = false;
        if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
            negative = true;
        }
        //分配首尾结点
        NFA start = allocFirstAndEndNodeForNFA(pairOut);
        start.setEdge(NFA.CCL);
        //如果第一个字符不是字符集结束符 ']'，那么进行 dash 处理(横杠)
        if (!lexer.MatchToken(Lexer.Token.CCL_END)) {
            doDash(start.inputSet);
        }
        //如果字符集内元素(包括横杠)处理完还没有遇到 ']'，则返回 [] 匹配错误
        if (!lexer.MatchToken(Lexer.Token.CCL_END)) {
            ErrorHandler.parseErr(ErrorHandler.Error.E_BADEXPR);
        }
        if (negative) {
            start.setComplement();
        }
        lexer.advance();
        return true;
    }

    /**
     * 几种字符集匹配操作的集合
     * @param pairOut pairOut
     */
    public void term(NFAPair pairOut) throws Exception {
        boolean handled = constructExprInParen(pairOut);
        if (!handled) {
            constructNFAForSingleCharacter(pairOut);
        }
        if (!handled) {
            handled = constructNFAForDot(pairOut);
        }
        if (!handled) {
            constructNFAForCharacterSet(pairOut);
        }
    }

    /**
     * 处理解析正则表达式 [] 中匹配的字符集(有 bug)
     */
    public void doDash(Set<Byte> set) {
        int pos = 0;
        while (!lexer.MatchToken(Lexer.Token.CCL_END) && !lexer.MatchToken(Lexer.Token.EOS)) {
            //如果没有遇到横杠-
            if (!lexer.MatchToken(Lexer.Token.DASH)) {
                pos = lexer.getCurChar();
                set.add((byte) pos);
            } else {
                lexer.advance();    //越过横杠
                for (; pos <= lexer.getCurChar(); pos++) {
                    set.add((byte) pos);
                }
            }
            lexer.advance();
        }
    }

    /**
     * 为 NFA 分配首尾结点
     *
     * @param pairOut pairOut
     * @return start 结点
     */
    public NFA allocFirstAndEndNodeForNFA(NFAPair pairOut) throws Exception {
        NFA start = nfaManger.newNFA();
        pairOut.startNode = start;
        pairOut.endNode = nfaManger.newNFA();
        start.next = pairOut.endNode;
        return start;
    }

    /**
     * 三种闭包操作的整合
     * @param pairOut NFAPair
     */
    public void factor(NFAPair pairOut) throws Exception {
        boolean handle = constructStarClosure(pairOut);
        if (!handle) {
            handle = constructPlusClosure(pairOut);
        }
        if (!handle) {
            handle = constructOptionsClosure(pairOut);
        }
    }

    /**
     * term* 星号闭包操作
     * @param pairOut NFAPair
     * @return boolean
     */
    public boolean constructStarClosure(NFAPair pairOut) throws Exception {
        //start, end 表示在简单 NFA 之外的新的首尾 NFA 结点，即通过 ε 连接的首尾 NFA 结点
        NFA start, end;
        term(pairOut);
        if (!lexer.MatchToken(Lexer.Token.CLOSURE)) {
            return false;
        }
        start = nfaManger.newNFA();
        end = nfaManger.newNFA();
        //进行指针连接，连成符合星号闭包的规则
        start.next = pairOut.startNode;
        start.next2 = end;
        pairOut.endNode.next = pairOut.startNode;
        pairOut.endNode.next2 = end;

        pairOut.startNode = start;
        pairOut.endNode = end;
        lexer.advance();
        return true;
    }

    /**
     * term+ 正闭包操作
     * @param pairOut NFAPair
     * @return boolean
     */
    public boolean constructPlusClosure(NFAPair pairOut) throws Exception {
        //start, end 表示在简单 NFA 之外的新的首尾 NFA 结点，即通过 ε 连接的首尾 NFA 结点
        NFA start, end;
        term(pairOut);
        if (!lexer.MatchToken(Lexer.Token.PLUS_CLOSE)) {
            return false;
        }
        start = nfaManger.newNFA();
        end = nfaManger.newNFA();
        //进行指针连接，连成符合星号闭包的规则
        start.next = pairOut.startNode;
        pairOut.endNode.next = pairOut.startNode;
        pairOut.endNode.next2 = end;

        pairOut.startNode = start;
        pairOut.endNode = end;
        lexer.advance();
        return true;
    }

    /**
     * term? 选择闭包操作
     * @param pairOut NFAPair
     * @return boolean
     */
    public boolean constructOptionsClosure(NFAPair pairOut) throws Exception {
        //start, end 表示在简单 NFA 之外的新的首尾 NFA 结点，即通过 ε 连接的首尾 NFA 结点
        NFA start, end;
        term(pairOut);
        if (!lexer.MatchToken(Lexer.Token.OPTIONAL)) {
            return false;
        }
        start = nfaManger.newNFA();
        end = nfaManger.newNFA();
        //进行指针连接，连成符合星号闭包的规则
        start.next = pairOut.startNode;
        pairOut.endNode.next = pairOut.startNode;
        pairOut.endNode.next2 = end;

        pairOut.startNode = start;
        pairOut.endNode = end;
        lexer.advance();
        return true;
    }

    /**
     * 正则表达式的 & (与) 操作
     * @param pairOut
     */
    public void cat_expr(NFAPair pairOut) throws Exception {
        /*
         * cat_expr -> factor factor .....
         * 由于多个factor 前后结合就是一个cat_expr所以
         * cat_expr-> factor cat_expr
         */
        if (first_in_cat(lexer.getCurToken())) {
            factor(pairOut);
        }
        while (first_in_cat(lexer.getCurToken())) {
            NFAPair newPair = new NFAPair();
            factor(newPair);
            pairOut.endNode.next = newPair.startNode;
            pairOut.endNode = newPair.endNode;
        }
    }

    /**
     * 判断表达式开头字符的合法性
     * @param token token
     * @return bool
     * @throws Exception Exception
     */
    private boolean first_in_cat(Lexer.Token token) throws Exception {
        switch (token) {
            //正确的表达式不会以 ) $ 开头,如果遇到 EOS 表示正则表达式解析完毕，那么就不应该执行该函数
            case CLOSE_PAREN:
            case AT_EOL:
            case OR:
            case EOS:
                return false;
            case CLOSURE:
            case PLUS_CLOSE:
            case OPTIONAL:
                //*, +, ? 这几个符号应该放在表达式的末尾
                ErrorHandler.parseErr(ErrorHandler.Error.E_CLOSE);
                return false;
            case CCL_END:
                //表达式不应该以 ] 开头
                ErrorHandler.parseErr(ErrorHandler.Error.E_BRACKET);
                return false;
            case AT_BOL:
                // ^ 必须在表达式的最开始
                ErrorHandler.parseErr(ErrorHandler.Error.E_BOL);
                return false;
        }
        return true;
    }

    /**
     * 实现正则表达式的或 (|) 操作
     * @param pairOut pairOut
     */
    public void expr(NFAPair pairOut) throws Exception {
        /*
         * expr 由一个或多个 cat_expr 之间进行 OR 形成
         * 如果表达式只有一个 cat_expr 那么expr 就等价于cat_expr
         * 如果表达式由多个 cat_expr 做或连接构成那么 expr-> cat_expr | cat_expr | ....
         * 由此得到 expr 的语法描述为:
         * expr -> expr OR cat_expr
         *         | cat_expr
         *
         */
        cat_expr(pairOut);
        while (lexer.MatchToken(Lexer.Token.OR)) {
            lexer.advance();
            NFAPair newPair = new NFAPair();
            cat_expr(newPair);

            NFA startNode = nfaManger.newNFA();
            startNode.next = pairOut.startNode;
            startNode.next2 = newPair.startNode;
            pairOut.startNode = startNode;

            NFA endNode = nfaManger.newNFA();
            pairOut.endNode.next = endNode;
            newPair.endNode.next = endNode;
            pairOut.endNode = endNode;
        }
    }

    /**
     * 处理遇到圆括号的问题，也顺便解决了输入正则表达式为宏定义的问题
     * @param pairOut pairOut
     * @return bool
     * @throws Exception Exception
     */
    private boolean constructExprInParen(NFAPair pairOut) throws Exception {
        if (lexer.MatchToken(Lexer.Token.OPEN_PAREN)) {
            lexer.advance();
            expr(pairOut);
            if (lexer.MatchToken(Lexer.Token.CLOSE_PAREN)) {
                lexer.advance();
            } else {
                ErrorHandler.parseErr(ErrorHandler.Error.E_PAREN);
            }
            return true;
        }
        return false;
    }
}
