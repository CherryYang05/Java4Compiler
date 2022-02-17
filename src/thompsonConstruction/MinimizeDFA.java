package thompsonConstruction;

import inputSystem.Input;

import java.util.Iterator;
import java.util.List;

/**
 * @author Cherry
 * @date 2022/2/14
 * @time 16:27
 * @brief ��С�� DFA ʵ��
 */

public class MinimizeDFA {
    private DFAConstructor dfaConstructor = null;
    private DFAGroupManager dfaGroupManager = null;
    private Input input = null;
    private static final int ASCII_NUM = 128;

    //������С��ǰ�� DFA ������
    private List<DFA> dfaList = null;
    //��С��֮ǰ����ת��
    private int[][] dfaTransferTable = null;
    //ִ����С��֮�����ת��
    private int[][] minDFA = null;

    //�Ƿ�������µķ��飬��û�в����µķ���˵����С�����
    private boolean addNewGroup = true;
    private DFAGroup newGroup = null;

    private static final int STATE_FAILURE = -1;

    /**
     * ����������ʼ��һЩ����
     * @param dfaConstructor dfaConstructor
     */
    public MinimizeDFA(DFAConstructor dfaConstructor, Input input) {
        this.dfaConstructor = dfaConstructor;
        dfaGroupManager = new DFAGroupManager();
        dfaList = dfaConstructor.getDFAList();
        dfaTransferTable = dfaConstructor.getDFAStateTransferTable();
        this.input = input;
    }

    /**
     * ִ�� DFA ��С���������㷨ʵ�ֵ��߼�����
     * @return ������С��֮�����ת��
     */
    public int[][] minimize() {
        /*
          ���Ƚ����� DFA ���ֳ������������ֱ��ǽ���״̬���ͷǽ���״̬��㣬
          ������˳���������ʼ���ı�ţ��Ƚ��ǽ���״̬���������Ա�֤��ʼ����Ϊ 0
         */
        addNoAcceptingDFAToGroup();
        addAcceptingDFAToGroup();

        while (addNewGroup) {
            addNewGroup = false;
            doGroupSeparationOnNumber();
            doGroupSeparationOnCharacter();
        }

        //��ӡ��С����� DFA
        printMiniDFA();
        //���� DFA ת�Ʊ�
        createMiniDFATransferTable();
        //��ӡ��С�� DFA ��ת�Ʊ�
        printMiniDFATable();

        return minDFA;
    }

    /**
     * ��������Ϊ���ֽ��л��� DFA ��������Ҫ˼�����£�������ԭ�����飩ԭ��ڶ��桷P94-95
     * 1. ���ȴ� DFAGroupManager �л�ȡÿһ�� Group
     * 2. ��ÿһ�� Group �ĵ�һ�������Ϊ����� ����������Щ ������ ��������С����� DFA ���
     * 3. ���������ֵ����״̬ת��������ת��Ľ��͵�һ�� ������ �����ת����ͬһ��������
     *    ���������µķ����������µĽ������µķ���
     * 4. �ظ� 3 ֱ��û���µķ�������
     *
     * [ע]:�о��� bug��for ѭ��Ӧ���������ĺ����ѭ����������ĺ���Ϊת���ַ�
     */
    private void doGroupSeparationOnNumber() {
        int dfaGroupManagerSize = dfaGroupManager.size();
        for (int i = 0; i < dfaGroupManagerSize; i++) {
            DFAGroup dfaGroup = dfaGroupManager.get(i);
            int dfaCount = 1;
            newGroup = null;
            //���ÿ�������� �������㡱
            DFA first = dfaGroup.get(0);
            //���λ������ DFA ���
            DFA next = dfaGroup.get(dfaCount);

            while (next != null) {
                for (char c = '0'; c <= '9'; c++) {
                    if (doGroupSeparationOnInput(dfaGroup, first, next, c)) {
                        addNewGroup = true;
                        break;
                    }
                }
                dfaCount++;
                next = dfaGroup.get(dfaCount);
            }
            dfaGroup.commitRemoved();
        }
    }


    /**
     * ��������Ϊ�ַ��������֣����л��� DFA ����
     */
    private void doGroupSeparationOnCharacter() {
        int dfaGroupManagerSize = dfaGroupManager.size();
        for (int i = 0; i < dfaGroupManagerSize; i++) {
            DFAGroup dfaGroup = dfaGroupManager.get(i);
            int dfaCount = 1;
            newGroup = null;
            //���ÿ�������� �������㡱
            DFA first = dfaGroup.get(0);
            //���λ������ DFA ���
            DFA next = dfaGroup.get(dfaCount);

            while (next != null) {
                for (char c = 0; c <= ASCII_NUM && (c < '0' || c > '9'); c++) {
                    if (doGroupSeparationOnInput(dfaGroup, first, next, c)) {
                        addNewGroup = true;
                        break;
                    }
                }
                dfaCount++;
                next = dfaGroup.get(dfaCount);
            }
            dfaGroup.commitRemoved();
        }
    }


    /**
     * ����������ַ����� DFA ��������
     * @param dfaGroup DFAGroup
     * @param first ÿ�� Group �� "��־" ���
     * @param next ���� DFA ���
     * @param c ת���ַ�
     * @return boolean
     */
    private boolean doGroupSeparationOnInput(DFAGroup dfaGroup, DFA first, DFA next, char c) {
        int first_state = dfaTransferTable[first.stateNum][c];
        int next_state = dfaTransferTable[next.stateNum][c];
        /*
           �������״̬����ͬһ������,�򴴽�һ���µ� DFAGroup
         */
        if (dfaGroupManager.getContainingGroup(first_state) != dfaGroupManager.getContainingGroup(next_state)) {
            if (newGroup == null) {
                newGroup = dfaGroupManager.createNewGroup();
                //���ø÷����Ƿ�Ϊ�ɽ���״̬����ʱ�á�����������һ�����ɽ����������������Ŀɽ������
                newGroup.setAccepted(next.isAccepted);
            }
            dfaGroup.setTobeRemoved(next);
            newGroup.addDFANode(next);
            System.out.println("DFA: " + first.stateNum + " and DFA: " +
                    next.stateNum + " jump to different group on input char: " + c);
            System.out.println("Remove DFA: " + next.stateNum + " from Group: " +
                    dfaGroup.getGroupNum() + " and add it to Group: " + newGroup.getGroupNum());
            System.out.println();
            return true;
        } else {
            return false;
        }
    }

    /**
     * ������С�� DFA �����ת���ѵ�������ת��ϵת��Ϊ�����������ת�ƹ�ϵ
     */
    private void createMiniDFATransferTable() {
        initMiniDfaTransTable();
        Iterator it = dfaList.iterator();
        while (it.hasNext()) {
            DFA dfa = (DFA)it.next();
            int from = dfa.stateNum;
            int to = 0;
            for (char c = 0; c <= ASCII_NUM; c++) {
                to = dfaTransferTable[from][c];
                if (to != STATE_FAILURE) {
                    //��� from �� to ���ڵķ����ţ���������С�� DFA ��ת����
                    int from_num = dfaGroupManager.getContainingGroup(from).getGroupNum();
                    int end_num = dfaGroupManager.getContainingGroup(to).getGroupNum();
                    minDFA[from_num][c] = end_num;
                }
            }
        }
    }

    /**
     * ������С���� DFA ����������ַ���
     */
    public void MinimizedDFAInterpreter() {
        input.ii_pushback();
        byte c;
        int state = 0;
        boolean isAccepted = false;
        StringBuilder stringBuilder = new StringBuilder();
        while ((c = input.ii_advance()) != input.EOF) {
            if (c != '\n'){
                state = minDFA[state][(char)c];
                stringBuilder.append((char)c);
            }
        }
        DFAGroup dfaGroup = dfaGroupManager.getContainingInMinimizedGroup(state);
        if (dfaGroup.isAccepted()) {
            System.out.println("\nThe Minimized DFA can recognize the String: " + stringBuilder.toString() + "!");
        } else {
            System.out.println("\nThe String " + stringBuilder.toString() + " can't be recognized...");
        }
    }

    /**
     * ��ӡ��С�� DFA �����ת��
     */
    private void printMiniDFATable() {
        System.out.println("\n========= DFA Transfer Table ==========");
        for (int i = 0; i < dfaGroupManager.size(); i++) {
            for (int j = 0; j < dfaGroupManager.size(); j++) {
                if (isOnNumberClass(i, j)) {
                    System.out.println("From " + i + " to " + j + " on D(digit)");
                }
                if (isOnDot(i, j)) {
                    System.out.println("From " + i + " to " + j + " on .(dot)");
                }
            }
        }
        System.out.println("======== DFA Transfer Table End =========");
    }


    /**
     * ��ӡ��С�� DFA �ķ����͸��� DFA ���
     */
    private void printMiniDFA() {
        System.out.println("============== Show Minimized DFA ============");
        for (int i = 0; i < dfaGroupManager.size(); i++) {
            DFAGroup dfaGroup = dfaGroupManager.get(i);
            dfaGroup.printGroup();
        }
        System.out.println("=========== Show Minimized DFA End ===========");
    }


    /**
     * ���ǽ���״̬������ DFAGroup���γɷ��� 0
     */
    private void addNoAcceptingDFAToGroup() {
        DFAGroup dfaGroup = dfaGroupManager.createNewGroup();
        dfaGroup.setAccepted(false);
        for (DFA dfa : dfaList) {
            if (!dfa.isAccepted) {
                dfaGroup.addDFANode(dfa);
            }
        }
    }

    /**
     * ������״̬������ DFAGroup���γɷ��� 1
     */
    private void addAcceptingDFAToGroup() {
        DFAGroup dfaGroup = dfaGroupManager.createNewGroup();
        dfaGroup.setAccepted(true);
        for (DFA dfa : dfaList) {
            if (dfa.isAccepted) {
                dfaGroup.addDFANode(dfa);
            }
        }
    }

    private void initMiniDfaTransTable() {
        minDFA = new int[dfaGroupManager.size()][ASCII_NUM];
        for (int i = 0; i < dfaGroupManager.size(); i++)
            for (int j = 0; j < ASCII_NUM; j++) {
                minDFA[i][j] = STATE_FAILURE;
            }
    }

    private boolean isOnNumberClass(int from, int to) {
        char c = '0';
        for (c = '0'; c <= '9'; c++) {
            if (minDFA[from][c] != to) {
                return false;
            }
        }

        return true;
    }

    private boolean isOnDot(int from, int to) {
        return minDFA[from]['.'] == to;
    }
}
