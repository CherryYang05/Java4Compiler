/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:20
 * @brief ��ѧ���ʽ���﷨���� (�ݹ鷽ʽ )
 */


public class BasicParser {
    private Lexer lexer;
    private boolean isLegalStatement = true;

    public BasicParser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void statements() {
        /*
         * statements -> expression; | expression;statements
         */

        expression();

        if (lexer.match(Lexer.SEMI)) {
            /*
             * look ahead ��ȡ��һ���ַ��������һ���ַ����� EOI
             * �ǾͲ����ұ߽�������
             */
            lexer.advance();
        } else {
            /*
             * ����������ʽ���ԷֺŽ������Ǿ����﷨����
             */
            isLegalStatement = false;
            System.out.println("Line: " + lexer.yylineno + " Missing semicolon");
            return;
        }

        if (!lexer.match(Lexer.EOI)) {
            /*
             * �ֺź����ַ�����������
             */
            statements();
        }

        if (isLegalStatement) {
            System.out.println("The statement is legal");
        }
    }

    private void expression() {
        /*
         * expression -> term expression'
         */
        term();
        expr_prime(); //expression'
    }

    private void expr_prime() {
        /*
         * expression' -> PLUS term expression' | '��'
         */
        if (lexer.match(Lexer.PLUS)) {
            lexer.advance();
            term();
            expr_prime();
        } else if (lexer.match(Lexer.UNKNOWN_SYMBOL)) {
            isLegalStatement = false;
            System.out.println("Unknow symbol: " + lexer.yytext);
            return;
        } else {
            /*
             * "��" ���ǲ��ٽ�����ֱ�ӷ���
             */
            return;
        }
    }

    private void term() {
        /*
         * term -> factor term'
         */
        factor();
        term_prime(); //term'
    }

    private void term_prime() {
        /*
         * term' -> * factor term' | '��'
         */
        if (lexer.match(Lexer.TIMES)) {
            lexer.advance();
            factor();
            term_prime();
        } else {
            /*
             * ��������� * ��ͷ�� ��ôִ�� '��'
             * Ҳ���ǲ�������һ��������ֱ�ӷ���
             */
            return;
        }
    }

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

