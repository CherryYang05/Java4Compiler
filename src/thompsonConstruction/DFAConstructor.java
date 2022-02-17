package thompsonConstruction;

import java.util.*;

/**
 * @author Cherry
 * @date 2022/2/13
 * @time 18:24
 * @brief ���� DFA �ṹ
 */

public class DFAConstructor {

    private NFAPair nfaMachine = null;
    private NFAInterpreter nfaInterpreter = null;           //Ϊ���� DFA ����� �� �� move �հ�����
    private ArrayList<DFA> dfaList = new ArrayList<>();     //���浱ǰ�Ѿ����ɵ� DFA ���

    //���� DFA ������������� 255 ��
    private static final int MAX_DFA_STATE_COUNT = 255;
    // ASCII �ɴ�ӡ�ַ������� 128 ��
    private static final int ASCII_COUNT = 128;
    private static final int STATE_FAILURE = -1;

    //�ö�ά�����ʾȷ��������״̬����״̬
    private int[][] dfaStateTransferTable = new int[MAX_DFA_STATE_COUNT][ASCII_COUNT + 1];

    /**
     * ��������
     */
    public DFAConstructor(NFAPair nfaPair, NFAInterpreter nfaInterpreter) {
        //��ȡ�Ѿ����ɵ� NFA ״̬��
        this.nfaMachine = nfaPair;
        this.nfaInterpreter = nfaInterpreter;
        initTransformTable();
    }

    /**
     * ��ʼ��ת������ -1
     */
    private void initTransformTable() {
        for (int i = 0; i < MAX_DFA_STATE_COUNT; i++)
            for (int j = 0; j <= ASCII_COUNT; j++) {
                dfaStateTransferTable[i][j] = STATE_FAILURE;
            }
    }

    /**
     * �� NFA ��㼯��ת���� DFA ���
     * ����ע�⵽������ NFA ���֮��Ĺ�ϵ����ָ�룬������ DFA ֮��Ĺ�ϵ����ת�����飬����Ϊʲô�أ�
     * ��Ҫ����Ϊ NFA ���������������ߣ��ҳ���Ҫô��������һ�� �� �ߣ�Ҫôֻ��һ����Ч�ַ��ߣ������ױ�ʾ��
     * Ȼ�� DFA ÿ�����ĳ���û�����ƣ������кܶ������ָ���ʾ�Ļ���Ƚ��鷳
     * @return ת������
     */
    public int[][] convertNFAToDFA() {
        //�ȴ�����һ�� DFA ���
        Set<NFA> input = new HashSet<>();
        input.add(nfaMachine.startNode);
        Set<NFA> nfaStartClosure = nfaInterpreter.e_closure(input);
        DFA start = DFA.getDFAFromNFASet(nfaStartClosure);
        dfaList.add(start);
        System.out.println("Create DFA start node!");
        printDFA(start);

        int nextState = STATE_FAILURE;
        int currentDFAIndex = 0;
        //ѭ���ж�������
        while (currentDFAIndex < dfaList.size()) {
            DFA currentDFA = dfaList.get(currentDFAIndex);
            System.out.println("--------------- DFA NUM " + currentDFA.stateNum +
                    " ---------------");

            //һ�� DFA �����кܶ�������
            for (char c = 0; c <= ASCII_COUNT; c++) {
                Set<NFA> move = nfaInterpreter.move(currentDFA.nfaStates, (byte)c);
                if (!move.isEmpty()) {
                    //��� move ����֮��� �� �հ�
                    Set<NFA> closure = nfaInterpreter.e_closure(move);
                    DFA dfa = isNFAStatesExistInDFA(closure);

                    //��� dfa Ϊ�գ�˵���������� NFA ��㼯����ͬ�� DFA ��㣬��Ҫ�����µ� DFA ���
                    if (dfa == null) {
                        System.out.println("Create a new DFA node!");
                        dfa = DFA.getDFAFromNFASet(closure);
                        dfaList.add(dfa);
                        printDFA(dfa);
                        nextState = dfa.stateNum;
                    } else {
                        //��֮��ʾ�Ѿ������� NFA ��ʾ��ͬ״̬�� DFA ���
                        System.out.println("Get a existed DFA node...");
                        printDFA(dfa);
                        nextState = dfa.stateNum;
                    }

                } else {
                    nextState = STATE_FAILURE;
                }
                if (nextState != STATE_FAILURE) {
                    System.out.println("DFA from state " + currentDFA.stateNum + " to state " +
                            nextState + " on char: " + c);
                    System.out.println();
                }
                dfaStateTransferTable[currentDFA.stateNum][c] = nextState;
            } // for end
            currentDFAIndex++;

        }   //while end
        return dfaStateTransferTable;
    }

    /**
     * ��ӡ���� DFA ���
     */
    public void printDFA() {
        System.out.println("============= Show all DFA =============");
        int dfaNum = dfaList.size();
        for (int i = 0; i < dfaNum; i++) {
            for (int j = 0; j < dfaNum; j++) {
                if (isOnNumberClass(i, j)) {
                    System.out.println("From state " + i + " to state " + j + " on D(digit)");
                }
                if (isOnDot(i, j)) {
                    System.out.println("From state " + i + " to state " + j + " on dot(.)");
                }
            }
        }
        System.out.println("=========== Show all DFA End ===========");
    }

    /**
     * ��ӡ������ DFA ���
     * @param dfa ������ DFA ���
     */
    private void printDFA(DFA dfa) {
        System.out.print("## Dfa state: " + dfa.stateNum + ". Its NFA states are: {");
        Iterator<NFA> it = dfa.nfaStates.iterator();
        while (it.hasNext()) {
            System.out.print(it.next().getStateNum());
            if (it.hasNext()) {
                System.out.print(", ");
            }
        }
        System.out.print("}\n");
    }

    /**
     * �жϵ�ǰ NFA ��㼯���Ƿ��Ѿ����ڶ�Ӧ�� DFA ���
     * @param inputSet ��ǰ����� NFA ��㼯��
     * @return DFA
     */
    private DFA isNFAStatesExistInDFA(Set<NFA> inputSet) {
        for (DFA dfa : dfaList) {
            if (dfa.isSameNFAStates(inputSet)) {
                return dfa;
            }
        }
        return null;
    }

    /**
     *
     * @param from start
     * @param to end
     * @return boolean
     */
    private boolean isOnNumberClass(int from, int to) {
        char c = '0';
        for (c = '0'; c <= '9'; c++) {
            if (dfaStateTransferTable[from][c] != to) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param from start
     * @param to end
     * @return boolean
     */
    private boolean isOnDot(int from, int to) {
        if (dfaStateTransferTable[from]['.'] != to) {
            return false;
        }

        return true;
    }

    public int[][] getDFAStateTransferTable() {
        return dfaStateTransferTable;
    }

    public List<DFA> getDFAList() {
        return dfaList;
    }
}
