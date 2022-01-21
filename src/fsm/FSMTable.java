package fsm;

/**
 * @author Cherry
 * @date 2022/1/21
 * @time 19:30
 * @brief 该类用于生成 FMS 表，用于状态转换和终止状态判断
 */

public class FSMTable implements FSM {

    private final int ASCII_COUNT = 128;
    private final int STATE_COUNT = 6;      //该整型-浮点FMS只有六个状态

    private int[][] fmsTable = new int[STATE_COUNT][ASCII_COUNT];
    private boolean[] accept = new boolean[] {false, true, true, false, true, false};

    public FSMTable() {
        //FMS表初始化为 -1
        for (int i = 0; i < STATE_COUNT; i++) {
            for (int j = 0; j < ASCII_COUNT; j++) {
                fmsTable[i][j] = -1;
            }
        }

        fmsTable[0]['.'] = 3;
        fmsTable[1]['.'] = 2;
        fmsTable[1]['e'] = 5;
        fmsTable[2]['e'] = 5;

        initFMSTable(0, 1);
        initFMSTable(1, 1);
        initFMSTable(2, 2);
        initFMSTable(3, 2);
        initFMSTable(5, 4);
        initFMSTable(4, 4);
    }

    /**
     * 将输入数字进行状态转化的对应 FMS 表初始化
     * @param begin_state 初始状态
     * @param end_state 结束状态
     */
    private void initFMSTable(int begin_state, int end_state) {
        for (int i = 0; i < 10; i++) {
            fmsTable[begin_state][i + '0'] = end_state;
        }
    }

    /**
     * 状态转换函数 Next，在当前状态下输入字符 c 后可转换到哪一个状态
     * @param state 当前状态
     * @param c 输入的字符
     * @return 转换后的状态
     */
    @Override
    public int yy_text(int state, byte c) {
        if (state == FSM.STATUS_FAILURE) {
            return FSM.STATUS_FAILURE;
        } else {
            return fmsTable[state][c];
        }
    }

    /**
     * 判断当前状态是否为可接收状态
     * @return boolean
     */
    @Override
    public boolean isAcceptState(int state) {
        if (state == FSM.STATUS_FAILURE) {
            return false;
        } else {
            return accept[state];
        }
    }
}
