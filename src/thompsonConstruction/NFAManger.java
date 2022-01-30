package thompsonConstruction;

import java.util.Stack;

/**
 * @author Cherry
 * @date 2022/1/29
 * @time 13:23
 * @brief ���� NFA �ڵ�Ĺ���ͻ���
 */

public class NFAManger {
    private final int NFA_MAX = 256;    //������ͬʱ�� 256 �� NFA ���
    private NFA[] nfaStatesArray = null;
    private Stack<NFA> nfaStack = null; //ջ���������Ѿ�����ĵ���ʹ����ɵĽ��
    private int nextAlloc = 0;          //NFA �����±�
    private int nfaStatesNum = 0;       //����� NFA ���

    /**
     * �������ڳ�ʼ�� NFA �����ջ
     * @throws Exception Exception
     */
    public NFAManger() throws Exception {
        nfaStatesArray = new NFA[NFA_MAX];
        for (int i = 0; i < NFA_MAX; i++) {
            nfaStatesArray[i] = new NFA();
        }

        nfaStack = new Stack<>();
        if (nfaStatesArray == null || nfaStack == null) {
            ErrorHandler.parseErr(ErrorHandler.Error.E_MEM);
        }
    }


    /**
     * ����һ���µ� NFA ���
     * @return NFA
     * @throws Exception
     */
    public NFA newNFA() throws Exception {
        //NFA ������������
        if (++nfaStatesNum >= NFA_MAX) {
            ErrorHandler.parseErr(ErrorHandler.Error.E_LENGTH);
        }
        NFA nfa = null;
        if (!nfaStack.isEmpty()) {
            nfa = nfaStack.pop();
        } else {
            nfa = nfaStatesArray[nextAlloc++];
        }

        //nfa.clearState();
        nfa.setStateNum(nfaStatesNum);
        nfa.setEdge(NFA.EPSILON);
        return nfa;
    }

    public void discardNFA(NFA nfa) {
        --nfaStatesNum;
        //����ջ�е� NFA �����Ҫ���������
        nfa.clearState();
        nfaStack.push(nfa);
    }
}
