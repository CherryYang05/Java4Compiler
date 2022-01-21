package inputSystem;

import java.util.Arrays;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 15:26
 * @brief �����������ݼ�һЩ��������ʵ��
 */

public class Input {
    public final int MAXLOOK = 8;                          //look ahead �����ַ���
    public final int MAXLEX = 16;                          //�ִʺ���ַ�������
    public final int BUFSIZE = MAXLEX * 3 + MAXLOOK * 2;   //ȫ����������С
    public final int END = BUFSIZE;                        //������������ַ
    public int End_buf = BUFSIZE;                          //�������߼�������ַ
    public final int DANGER = End_buf - MAXLOOK;           //������Σ�������ַ��Խ����ָ����Ҫˢ������

    public final byte[] Start_buf = new byte[BUFSIZE];     //������
    public int Next = END;                                 //ָ��ǰҪ������ַ�����λ��
    public int pMark = END;                                //���ʷ���������������һ���ַ�������ʼ��ַ
    public int sMark = END;                                //���ʷ������������ĵ�ǰ�ַ�������ʼ��ַ
    public int eMark = END;                                //���ʷ������������ĵ�ǰ�ַ����Ľ�����ַ
    public int pLineno = 0;                                //��һ�����ʷ��������������ַ������ڵ��к�
    public int pLength = 0;                                //��һ�����ʷ��������������ַ�������
    public int Lineno = 1;                                 //��ǰ���ʷ��������������ַ������к�
    public int Mline  = 1;
    public boolean EOF_read = false;                       //���������Ƿ��пɶ���Ϣ

    public FileHandler fileHandler = null;
    public final byte EOF = 0;                              //��������û�п��Զ�ȡ����Ϣ
    /**
     * ���������Ƿ��пɶ����ַ�
     * @return ����������û�пɶ���Ϣ��������һ��Ҫ��д��ָ���Ѿ�Խ���߼�������ַ��
     *         ���ʾ�������Ѿ�û�пɶ����ַ�
     */
    private boolean noMoreChars() {
        return EOF_read && Next >= End_buf;
    }

    /**
     * ��ȡ FileHandler ����
     * @param fileName
     * @return StdInHandler() or DiskFileHandler(fileName)
     */
    public FileHandler getFileHandler(String fileName) {
        return fileName == null ? new StdInHandler() : new DiskFileHandler(fileName);
    }

    /**
     * �ýӿ����ھ����������Ǵ����ļ����ǿ���̨�������в�����ֻ����ָ��ͱ����ĳ�ʼ��
     * @param fileName ���̶�д�����ļ���������̨��д����null
     */
    public void ii_newFile(String fileName) {
        if (fileHandler != null) {
            fileHandler.Close();
        }
        fileHandler = getFileHandler(fileName);
        fileHandler.Open();
        EOF_read = false;
        Next = END;
        sMark = END;
        eMark = END;
        End_buf = END;
        Lineno = 1;
        Mline = 1;
    }

    /**
     * ii_advance() �������Ļ�ȡ���뺯�����������ݴ��������ж��뻺������
     * ���ӻ������з���Ҫ��ȡ���ַ����� Next ��һ���Ӷ�ָ����һ��Ҫ��ȡ���ַ�,
     * ��� Next ��λ�þ��뻺�������߼�ĩβ(End_buf)���� MAXLOOK ʱ��
     * ����Ի���������һ�� flush ����
     * һ�ζ�һ���ַ������͸��ʷ�������
     * @return Next ָ���һ���ַ�
     */
    public byte ii_advance() {
        if (noMoreChars()) {
            return 0;
        }
        //������д�뻺��������
        if (!EOF_read && ii_flush(false) < 0) {
            return -1;
        }
        if (Start_buf[Next] == '\n') {
            Lineno++;
        }
        return Start_buf[Next++];
    }

    /**
     * Ԥ��ȡ���ɸ��ַ�
     * @param n Ԥ��ȡ�ַ�����
     * @return ����Ԥ��ȡ�ַ���βָ��
     */
    public byte ii_lookahead(int n) {
        if (EOF_read && Next + n - 1 >= End_buf) {
            return EOF;
        }
        if (Next + n - 1 < 0 || Next + n - 1 >= End_buf) {
            return EOF;
        } else {
            return Start_buf[Next + n - 1];
        }
    }

    /**
     * ��������ָ������ָ�� 0
     */
    public void ii_pushback() {
        Next = 0;
    }

    /**
     * ���������ж�ȡ��Ϣ����仺����ƽ�ƺ�Ŀ��ÿռ䣬
     * ���ÿռ�ĳ����Ǵ� starting_at һֱ�� End_buf
     * ÿ�δ��������ж�ȡ�����ݳ����� MAXLEX д������
     * @param starting_at ���ÿռ���ʼ��ַ
     * @return д�����ݵĳ���
     */
    public int ii_fillBuf(int starting_at) {
        int need = (End_buf - starting_at) / MAXLEX * MAXLEX;   //��Ҫ��ȡ���ַ�������
        int got = 0;                                            //ʵ�ʶ�ȡ���ַ�������
        if (need < 0) {
            System.err.println("Internal Error (ii_fillBuf): Bad read-request starting addr...");
        } else if (need == 0) {
            return 0;
        }
        got = fileHandler.Read(Start_buf, starting_at, need);
        if (got < 0) {
            System.err.println("Can't read file from stream...");
        }
        //�������Ѿ���ĩβ
        if (got < need) {
            EOF_read = true;
        }
        //�������߼�������ַ�Ƕ�̬�仯��
        End_buf = starting_at + got;
        return got;
    }

    public static int NO_MORE_CHARS_TO_READ = 0;
    public static int FLUSH_OK = 1;
    public static int FLUSH_FAIL = -1;

    /**
     * ��������������ǰƽ�ƣ����������������¶����µ�����
     * @param force �Ƿ�ǿ��ƽ��
     * @return FLUSH STATUS
     */
    public int ii_flush(boolean force) {
        /*
         * flush ����������� Next û��Խ�� Danger �Ļ����Ǿ�ʲô������
         * Ҫ��Ȼ����һ����˵��һ�������ݽ���ƽ�ƣ������������ж������ݣ�д��ƽ�ƺ�
         * �������Ŀռ�
         *                            pMark                     DANGER
         *                              |                          |
         *     Start_buf              sMark         eMark          | Next  End_buf
         *         |                    | |           |            |  |      |
         *         V                    V V           V            V  V      V
         *         +---------------------------------------------------------+---------+
         *         |    �Ѿ���ȡ������   |          δ��ȡ������               |�˷ѵ�����|
         *         +--------------------------------------------------------------------
         *         |<---- shift_amt---->|<---------- copy_amt -------------->|
         *         |<------------------------- BUFSIZE ------------------------------->|
         *
         *  δ��ȡ�������߽��� pMark �� sMark(���߽�С���Ǹ�)����δ��ȡ����ƽ�Ƶ�����߸����Ѿ���ȡ���򣬷���1
         *  ��� flush �����ɹ���-1�������ʧ�ܣ�0 ������������Ѿ�û�п��Զ�ȡ�Ķ����ַ������ force Ϊ true
         *  ��ô���� Next ��û��Խ�� Danger,�������� Flush ����
         */
        int shift_amt, copy_amt, left_edge;
        //���
        if (noMoreChars()) {
            return NO_MORE_CHARS_TO_READ;
        }
        //�������Ѿ�û�ж�����Ϣ��
        if (EOF_read) {
            return FLUSH_OK;
        }

        if (Next > DANGER || force) {
            left_edge = Math.min(pMark, sMark);
            shift_amt = left_edge;
            //������ƶ����벻��һ��MAXLEX���ȣ���ǿ��ˢ����
            if (shift_amt < MAXLEX) {
                if (!force) {
                    return FLUSH_FAIL;
                }
                left_edge = ii_mark_start();
                ii_mark_prev();
                shift_amt = left_edge;
            }
            copy_amt = End_buf - left_edge;
            System.arraycopy(Start_buf, left_edge, Start_buf, 0, copy_amt);
            if (ii_fillBuf(copy_amt) == 0) {
                System.err.println("Internal Error (ii_flush): Buffer full, can't read");
            }
            if (pMark != 0) {
                pMark -= shift_amt;
            }
            sMark -= shift_amt;
            eMark -= shift_amt;
            Next  -= shift_amt;
        }

        return 0;
    }

    /**
     * ��ʾ��ǰ�ʷ��������������ַ���
     * @return ��ǰ�ַ���
     */
    public String ii_text() {
        return new String(Arrays.copyOfRange(Start_buf, sMark, sMark + ii_length()));
    }

    /**
     * ��ʾ�ʷ���������������һ���ַ���
     * @return ��һ���ַ���
     */
    public String ii_pText() {
        return new String(Arrays.copyOfRange(Start_buf, pMark, pMark + ii_pLength()));
    }
    /**
     * @return ���شʷ������������ĵ�ǰ�ַ����ĳ���
     */
    public int ii_length() {
        return eMark - sMark;
    }

    /**
     * @return ��һ�����ʷ��������������ַ�������
     */
    public int ii_pLength() {
        return pLength;
    }
    /**
     * @return ���ص�ǰ���ʷ��������������ַ������к�
     */
    public int ii_lineno() {
        return Lineno;
    }

    /**
     * @return ��һ�����ʷ��������������ַ������ڵ��к�
     */
    public int ii_pLineno() {
        return pLineno;
    }

    /**
     * ��Ǵʷ���������ǰ�������ַ����Ŀ�ʼָ��
     */
    public int ii_mark_start() {
        Mline = Lineno;
        eMark = sMark = Next;
        return sMark;
    }

    /**
     * ��Ǵʷ���������ǰ�������ַ����Ľ�βָ��
     */
    public int ii_mark_end() {
        Mline = Lineno;
        eMark = Next;
        return eMark;
    }

    /**
     * ִ�������������һ�����ʷ��������������ַ������޷��ڻ��������ҵ�
     * @return ��ǰ�ַ�������ʼ��ַ
     */
    public int ii_mark_prev() {
        pMark = sMark;
        pLineno = Lineno;
        pLength = eMark - sMark;
        return pMark;
    }

}
