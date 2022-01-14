import java.sql.SQLOutput;

/**
 * @author Cherry
 * @date 2022/1/14
 * @time 22:56
 * @brief �Ľ��ļ���ѧ���ʽ���﷨������ѭ����ʽ��
 * [���ݹ鷽ʽ��Ϊѭ����ʽ ]
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
        //���û��ĩβ���������ȡ
        while (!lexer.match(Lexer.EOI)) {
            expression();
            //��������ֺţ���������һ���ַ�(����Ϊ�գ�����Ϊstatements)
            if (lexer.match(Lexer.SEMI)) {
                lexer.advance();
            } else {        //��� expression ���治�Ƿֺţ�˵���﷨����ȱ�ٷֺ�
                isLegalStatement = false;
                System.out.println("Line: " + lexer.yylineno + " Missing semicolon");
            }
        }
        //expression();
        //
        //if (lexer.match(Lexer.SEMI)) {
        //    /*
        //     * look ahead ��ȡ��һ���ַ��������һ���ַ����� EOI
        //     * �ǾͲ����ұ߽�������
        //     */
        //    lexer.advance();
        //} else {
        //    /*
        //     * ����������ʽ���ԷֺŽ������Ǿ����﷨����
        //     */
        //    isLegalStatement = false;
        //    System.out.println("Line: " + lexer.yylineno + " Missing semicolon");
        //    return;
        //}
        //
        //if (!lexer.match(Lexer.EOI)) {
        //    /*
        //     * �ֺź����ַ�����������
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
        //��� ÿ��expression����+��ʼ����ѭ����ȡ
        while (lexer.match(Lexer.PLUS)) {
            lexer.advance();
            term();
        }

        if (lexer.match(Lexer.UNKNOWN_SYMBOL)) {
            isLegalStatement = false;
            System.out.println("Unknown symbol: " + lexer.yytext);
            return;
        } else {
            //�գ�ֱ�ӷ���
            return;
        }
    }

    /**
     * expr_prime�ĵݹ���ÿ������ϵ� expression��
     * ���ڵ� lexer.match(Lexer.PLUS)�жϳ���ʱ��expr_prime()�еݹ����
     */
    //private void expr_prime() {
    //    /*
    //     * expression' -> PLUS term expression' | '��'
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
    //         * "��" ���ǲ��ٽ�����ֱ�ӷ���
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
    //     * term' -> * factor term' | '��'
    //     */
    //    if (lexer.match(Lexer.TIMES)) {
    //        lexer.advance();
    //        factor();
    //        term_prime();
    //    } else {
    //        /*
    //         * ��������� * ��ͷ�� ��ôִ�� '��'
    //         * Ҳ���ǲ�������һ��������ֱ�ӷ���
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
                 * �������ŵ�û�������ţ�����
                 */
                isLegalStatement = false;
                System.out.println("Line: " + lexer.yylineno + " Missing )");
                return;
            }
        } else {
            /*
             * ���ﲻ�����֣���������
             */
            isLegalStatement = false;
            System.out.println("Illegal statements");
            return;
        }
    }
}