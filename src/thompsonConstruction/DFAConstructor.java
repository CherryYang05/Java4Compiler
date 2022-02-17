package thompsonConstruction;

import java.util.*;

/**
 * @author Cherry
 * @date 2022/2/13
 * @time 18:24
 * @brief 构造 DFA 结构
 */

public class DFAConstructor {

    private NFAPair nfaMachine = null;
    private NFAInterpreter nfaInterpreter = null;           //为了在 DFA 中完成 ε 和 move 闭包运算
    private ArrayList<DFA> dfaList = new ArrayList<>();     //保存当前已经生成的 DFA 结点

    //假设 DFA 结点数量不超过 255 个
    private static final int MAX_DFA_STATE_COUNT = 255;
    // ASCII 可打印字符不超过 128 个
    private static final int ASCII_COUNT = 128;
    private static final int STATE_FAILURE = -1;

    //用二维数组表示确定的有限状态机的状态
    private int[][] dfaStateTransferTable = new int[MAX_DFA_STATE_COUNT][ASCII_COUNT + 1];

    /**
     * 构造器，
     */
    public DFAConstructor(NFAPair nfaPair, NFAInterpreter nfaInterpreter) {
        //获取已经生成的 NFA 状态机
        this.nfaMachine = nfaPair;
        this.nfaInterpreter = nfaInterpreter;
        initTransformTable();
    }

    /**
     * 初始化转移数组 -1
     */
    private void initTransformTable() {
        for (int i = 0; i < MAX_DFA_STATE_COUNT; i++)
            for (int j = 0; j <= ASCII_COUNT; j++) {
                dfaStateTransferTable[i][j] = STATE_FAILURE;
            }
    }

    /**
     * 将 NFA 结点集合转化成 DFA 结点
     * 我们注意到：保存 NFA 结点之间的关系是用指针，而保存 DFA 之间的关系是用转移数组，这是为什么呢？
     * 主要是因为 NFA 结点最多有两个出边，且出边要么是两个或一个 ε 边，要么只有一个有效字符边，很容易表示，
     * 然而 DFA 每个结点的出边没有限制，可以有很多个，用指针表示的话会比较麻烦
     * @return 转换数组
     */
    public int[][] convertNFAToDFA() {
        //先创建第一个 DFA 结点
        Set<NFA> input = new HashSet<>();
        input.add(nfaMachine.startNode);
        Set<NFA> nfaStartClosure = nfaInterpreter.e_closure(input);
        DFA start = DFA.getDFAFromNFASet(nfaStartClosure);
        dfaList.add(start);
        System.out.println("Create DFA start node!");
        printDFA(start);

        int nextState = STATE_FAILURE;
        int currentDFAIndex = 0;
        //循环判断条件：
        while (currentDFAIndex < dfaList.size()) {
            DFA currentDFA = dfaList.get(currentDFAIndex);
            System.out.println("--------------- DFA NUM " + currentDFA.stateNum +
                    " ---------------");

            //一个 DFA 可以有很多条出边
            for (char c = 0; c <= ASCII_COUNT; c++) {
                Set<NFA> move = nfaInterpreter.move(currentDFA.nfaStates, (byte)c);
                if (!move.isEmpty()) {
                    //求出 move 操作之后的 ε 闭包
                    Set<NFA> closure = nfaInterpreter.e_closure(move);
                    DFA dfa = isNFAStatesExistInDFA(closure);

                    //如果 dfa 为空，说明不存在与 NFA 结点集合相同的 DFA 结点，需要创建新的 DFA 结点
                    if (dfa == null) {
                        System.out.println("Create a new DFA node!");
                        dfa = DFA.getDFAFromNFASet(closure);
                        dfaList.add(dfa);
                        printDFA(dfa);
                        nextState = dfa.stateNum;
                    } else {
                        //反之表示已经存在与 NFA 表示相同状态的 DFA 结点
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
     * 打印所有 DFA 结点
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
     * 打印给定的 DFA 结点
     * @param dfa 给定的 DFA 结点
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
     * 判断当前 NFA 结点集合是否已经存在对应的 DFA 结点
     * @param inputSet 当前输入的 NFA 结点集合
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
