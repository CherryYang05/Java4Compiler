package thompsonConstruction;

import java.util.Stack;

/**
 * @author Cherry
 * @date 2022/1/29
 * @time 13:23
 * @brief 负责 NFA 节点的构造和回收
 */

public class NFAManger {
    private final int NFA_MAX = 256;    //最多可以同时有 256 个 NFA 结点
    private NFA[] nfaStatesArray = null;
    private Stack<NFA> nfaStack = null; //栈用来回收已经分配的但是使用完成的结点
    private int nextAlloc = 0;          //NFA 数组下标
    private int nfaStatesNum = 0;       //分配的 NFA 编号

    /**
     * 构造器内初始化 NFA 数组和栈
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
     * 分配一个新的 NFA 结点
     * @return NFA
     * @throws Exception
     */
    public NFA newNFA() throws Exception {
        //NFA 数量超过限制
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
        //放入栈中的 NFA 结点需要清除脏数据
        nfa.clearState();
        nfaStack.push(nfa);
    }
}
