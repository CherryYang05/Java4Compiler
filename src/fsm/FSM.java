package fsm;

/**
 * @author Cherry
 * @date 2022/1/21
 * @time 19:31
 * @brief 有限状态机 FSM 接口定义
 */

public interface FSM {
    public final int STATUS_FAILURE = -1;

    /**
     * 状态转换函数 Next，在当前状态下输入字符 c 后可转换到哪一个状态
     * @param state 当前状态
     * @param c 输入的字符
     * @return 转换后的状态
     */
    int yy_text(int state, byte c);

    /**
     * 判断当前状态是否为可接收状态
     * @return boolean
     */
    boolean isAcceptState(int state);
}
