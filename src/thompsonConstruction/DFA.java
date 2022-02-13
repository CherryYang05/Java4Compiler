package thompsonConstruction;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Cherry
 * @date 2022/2/13
 * @time 18:23
 * @brief ���� DFA �����Ϣ
 */

public class DFA {
    private static int STATE_NUM = 0;       //��� DFA �����
    int stateNum = 0;                       //��ǰ DFA ���ı��
    Set<NFA> nfaStates = new HashSet<>();   //��¼ DFA ����Ӧ�� NFA �հ�����
    boolean isAccepted = false;

    /**
     * ���� NFA ��㣬���� DFA ��㣬�� NFA ��㼯���а�����ֹ��㣬
     * ��� DFA �����Ϊ��ֹ���
     * ��Ҫע����ǣ��� Thompson ���취ͨ��������ʽ���� NFA ʱ��
     * ��ʼ����ֹ���֮��һ��������ж� NFA ���Ͻ�����Ƿ������ֹ���ʱ��
     * �����жϸý���Ƿ���� pair.endNode, �����жϸýڵ������ָ���Ƿ�Ϊ��
     * @param input NFA �Ľ�㼯��
     * @return DFA ���
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
     * �жϸ� DFA ����Ӧ�� NFA ��㼯���Ƿ���ͬ
     * @param inputSet NFA ��㼯��
     * @return boolean
     */
    boolean isSameNFAStates(Set<NFA> inputSet) {
        return inputSet.equals(nfaStates);
    }

}
