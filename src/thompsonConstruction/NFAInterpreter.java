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
     * ����������ַ���
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
     * �űհ� ����������ʹ�� DFS ������NFAȷ�������л�·��
     * @param inputSet NFA ����
     * @return ���� �� �հ�������ļ���
     */
    Set<NFA> e_closure(Set<NFA> inputSet) {
        /*
         * ����input������nfa�ڵ�����Ӧ�Ħűհ���
         * �����հ��Ľڵ���뵽input��
         */
        Set<NFA> newSet = new HashSet<>(inputSet);
        System.out.print("��-Closure({" + strFromNFASet(inputSet) + "}) = ");
        inputSet.clear();
        for (NFA nfa : newSet) {
            DFS_e_closure(nfa, inputSet);
        }
        inputSet.addAll(newSet);
        System.out.println("{" + strFromNFASet(inputSet) + "}");
        return inputSet;
    }

    /**
     * move �������� NFA �ж��� move ������ֻҪ��ת�ַ���Ϊ �ţ��������Ϊ��
     * 1. �� NFA ���ֻ��һ�����ȣ��ҹ����� next ��㣨next2���Ϊ�գ�
     * 2. ������ѭ������������ת�ַ���ת�����½��ı�һ���� ��
     * ���� NFA ������Կ���ʹ�� move ��������ʹ�� DFS �������෴ �� �հ���������Ҫ����������
     *
     * @param input NFA ����
     * @param c ��ת�ַ�
     * @return ���� move ������ļ���
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
     * �� NFA �� Set ������Ԫ��ת�����ַ������
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
     * �жϵ�ǰ�����е�״̬�Ƿ��������״̬
     * ������Ҫע�⣬�� Thompson ���취ͨ��������ʽ���� NFA ʱ��
     * ��ʼ����ֹ���֮��һ��������ж� NFA ���Ͻ�����Ƿ������ֹ���ʱ��
     * �����жϸý���Ƿ���� pair.endNode, �����жϸýڵ������ָ���Ƿ�Ϊ��
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
