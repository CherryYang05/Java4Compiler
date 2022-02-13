package thompsonConstruction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Cherry
 * @date 2022/2/13
 * @time 18:23
 * @brief 定义 DFA 结点信息
 */

public class DFA {
    private static int STATE_NUM = 0;       //标记 DFA 结点编号
    int stateNum = 0;                       //当前 DFA 结点的编号
    Set<NFA> nfaStates = new HashSet<>();   //记录 DFA 结点对应的 NFA 闭包集合
    boolean isAccepted = false;

    /**
     * 给出 NFA 结点，构造 DFA 结点，若 NFA 结点集合中包含终止结点，
     * 则该 DFA 结点标记为终止结点
     * 需要注意的是，用 Thompson 构造法通过正则表达式构造 NFA 时，
     * 起始和终止结点之有一个，因此判断 NFA 集合结点中是否包含终止结点时，
     * 可以判断该结点是否等于 pair.endNode, 或者判断该节点的两个指针是否都为空
     * @param input NFA 的结点集合
     * @return DFA 结点
     */
    public static DFA getDFAFromNFASet(Set<NFA> input) {
        DFA dfa = new DFA();
        Iterator it = input.iterator();
        while (it.hasNext()) {
            NFA tmpNFA = (NFA)it.next();
            dfa.nfaStates.add(tmpNFA);
            if (tmpNFA.next == null && tmpNFA.next2 == null) {
                dfa.isAccepted = true;
            }
        }
        dfa.stateNum = STATE_NUM;
        STATE_NUM++;
        return dfa;
    }

    /**
     * 判断该 DFA 结点对应的 NFA 结点集合是否相同
     * @param inputSet NFA 结点集合
     * @return boolean
     */
    boolean isSameNFAStates(Set<NFA> inputSet) {
        return inputSet.equals(nfaStates);
    }

}
