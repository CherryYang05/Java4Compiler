package inputSystem;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 15:25
 * @brief ����ϵͳ���
 */

public class InputSystem {

    private Input input = new Input();

    /**
     * ��������ϵͳ�������ȡ�������ķ�ʽ
     * @param type type=filename����ʾ�Ӵ��̻�ȡ������
     *             type=null����ʾ�ӿ���̨��ȡ������
     */
    private void runExample(String type) {
        input.ii_newFile(null);

        input.ii_mark_start();

        printWord();
        System.out.println(input.ii_mark_end());

        input.ii_mark_prev();

        /*
         *   ִ���������󣬻����������ָ���������ͼ
         *       sMark
         *         |
         *       pMark      eMark
         *         |         |
         *       Start_buf  Next                                   Danger   End_buf
         *         |         |                                        |       |
         *         V         V                                        V       V
         *         +---------------------------------------------------------+----------+
         *         | typedef |              δ��ȡ������               |       |�˷ѵ�����|
         *         +---------------------------------------------------------------------
         *         |<------------------------ BUFSIZE --------------------------------->|
         *
         */

        input.ii_mark_start();
        printWord();
        input.ii_mark_end();

        /*
         *   ִ���������󣬻����������ָ���������ͼ
         *                 sMark
         *                   |
         *       pMark       |   eMark
         *         |         |     |
         *       Start_buf   |    Next                               Danger End_buf
         *         |         |     |                                   |     |
         *         V         V     V                                   V     V
         *         +---------------------------------------------------------+---------+
         *         | typedef | int |           δ��ȡ����               |     |�˷ѵ�����|
         *         +--------------------------------------------------------------------
         *         |<------------------------ BUFSIZE -------------------------------->|
         *
         */

        //���� ii_text() �����ÿ�����ʺ���Ŀո�
        System.out.println("ǰһ���ַ���: " + input.ii_pText());//��ӡ�� typedef
        System.out.println("��ǰ���ַ���: " + input.ii_text()); //��ӡ�� STATUS
    }

    /**
     * ��ӡ�ַ�
     */
    private void printWord() {
        char c;
        while ((c = (char)input.ii_advance()) != ' ') {
            System.out.print(c);
        }
        System.out.println("");
    }

    public static void main(String[] args) {
        InputSystem inputSystem = new InputSystem();
        inputSystem.runExample(null);
    }

    /*
    typedef STATUS string public void int ABCDEFG 123456789 synchronized EndofFile
    end
     */
}
