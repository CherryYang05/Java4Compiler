package thompsonConstruction;

import inputSystem.Input;

import java.util.Set;

/**
 * @author Cherry
 * @date 2022/1/29
 * @time 13:51
 * @brief ���� NFA ��㣬ͨ������������ʽ������״̬��
 */

public class NFAMachineConstructor {

    public Lexer lexer = null;
    public NFAManger nfaManger = null;


    NFAMachineConstructor(Lexer lexer) throws Exception {
        this.lexer = lexer;
        nfaManger = new NFAManger();
        //lexer ��ʼ��ʱ curToken Ϊ EOS
        if (lexer.MatchToken(Lexer.Token.EOS)) {
            lexer.advance();
        }
    }


    /**
     * ���ַ�ƥ��
     *
     * @param pairOut ������β���� NFA ������
     * @return true
     */
    public boolean constructNFAForSingleCharacter(NFAPair pairOut) throws Exception {
        if (!lexer.MatchToken(Lexer.Token.L)) {
            return false;
        }
        //Ϊ��β NFA ������õĽ��
        NFA start = allocFirstAndEndNodeForNFA(pairOut);
        //���ñ�
        start.setEdge(lexer.getCurChar());
        lexer.advance();
        return true;
    }

    /**
     * ƥ�����ⵥ�ַ� "."
     *
     * @param pairOut pairOut
     * @return true
     */
    public boolean constructNFAForDot(NFAPair pairOut) throws Exception {
        if (!lexer.MatchToken(Lexer.Token.ANY)) {
            return false;
        }
        //Ϊ��β NFA ������õĽ��
        NFA start = allocFirstAndEndNodeForNFA(pairOut);
        //���ñߺ��ַ���������
        start.setEdge(NFA.CCL);
        //���˻س����������ַ��⣬���������ַ�
        start.addToSet((byte) '\n');
        start.addToSet((byte) '\r');
        start.setComplement();
        lexer.advance();
        return true;
    }

    /**
     * ƥ���ַ�����������ȡ���������� [abcd] (ȡ�������磺[^0-9])
     * @return true
     */
    public boolean constructNFAForCharacterSet(NFAPair pairOut) throws Exception {
        //�ַ��������� '[' ��ͷ�򷵻� false
        if (!lexer.MatchToken(Lexer.Token.CCL_START)) {
            return false;
        }
        lexer.advance();
        //ȡ�����^
        boolean negative = false;
        if (lexer.MatchToken(Lexer.Token.AT_BOL)) {
            negative = true;
        }
        //������β���
        NFA start = allocFirstAndEndNodeForNFA(pairOut);
        start.setEdge(NFA.CCL);
        //�����һ���ַ������ַ��������� ']'����ô���� dash ����(���)
        if (!lexer.MatchToken(Lexer.Token.CCL_END)) {
            doDash(start.inputSet);
        }
        //����ַ�����Ԫ��(�������)�����껹û������ ']'���򷵻� [] ƥ�����
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
     * �����ַ���ƥ������ļ���
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
     * �������������ʽ [] ��ƥ����ַ���(�� bug)
     */
    public void doDash(Set<Byte> set) {
        int pos = 0;
        while (!lexer.MatchToken(Lexer.Token.CCL_END) && !lexer.MatchToken(Lexer.Token.EOS)) {
            //���û���������-
            if (!lexer.MatchToken(Lexer.Token.DASH)) {
                pos = lexer.getCurChar();
                set.add((byte) pos);
            } else {
                lexer.advance();    //Խ�����
                for (; pos <= lexer.getCurChar(); pos++) {
                    set.add((byte) pos);
                }
            }
            lexer.advance();
        }
    }

    /**
     * Ϊ NFA ������β���
     *
     * @param pairOut pairOut
     * @return start ���
     */
    public NFA allocFirstAndEndNodeForNFA(NFAPair pairOut) throws Exception {
        NFA start = nfaManger.newNFA();
        pairOut.startNode = start;
        pairOut.endNode = nfaManger.newNFA();
        start.next = pairOut.endNode;
        return start;
    }

    /**
     * ���ֱհ�����������
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
     * term* �Ǻűհ�����
     * @param pairOut NFAPair
     * @return boolean
     */
    public boolean constructStarClosure(NFAPair pairOut) throws Exception {
        //start, end ��ʾ�ڼ� NFA ֮����µ���β NFA ��㣬��ͨ�� �� ���ӵ���β NFA ���
        NFA start, end;
        term(pairOut);
        if (!lexer.MatchToken(Lexer.Token.CLOSURE)) {
            return false;
        }
        start = nfaManger.newNFA();
        end = nfaManger.newNFA();
        //����ָ�����ӣ����ɷ����Ǻűհ��Ĺ���
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
     * term+ ���հ�����
     * @param pairOut NFAPair
     * @return boolean
     */
    public boolean constructPlusClosure(NFAPair pairOut) throws Exception {
        //start, end ��ʾ�ڼ� NFA ֮����µ���β NFA ��㣬��ͨ�� �� ���ӵ���β NFA ���
        NFA start, end;
        term(pairOut);
        if (!lexer.MatchToken(Lexer.Token.PLUS_CLOSE)) {
            return false;
        }
        start = nfaManger.newNFA();
        end = nfaManger.newNFA();
        //����ָ�����ӣ����ɷ����Ǻűհ��Ĺ���
        start.next = pairOut.startNode;
        pairOut.endNode.next = pairOut.startNode;
        pairOut.endNode.next2 = end;

        pairOut.startNode = start;
        pairOut.endNode = end;
        lexer.advance();
        return true;
    }

    /**
     * term? ѡ��հ�����
     * @param pairOut NFAPair
     * @return boolean
     */
    public boolean constructOptionsClosure(NFAPair pairOut) throws Exception {
        //start, end ��ʾ�ڼ� NFA ֮����µ���β NFA ��㣬��ͨ�� �� ���ӵ���β NFA ���
        NFA start, end;
        term(pairOut);
        if (!lexer.MatchToken(Lexer.Token.OPTIONAL)) {
            return false;
        }
        start = nfaManger.newNFA();
        end = nfaManger.newNFA();
        //����ָ�����ӣ����ɷ����Ǻűհ��Ĺ���
        start.next = pairOut.startNode;
        pairOut.endNode.next = pairOut.startNode;
        pairOut.endNode.next2 = end;

        pairOut.startNode = start;
        pairOut.endNode = end;
        lexer.advance();
        return true;
    }

    /**
     * ������ʽ�� & (��) ����
     * @param pairOut
     */
    public void cat_expr(NFAPair pairOut) throws Exception {
        /*
         * cat_expr -> factor factor .....
         * ���ڶ��factor ǰ���Ͼ���һ��cat_expr����
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
     * �жϱ��ʽ��ͷ�ַ��ĺϷ���
     * @param token token
     * @return bool
     * @throws Exception Exception
     */
    private boolean first_in_cat(Lexer.Token token) throws Exception {
        switch (token) {
            //��ȷ�ı��ʽ������ ) $ ��ͷ,������� EOS ��ʾ������ʽ������ϣ���ô�Ͳ�Ӧ��ִ�иú���
            case CLOSE_PAREN:
            case AT_EOL:
            case OR:
            case EOS:
                return false;
            case CLOSURE:
            case PLUS_CLOSE:
            case OPTIONAL:
                //*, +, ? �⼸������Ӧ�÷��ڱ��ʽ��ĩβ
                ErrorHandler.parseErr(ErrorHandler.Error.E_CLOSE);
                return false;
            case CCL_END:
                //���ʽ��Ӧ���� ] ��ͷ
                ErrorHandler.parseErr(ErrorHandler.Error.E_BRACKET);
                return false;
            case AT_BOL:
                // ^ �����ڱ��ʽ���ʼ
                ErrorHandler.parseErr(ErrorHandler.Error.E_BOL);
                return false;
        }
        return true;
    }

    /**
     * ʵ��������ʽ�Ļ� (|) ����
     * @param pairOut pairOut
     */
    public void expr(NFAPair pairOut) throws Exception {
        /*
         * expr ��һ������ cat_expr ֮����� OR �γ�
         * ������ʽֻ��һ�� cat_expr ��ôexpr �͵ȼ���cat_expr
         * ������ʽ�ɶ�� cat_expr �������ӹ�����ô expr-> cat_expr | cat_expr | ....
         * �ɴ˵õ� expr ���﷨����Ϊ:
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
     * ��������Բ���ŵ����⣬Ҳ˳����������������ʽΪ�궨�������
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
