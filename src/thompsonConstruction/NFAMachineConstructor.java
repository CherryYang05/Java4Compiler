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
     * 几种字符集匹配操作的集合
     * @param pairOut pairOut
     */
    public void term(NFAPair pairOut) throws Exception {
        boolean handled = constructNFAForSingleCharacter(pairOut);
        if (!handled) {
            handled = constructNFAForDot(pairOut);
        }
        if (!handled) {
            constructNFAForCharacterSet(pairOut);
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
        return true;
    }

    /**
     * 匹配字符集，可能有取反操作例如 [abcd] (取反的例如：[^0-9])
     *
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

    ///**
    // * 匹配没有取反操作的字符集，如：[^0-9]
    // *
    // * @param pairOut pairOut
    // * @return true
    // */
    //public boolean constructNfaForCharacterSet(NFAPair pairOut) throws Exception {
    //    if (!lexer.MatchToken(Lexer.Token.CCL_START)) {
    //        return false;
    //    }
    //    lexer.advance();
    //    //取反标记^
    //    boolean negative = false;
    //    if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
    //        negative = true;
    //    }
    //    NFA start = allocFirstAndEndNodeForNFA(pairOut)
    //    start.setEdge(NFA.CCL);
    //
    //    if (!lexer.MatchToken(Lexer.Token.CCL_END)) {
    //        doDash(start.inputSet);
    //    }
    //    if (!lexer.MatchToken(Lexer.Token.CCL_END)) {
    //        ErrorHandler.parseErr(ErrorHandler.Error.E_BADEXPR);
    //    }
    //    if (negative) {
    //        start.setComplement();
    //    }
    //    lexer.advance();
    //    return true;
    //}

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


}
