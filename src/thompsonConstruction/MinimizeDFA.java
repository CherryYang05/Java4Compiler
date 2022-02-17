package thompsonConstruction;

import inputSystem.Input;

import java.util.Iterator;
import java.util.List;

/**
 * @author Cherry
 * @date 2022/2/14
 * @time 16:27
 * @brief 最小化 DFA 实现
 */

public class MinimizeDFA {
    private DFAConstructor dfaConstructor = null;
    private DFAGroupManager dfaGroupManager = null;
    private Input input = null;
    private static final int ASCII_NUM = 128;

    //保存最小化前的 DFA 结点队列
    private List<DFA> dfaList = null;
    //最小化之前的跳转表
    private int[][] dfaTransferTable = null;
    //执行最小化之后的跳转表
    private int[][] minDFA = null;

    //是否产生了新的分组，若没有产生新的分组说明最小化完成
    private boolean addNewGroup = true;
    private DFAGroup newGroup = null;

    private static final int STATE_FAILURE = -1;

    /**
     * 构造器，初始化一些参数
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
     * 执行 DFA 最小化操作，算法实现的逻辑主体
     * @return 返回最小化之后的跳转表
     */
    public int[][] minimize() {
        /*
          首先将所有 DFA 结点分成两个分区，分别是接收状态结点和非接收状态结点，
          这两个顺序决定了起始结点的编号，先将非接收状态结点分区可以保证初始分区为 0
         */
        addNoAcceptingDFAToGroup();
        addAcceptingDFAToGroup();

        while (addNewGroup) {
            addNewGroup = false;
            doGroupSeparationOnNumber();
            doGroupSeparationOnCharacter();
        }

        //打印最小化后的 DFA
        printMiniDFA();
        //创建 DFA 转移表
        createMiniDFATransferTable();
        //打印最小化 DFA 的转移表
        printMiniDFATable();

        return minDFA;
    }

    /**
     * 根据输入为数字进行划分 DFA 分区，主要思想如下：《编译原理（龙书）原书第二版》P94-95
     * 1. 首先从 DFAGroupManager 中获取每一个 Group
     * 2. 将每一个 Group 的第一个结点作为该组的 “代表”，这些 “代表” 构成了最小化后的 DFA 结点
     * 3. 根据输入的值进行状态转换，若跳转后的结点和第一个 “代表” 结点跳转后不在同一个分区，
     *    于是生成新的分区，并将新的结点放入新的分区
     * 4. 重复 3 直到没有新的分区生成
     *
     * [注]:感觉有 bug，for 循环应该针对输入的宏进行循环，将输入的宏作为转移字符
     */
    private void doGroupSeparationOnNumber() {
        int dfaGroupManagerSize = dfaGroupManager.size();
        for (int i = 0; i < dfaGroupManagerSize; i++) {
            DFAGroup dfaGroup = dfaGroupManager.get(i);
            int dfaCount = 1;
            newGroup = null;
            //获得每个分区的 “代表结点”
            DFA first = dfaGroup.get(0);
            //依次获得其他 DFA 结点
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
     * 根据输入为字符（非数字）进行划分 DFA 分区
     */
    private void doGroupSeparationOnCharacter() {
        int dfaGroupManagerSize = dfaGroupManager.size();
        for (int i = 0; i < dfaGroupManagerSize; i++) {
            DFAGroup dfaGroup = dfaGroupManager.get(i);
            int dfaCount = 1;
            newGroup = null;
            //获得每个分区的 “代表结点”
            DFA first = dfaGroup.get(0);
            //依次获得其他 DFA 结点
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
     * 根据输入的字符进行 DFA 分区划分
     * @param dfaGroup DFAGroup
     * @param first 每个 Group 的 "标志" 结点
     * @param next 其他 DFA 结点
     * @param c 转移字符
     * @return boolean
     */
    private boolean doGroupSeparationOnInput(DFAGroup dfaGroup, DFA first, DFA next, char c) {
        int first_state = dfaTransferTable[first.stateNum][c];
        int next_state = dfaTransferTable[next.stateNum][c];
        /*
           如果两个状态不在同一个分区,则创建一个新的 DFAGroup
         */
        if (dfaGroupManager.getContainingGroup(first_state) != dfaGroupManager.getContainingGroup(next_state)) {
            if (newGroup == null) {
                newGroup = dfaGroupManager.createNewGroup();
                //设置该分区是否为可接收状态，暂时用“代表”结点的下一个结点可接收情况代替分区结点的可接收情况
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
     * 创建最小化 DFA 后的跳转表，把点与点的跳转关系转换为分区与分区的转移关系
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
                    //求得 from 和 to 所在的分区号，并填入最小化 DFA 的转换表
                    int from_num = dfaGroupManager.getContainingGroup(from).getGroupNum();
                    int end_num = dfaGroupManager.getContainingGroup(to).getGroupNum();
                    minDFA[from_num][c] = end_num;
                }
            }
        }
    }

    /**
     * 根据最小化的 DFA 解析输入的字符串
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
     * 打印最小化 DFA 后的跳转表
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
     * 打印最小化 DFA 的分区和各个 DFA 结点
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
     * 将非接收状态结点加入 DFAGroup，形成分区 0
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
     * 将接收状态结点加入 DFAGroup，形成分区 1
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
