package fsm;

/**
 * @author Cherry
 * @date 2022/1/21
 * @time 19:30
 * @brief ������������ FMS ������״̬ת������ֹ״̬�ж�
 */

public class FSMTable implements FSM {

    private final int ASCII_COUNT = 128;
    private final int STATE_COUNT = 6;      //������-����FMSֻ������״̬

    private int[][] fmsTable = new int[STATE_COUNT][ASCII_COUNT];
    private boolean[] accept = new boolean[] {false, true, true, false, true, false};

    public FSMTable() {
        //FMS���ʼ��Ϊ -1
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
     * ���������ֽ���״̬ת���Ķ�Ӧ FMS ���ʼ��
     * @param begin_state ��ʼ״̬
     * @param end_state ����״̬
     */
    private void initFMSTable(int begin_state, int end_state) {
        for (int i = 0; i < 10; i++) {
            fmsTable[begin_state][i + '0'] = end_state;
        }
    }

    /**
     * ״̬ת������ Next���ڵ�ǰ״̬�������ַ� c ���ת������һ��״̬
     * @param state ��ǰ״̬
     * @param c ������ַ�
     * @return ת�����״̬
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
     * �жϵ�ǰ״̬�Ƿ�Ϊ�ɽ���״̬
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
