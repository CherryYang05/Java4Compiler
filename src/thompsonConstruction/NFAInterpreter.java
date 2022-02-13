package thompsonConstruction;

import inputSystem.Input;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Cherry
 * @date 2022/2/10
 * @time 12:23
 * @brief
 */

public class NFAInterpreter {

    private NFAPair nfaPair;
    private NFA start;
    private Input input;

    NFAInterpreter(NFAPair pair, Input input) {
        this.nfaPair = pair;
        this.start = pair.startNode;
        this.input = input;
    }

    /**
     * 解析输入的字符串
     */
    public void interpret() {
        Set<NFA> next = new HashSet<>();
        next.add(start);
        e_closure(next);
        Set<NFA> current = null;
        byte c;
        StringBuilder inputString = new StringBuilder();
        boolean isAccepted = false;
        while ((c = input.ii_advance()) != input.EOF) {
            if (c == '\n') continue;
            current = move(next, c);
            next = e_closure(current);
            isAccepted = isAcceptState(next);
            inputString.append((char)c);
        }
        if (isAccepted) {
            System.out.println("The NFA Machine can recognize string: " + inputString);
        }
    }

    /**
     * ε闭包 操作，可以使用 DFS 搜索（NFA确保不会有回路）
     * @param inputSet NFA 集合
     * @return 进行 ε 闭包操作后的集合
     */
    Set<NFA> e_closure(Set<NFA> inputSet) {
        /*
         * 计算input集合中nfa节点所对应的ε闭包，
         * 并将闭包的节点加入到input中
         */
        Set<NFA> newSet = new HashSet<>(inputSet);
        System.out.print("ε-Closure({" + strFromNFASet(inputSet) + "}) = ");
        inputSet.clear();
        for (NFA nfa : newSet) {
            DFS_e_closure(nfa, inputSet);
        }
        inputSet.addAll(newSet);
        System.out.println("{" + strFromNFASet(inputSet) + "}");
        return inputSet;
    }

    /**
     * move 操作，在 NFA 中对于 move 操作，只要跳转字符不为 ε，则可以认为：
     * 1. 该 NFA 结点只有一个出度，且关联到 next 结点（next2结点为空）
     * 2. 不存在循环，即根据跳转字符跳转到的新结点的边一定是 ε
     * 以上 NFA 结点特性可以使得 move 操作无需使用 DFS 搜索，相反 ε 闭包操作是需要进行搜索的
     *
     * @param input NFA 集合
     * @param c 跳转字符
     * @return 进行 move 操作后的集合
     */
    Set<NFA> move(Set<NFA> input, byte c) {
        Iterator it = input.iterator();
        Set<NFA> outSet = new HashSet<>();
        while (it.hasNext()) {
            NFA nfaNode = (NFA) it.next();
            if (nfaNode.getEdge() == c || nfaNode.inputSet.contains(c)) {
                outSet.add(nfaNode.next);
            }
        }
        if (!outSet.isEmpty()) {
            System.out.print("move({" + strFromNFASet(input) + "}, '" + (char)c + "') = ");
            System.out.println("{" + strFromNFASet(outSet) + "}");
        }
        return outSet;
    }

    public void DFS_e_closure(NFA start, Set<NFA> inputSet) {
        if (start.getEdge() == NFA.EPSILON) {
            if (start.next != null) {
                inputSet.add(start.next);
                DFS_e_closure(start.next, inputSet);
            }
            if (start.next2 != null) {
                inputSet.add(start.next2);
                DFS_e_closure(start.next2, inputSet);
            }
        }
        return;
    }

    /**
     * 将 NFA 的 Set 集合中元素转化成字符串输出
     * @param input NFA Set
     * @return String
     */
    private String strFromNFASet(Set<NFA> input) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator it = input.iterator();
        while (it.hasNext()) {
            stringBuilder.append(((NFA)it.next()).getStateNum());
            if (it.hasNext()) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 判断当前集合中的状态是否包含接收状态
     * 这里需要注意，用 Thompson 构造法通过正则表达式构造 NFA 时，
     * 起始和终止结点之有一个，因此判断 NFA 集合结点中是否包含终止结点时，
     * 可以判断该结点是否等于 pair.endNode, 或者判断该节点的两个指针是否都为空
     * @param input NFA set
     * @return bool
     */
    private boolean isAcceptState(Set<NFA> input) {
        Iterator it = input.iterator();
        boolean isAccepted = false;
        while (it.hasNext()) {
            if (((NFA)it.next()).getStateNum() == nfaPair.endNode.getStateNum()) {
                isAccepted = true;
            }
        }
        return isAccepted;
    }
}
