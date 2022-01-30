package thompsonConstruction;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Cherry
 * @date 2022/1/29
 * @time 13:23
 * @brief 定义 NFA 的结点信息
 */

@Setter
@Getter
public class NFA {
    public enum ANCHOR {
        NONE,
        START,
        END,
        BOTH
    }
    public static final int EPSILON = -1;       //边对应的是ε
    public static final int CCL = -2;           //边对应的是字符集
    public static final int EMPTY = -3;         //该节点没有出去的边
    private static final int ASCII_COUNT = 127; //可用的 ASCII 字符有 127 个

    private int edge;                           //记录转换边对应的输入，输入可以是空, ε，字符集(CCL),或空，也就是没有出去的边

    public Set<Byte> inputSet;      //用来存储字符集类
    public NFA next;                //跳转的下一个状态，可以是空
    public NFA next2;               //跳转的另一个状态，当状态含有两条 ε 边时，这个指针才有效
    private ANCHOR anchor;          //对应的正则表达式是否开头含有^, 或结尾含有$,  或两种情况都有
    private int stateNum;           //节点编号
    private boolean visited = false;//节点是否被访问过，用于节点打印


    /**
     * 构造函数中初始化
     */
    public NFA() {
        inputSet = new HashSet<>();
        clearState();
    }

    public void clearState() {
        inputSet.clear();
        next = null;
        next2 = null;
        anchor = ANCHOR.NONE;
        stateNum = -1;
    }

    /**
     * inputSet 为需要忽略的字符集，将需要忽略的字符加入，取其补即为要匹配的字符集
     * @param b
     */
    public void addToSet(Byte b) {
        inputSet.add(b);
    }

    /**
     * 将 inputSet 求补集
     */
    public void setComplement() {
        Set<Byte> newSet = new HashSet<>();
        for (byte i = 0; i < ASCII_COUNT; i++) {
            if (!inputSet.contains(i)) {
                newSet.add(i);
            }
        }
        inputSet.clear();
        inputSet = newSet;
    }

}
