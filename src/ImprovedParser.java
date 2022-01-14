import java.sql.SQLOutput;

/**
 * @author Cherry
 * @date 2022/1/14
 * @time 22:56
 * @brief 改进的简单数学表达式的语法分析（循环方式）
 * [将递归方式改为循环方式 ]
 */

public class ImprovedParser {
    private Lexer lexer;
    private boolean isLegalStatement = true;

    public ImprovedParser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void statements() {
        /*
         * statements -> expression; | expression;statements
         */
        //如果没到末尾，则继续读取
        while (!lexer.match(Lexer.EOI)) {
            expression();
            //如果读到分号，继续读后一个字符(可能为空，可能为statements)
            if (lexer.match(Lexer.SEMI)) {
                lexer.advance();
            } else {        //如果 expression 后面不是分号，说明语法错误，缺少分号
                isLegalStatement = false;
                System.out.println("Line: " + lexer.yylineno + " Missing semicolon");
            }
        }
        //expression();
        //
        //if (lexer.match(Lexer.SEMI)) {
        //    /*
        //     * look ahead 读取下一个字符，如果下一个字符不是 EOI
        //     * 那就采用右边解析规则
        //     */
        //    lexer.advance();
        //} else {
        //    /*
        //     * 如果算术表达式不以分号结束，那就是语法错误
        //     */
        //    isLegalStatement = false;
        //    System.out.println("Line: " + lexer.yylineno + " Missing semicolon");
        //    return;
        //}
        //
        //if (!lexer.match(Lexer.EOI)) {
        //    /*
        //     * 分号后还有字符，继续解析
        //     */
        //    statements();
        //}

        if (isLegalStatement) {
            System.out.println("The statement is legal");
        }
    }

    private void expression() {
        /*
         * expression -> term expression'
         */
        term();
        //expr_prime(); //expression'
        //如果 每个expression都以+开始，则循环读取
        while (lexer.match(Lexer.PLUS)) {
            lexer.advance();
            term();
        }

        if (lexer.match(Lexer.UNKNOWN_SYMBOL)) {
            isLegalStatement = false;
            System.out.println("Unknown symbol: " + lexer.yytext);
            return;
        } else {
            //空，直接返回
            return;
        }
    }

    /**
     * expr_prime的递归调用可以整合到 expression里
     * 由于当 lexer.match(Lexer.PLUS)判断成立时，expr_prime()有递归调用
     */
    //private void expr_prime() {
    //    /*
    //     * expression' -> PLUS term expression' | '空'
    //     */
    //    if (lexer.match(Lexer.PLUS)) {
    //        lexer.advance();
    //        term();
    //        expr_prime();
    //    } else if (lexer.match(Lexer.UNKNOWN_SYMBOL)) {
    //        isLegalStatement = false;
    //        System.out.println("Unknow symbol: " + lexer.yytext);
    //        return;
    //    } else {
    //        /*
    //         * "空" 就是不再解析，直接返回
    //         */
    //        return;
    //    }
    //}

    private void term() {
        /*
         * term -> factor term'
         */
        factor();
        //term_prime(); //term'
        while (lexer.match(Lexer.TIMES)) {
            lexer.advance();
            factor();
        }
    }

    //private void term_prime() {
    //    /*
    //     * term' -> * factor term' | '空'
    //     */
    //    if (lexer.match(Lexer.TIMES)) {
    //        lexer.advance();
    //        factor();
    //        term_prime();
    //    } else {
    //        /*
    //         * 如果不是以 * 开头， 那么执行 '空'
    //         * 也就是不再做进一步解析，直接返回
    //         */
    //        return;
    //    }
    //}

    private void factor() {
        /*
         * factor -> NUM_OR_ID | LP expression RP
         */

        if (lexer.match(Lexer.NUM_OR_ID)) {
            lexer.advance();
        } else if (lexer.match(Lexer.LP)) {
            lexer.advance();
            expression();
            if (lexer.match(Lexer.RP)) {
                lexer.advance();
            } else {
                /*
                 * 有左括号但没有右括号，错误
                 */
                isLegalStatement = false;
                System.out.println("Line: " + lexer.yylineno + " Missing )");
                return;
            }
        } else {
            /*
             * 这里不是数字，解析出错
             */
            isLegalStatement = false;
            System.out.println("Illegal statements");
            return;
        }
    }
}