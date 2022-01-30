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
     * �����ַ���ƥ������ļ���
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
        return true;
    }

    /**
     * ƥ���ַ�����������ȡ���������� [abcd] (ȡ�������磺[^0-9])
     *
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

    ///**
    // * ƥ��û��ȡ���������ַ������磺[^0-9]
    // *
    // * @param pairOut pairOut
    // * @return true
    // */
    //public boolean constructNfaForCharacterSet(NFAPair pairOut) throws Exception {
    //    if (!lexer.MatchToken(Lexer.Token.CCL_START)) {
    //        return false;
    //    }
    //    lexer.advance();
    //    //ȡ�����^
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


}
