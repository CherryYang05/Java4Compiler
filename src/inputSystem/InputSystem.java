package inputSystem;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 15:25
 * @brief 输入系统入口
 */

public class InputSystem {

    private Input input = new Input();

    /**
     * 运行输入系统，传入获取输入流的方式
     * @param type type=filename，表示从磁盘获取输入流
     *             type=null，表示从控制台获取输入流
     */
    private void runExample(String type) {
        input.ii_newFile(null);

        input.ii_mark_start();

        printWord();
        System.out.println(input.ii_mark_end());

        input.ii_mark_prev();

        /*
         *   执行上面语句后，缓冲区及相关指针情况如下图
         *       sMark
         *         |
         *       pMark      eMark
         *         |         |
         *       Start_buf  Next                                   Danger   End_buf
         *         |         |                                        |       |
         *         V         V                                        V       V
         *         +---------------------------------------------------------+----------+
         *         | typedef |              未读取的区域               |       |浪费的区域|
         *         +---------------------------------------------------------------------
         *         |<------------------------ BUFSIZE --------------------------------->|
         *
         */

        input.ii_mark_start();
        printWord();
        input.ii_mark_end();

        /*
         *   执行上面语句后，缓冲区及相关指针情况如下图
         *                 sMark
         *                   |
         *       pMark       |   eMark
         *         |         |     |
         *       Start_buf   |    Next                               Danger End_buf
         *         |         |     |                                   |     |
         *         V         V     V                                   V     V
         *         +---------------------------------------------------------+---------+
         *         | typedef | int |           未读取区域               |     |浪费的区域|
         *         +--------------------------------------------------------------------
         *         |<------------------------ BUFSIZE -------------------------------->|
         *
         */

        //这里 ii_text() 会输出每个单词后面的空格
        System.out.println("前一个字符串: " + input.ii_pText());//打印出 typedef
        System.out.println("当前的字符串: " + input.ii_text()); //打印出 STATUS
    }

    /**
     * 打印字符
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
