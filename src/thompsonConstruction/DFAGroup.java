package thompsonConstruction;

import java.util.*;

/**
 * @author Cherry
 * @date 2022/2/14
 * @time 16:29
 * @brief DFA 的结点分区，初始时有两个分区，分别为接收结点分区和非接收结点分区
 */

public class DFAGroup {

    private static int GROUP_COUNT = 0;
    private int group_num = 0;
    private boolean isAccepted;

    //每个分区中的 DFA 结点
    private List<DFA> dfaNode = new ArrayList<>();
    //每次分割操作时要去除的 DFA 结点
    private List<DFA> tobeRemoved = new ArrayList<>();

    private DFAGroup() {
        group_num = GROUP_COUNT;
        GROUP_COUNT++;
    }

    public static DFAGroup createNewGroup() {
        return new DFAGroup();
    }

    /**
     * 实现 dfaNode.add() 方法
     * @param dfa DFA
     */
    public void addDFANode(DFA dfa) {
        dfaNode.add(dfa);
    }

    public void setTobeRemoved(DFA dfa) {
        tobeRemoved.add(dfa);
    }

    public void commitRemoved() {
        for (DFA dfa : tobeRemoved) {
            dfaNode.remove(dfa);
        }
        tobeRemoved.clear();
    }

    public DFA get(int index) {
        if (index < dfaNode.size()) {
            return dfaNode.get(index);
        } else {
            return null;
        }
    }

    public int getGroupNum() {
        return group_num;
    }

    public int size() {
        return dfaNode.size();
    }

    public void printGroup() {
        /*
         * 排序是为了调试演示方便，可以去掉，不影响逻辑
         */
        dfaNode.sort((Comparator) (o1, o2) -> {
            DFA dfa1 = (DFA) o1;
            DFA dfa2 = (DFA) o2;

            if (dfa1.stateNum > dfa2.stateNum) {
                return 1;
            }

            return 0;
        });

        System.out.print("DFA Group num: " + group_num + ". It has DFA node: ");
        for (DFA dfa : dfaNode) {
            System.out.print(dfa.stateNum + " ");
        }

        System.out.print("\n");
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
}
