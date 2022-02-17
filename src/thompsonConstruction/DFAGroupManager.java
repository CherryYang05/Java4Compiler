package thompsonConstruction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cherry
 * @date 2022/2/14
 * @time 16:41
 * @brief ���� DFA ����
 */

public class DFAGroupManager {

    //�洢 DFA �����ֵ�ÿ���������������С�� DFA �ĸ������
    private List<DFAGroup> dfaGroupList = new ArrayList<>();

    /**
     * ����һ���µ� DFA ����
     * @return DFAGroup
     */
    public DFAGroup createNewGroup() {
        DFAGroup dfaGroup = DFAGroup.createNewGroup();
        dfaGroupList.add(dfaGroup);
        return dfaGroup;
    }

    /**
     * ��ȡ��ǰ������ DFA �������һ�� DFA ������
     * @param dfaStateNum ������ DFA �����
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
     * ��ȡ��ǰ������ DFA �������һ����С���� DFA ������
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
     * ʵ�� dfaGroupList.get() ����
     * @param index index
     * @return DFAGroup
     */
    public DFAGroup get(int index) {
        return dfaGroupList.get(index);
    }

}
