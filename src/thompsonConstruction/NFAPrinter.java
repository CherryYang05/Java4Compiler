package thompsonConstruction;

import java.util.Set;

/**
 * @author Cherry
 * @date 2022/1/29
 * @time 13:50
 * @brief 该类用于打印所有 NFA 结点
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
        //递归打印结点
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
            //如果一个结点有两个出度，则顺序打印两个结点
            if (node.next2 != null) {
                System.out.print("&" + node.next2.getStateNum());
            }
            System.out.print(", Edge: ");
            switch (node.getEdge()) {
                case NFA.CCL:
                    printCCL(node.inputSet);
                    break;
                case NFA.EPSILON:
                    System.out.print("ε");
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
