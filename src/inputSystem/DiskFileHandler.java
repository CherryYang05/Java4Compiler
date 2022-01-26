package inputSystem;

import java.io.*;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 16:18
 * @brief �Ӵ����ļ��ж��뻺����
 */

public class DiskFileHandler implements FileHandler {

    private String filename;
    //��������������ַ������������������һ���ַ���
    public StringBuilder input_buffer = new StringBuilder();
    //input_buffer��ǰָ��λ��
    private int curPos = 0;

    FileInputStream fs = null;
    BufferedReader br = null;

    DiskFileHandler(String filename) {
        this.filename = filename;
    }

    @Override
    public void Open() throws IOException {
        String line = "";
        br = new BufferedReader(new FileReader(filename));
        while ((line = br.readLine()) != null) {
            input_buffer.append(line);
            input_buffer.append('\n');
        }
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
