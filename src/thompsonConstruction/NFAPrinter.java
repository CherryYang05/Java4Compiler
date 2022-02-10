package thompsonConstruction;

import java.util.Set;

/**
 * @author Cherry
 * @date 2022/1/29
 * @time 13:50
 * @brief �������ڴ�ӡ���� NFA ���
 */

public class NFAPrinter {
    private static final int ASCII_NUM = 128;
    private boolean start = true;

    public boolean printNFA(NFA startNFA) {
        if (startNFA == null || startNFA.isVisited()) {
            return false;
        }
        if (start) {
            System.out.println("\n---------- NFA ---------");
        }
        printNFANode(startNFA);
        startNFA.setVisited(true);
        if (start) {
            System.out.println(" (START STATE)");
            start = false;
        } else {
            System.out.println();
        }
        //�ݹ��ӡ���
        printNFA(startNFA.next);
        printNFA(startNFA.next2);
        return true;
    }

    private void printNFANode(NFA node) {
        if (node.next == null) {
            //System.out.println("END");
        } else {
            System.out.print("NFA state: " + node.getStateNum());
            System.out.print("-->" + node.next.getStateNum());
            //���һ��������������ȣ���˳���ӡ�������
            if (node.next2 != null) {
                System.out.print("&" + node.next2.getStateNum());
            }
            System.out.print(", Edge: ");
            switch (node.getEdge()) {
                case NFA.CCL:
                    printCCL(node.inputSet);
                    break;
                case NFA.EPSILON:
                    System.out.print("��");
                    break;
                default:
                    System.out.print((char)node.getEdge());
                    break;
            }
        }
    }

    private void printCCL(Set<Byte> set) {
        System.out.print('[');
        for (int i = 0; i < ASCII_NUM; i++) {
            if (set.contains((byte)i)) {
                if (i < ' ') {
                    System.out.print("^" + (char)(i + '@'));
                }
                else {
                    System.out.print((char)i);
                }
            }
        }
        System.out.print(']');
    }



}
