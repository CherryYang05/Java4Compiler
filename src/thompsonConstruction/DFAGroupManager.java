package thompsonConstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cherry
 * @date 2022/2/14
 * @time 16:41
 * @brief 管理 DFA 分区
 */

public class DFAGroupManager {

    //存储 DFA 被划分的每个分区，这便是最小化 DFA 的各个结点
    private List<DFAGroup> dfaGroupList = new ArrayList<>();

    /**
     * 划分一个新的 DFA 分区
     * @return DFAGroup
     */
    public DFAGroup createNewGroup() {
        DFAGroup dfaGroup = DFAGroup.createNewGroup();
        dfaGroupList.add(dfaGroup);
        return dfaGroup;
    }

    /**
     * 获取当前给定的 DFA 结点在哪一个 DFA 分区中
     * @param dfaStateNum 给定的 DFA 结点编号
     * @return DFAGroup or null
     */
    public DFAGroup getContainingGroup(int dfaStateNum) {
        for (DFAGroup dfaGroup : dfaGroupList) {
            for (int i = 0; i < dfaGroup.size(); i++) {
                if (dfaStateNum == dfaGroup.get(i).stateNum) {
                    return dfaGroup;
                }
            }
        }
        return null;
    }

    /**
     * 获取当前给定的 DFA 结点在哪一个最小化的 DFA 分区中
     * @param minimizedDFAStateNum
     * @return
     */
    public DFAGroup getContainingInMinimizedGroup(int minimizedDFAStateNum) {
        for (DFAGroup dfaGroup : dfaGroupList) {
            if (minimizedDFAStateNum == dfaGroup.getGroupNum()) {
                return dfaGroup;
            }
        }
        return null;
    }

    public int size() {
        return dfaGroupList.size();
    }

    /**
     * 实现 dfaGroupList.get() 方法
     * @param index index
     * @return DFAGroup
     */
    public DFAGroup get(int index) {
        return dfaGroupList.get(index);
    }

}
