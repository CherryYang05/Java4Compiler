package thompsonConstruction;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Cherry
 * @date 2022/1/29
 * @time 13:23
 * @brief ���� NFA �Ľ����Ϣ
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
    public static final int EPSILON = -1;       //�߶�Ӧ���Ǧ�
    public static final int CCL = -2;           //�߶�Ӧ�����ַ���
    public static final int EMPTY = -3;         //�ýڵ�û�г�ȥ�ı�
    private static final int ASCII_COUNT = 127; //���õ� ASCII �ַ��� 127 ��

    private int edge;                           //��¼ת���߶�Ӧ�����룬��������ǿ�, �ţ��ַ���(CCL),��գ�Ҳ����û�г�ȥ�ı�

    public Set<Byte> inputSet;      //�����洢�ַ�����
    public NFA next;                //��ת����һ��״̬�������ǿ�
    public NFA next2;               //��ת����һ��״̬����״̬�������� �� ��ʱ�����ָ�����Ч
    private ANCHOR anchor;          //��Ӧ��������ʽ�Ƿ�ͷ����^, ���β����$,  �������������
    private int stateNum;           //�ڵ���
    private boolean visited = false;//�ڵ��Ƿ񱻷��ʹ������ڽڵ��ӡ


    /**
     * ���캯���г�ʼ��
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
     * inputSet Ϊ��Ҫ���Ե��ַ���������Ҫ���Ե��ַ����룬ȡ�䲹��ΪҪƥ����ַ���
     * @param b
     */
    public void addToSet(Byte b) {
        inputSet.add(b);
    }

    /**
     * �� inputSet �󲹼�
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
