package inputSystem;

import java.util.Scanner;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 15:48
 * @brief �ӱ�׼�������У�����̨���ж��뻺����
 */

public class StdInHandler implements FileHandler {

    //��������������ַ������������������һ���ַ���
    public StringBuilder input_buffer = new StringBuilder();
    //input_buffer��ǰָ��λ��
    private int curPos = 0;

    @Override
    public void Open() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();
            if (line.equals("end")) {
                break;
            }
            input_buffer.append(line);      //StringBuilder�̲߳���ȫ�������ܸ���StringBuffer
        }
        sc.close();                         //һ��������͹ر�������
    }

    @Override
    public int Close() {
        return 0;
    }

    /**
     * �� input_buffer �ж����ݵ�������
     * @param buf ������ Start_buf
     * @param begin ������д�����ʼ��ַ
     * @param len ������Ҫд���ַ��ĳ���
     * @return ����ʵ����д����ַ�����
     */
    @Override
    public int Read(byte[] buf, int begin, int len) {
        int readCnt = 0;        //ʵ���϶�ȡ���ַ�����
        if (curPos >= input_buffer.length()) {
            return 0;
        }
        byte[] inputBuf = input_buffer.toString().getBytes();
        //���Ѿ���ȡ���ַ�������С��Ҫ���ȡ�ĳ��� �� �Ѿ���ȡ�ĳ���С����������ʣ��ĳ���
        while (readCnt < len && curPos + readCnt < input_buffer.length()) {
            buf[begin + readCnt] = inputBuf[curPos + readCnt];
            readCnt++;
        }
        curPos = curPos + readCnt;
        return readCnt;
    }
}
