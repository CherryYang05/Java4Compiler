package fsm;

/**
 * @author Cherry
 * @date 2022/1/21
 * @time 19:31
 * @brief ����״̬�� FSM �ӿڶ���
 */

public interface FSM {
    public final int STATUS_FAILURE = -1;

    /**
     * ״̬ת������ Next���ڵ�ǰ״̬�������ַ� c ���ת������һ��״̬
     * @param state ��ǰ״̬
     * @param c ������ַ�
     * @return ת�����״̬
     */
    int yy_text(int state, byte c);

    /**
     * �жϵ�ǰ״̬�Ƿ�Ϊ�ɽ���״̬
     * @return boolean
     */
    boolean isAcceptState(int state);
}
