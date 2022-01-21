package inputSystem;

import java.util.Arrays;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 15:26
 * @brief 处理缓冲区数据及一些基础函数实现
 */

public class Input {
    public final int MAXLOOK = 8;                          //look ahead 最多的字符数
    public final int MAXLEX = 16;                          //分词后最长字符串长度
    public final int BUFSIZE = MAXLEX * 3 + MAXLOOK * 2;   //全部缓冲区大小
    public final int END = BUFSIZE;                        //缓冲区结束地址
    public int End_buf = BUFSIZE;                          //缓冲区逻辑结束地址
    public final int DANGER = End_buf - MAXLOOK;           //缓冲区危险区域地址（越过该指针需要刷新流）

    public final byte[] Start_buf = new byte[BUFSIZE];     //缓冲区
    public int Next = END;                                 //指向当前要读入的字符串的位置
    public int pMark = END;                                //被词法分析器分析的上一个字符串的起始地址
    public int sMark = END;                                //被词法分析器分析的当前字符串的起始地址
    public int eMark = END;                                //被词法分析器分析的当前字符串的结束地址
    public int pLineno = 0;                                //上一个被词法分析器分析的字符串所在的行号
    public int pLength = 0;                                //上一个被词法分析器分析的字符串长度
    public int Lineno = 1;                                 //当前被词法分析器分析的字符串的行号
    public int Mline  = 1;
    public boolean EOF_read = false;                       //输入流中是否还有可读信息

    public FileHandler fileHandler = null;
    public final byte EOF = 0;                              //输入流中没有可以读取的信息
    /**
     * 缓冲区中是否还有可读的字符
     * @return 若输入流中没有可读信息，并且下一个要读写的指针已经越过逻辑结束地址，
     *         则表示缓冲区已经没有可读的字符
     */
    private boolean noMoreChars() {
        return EOF_read && Next >= End_buf;
    }

    /**
     * 获取 FileHandler 类型
     * @param fileName
     * @return StdInHandler() or DiskFileHandler(fileName)
     */
    public FileHandler getFileHandler(String fileName) {
        return fileName == null ? new StdInHandler() : new DiskFileHandler(fileName);
    }

    /**
     * 该接口用于决定输入流是磁盘文件还是控制台，不进行操作，只进行指针和变量的初始化
     * @param fileName 磁盘读写则传入文件名，控制台读写则传入null
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
     * ii_advance() 是真正的获取输入函数，他将数据从输入流中读入缓冲区，
     * 并从缓冲区中返回要读取的字符并将 Next 加一，从而指向下一个要读取的字符,
     * 如果 Next 的位置距离缓冲区的逻辑末尾(End_buf)不到 MAXLOOK 时，
     * 将会对缓冲区进行一次 flush 操作
     * 一次读一个字符，传送给词法分析器
     * @return Next 指向的一个字符
     */
    public byte ii_advance() {
        if (noMoreChars()) {
            return 0;
        }
        //输入流写入缓冲区错误
        if (!EOF_read && ii_flush(false) < 0) {
            return -1;
        }
        if (Start_buf[Next] == '\n') {
            Lineno++;
        }
        return Start_buf[Next++];
    }

    /**
     * 预读取若干个字符
     * @param n 预读取字符数量
     * @return 返回预读取字符串尾指针
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
     * 将缓冲区指针重新指向 0
     */
    public void ii_pushback() {
        Next = 0;
    }

    /**
     * 从输入流中读取信息，填充缓冲区平移后的可用空间，
     * 可用空间的长度是从 starting_at 一直到 End_buf
     * 每次从输入流中读取的数据长度是 MAXLEX 写整数倍
     * @param starting_at 可用空间起始地址
     * @return 写入数据的长度
     */
    public int ii_fillBuf(int starting_at) {
        int need = (End_buf - starting_at) / MAXLEX * MAXLEX;   //需要读取的字符串长度
        int got = 0;                                            //实际读取的字符串长度
        if (need < 0) {
            System.err.println("Internal Error (ii_fillBuf): Bad read-request starting addr...");
        } else if (need == 0) {
            return 0;
        }
        got = fileHandler.Read(Start_buf, starting_at, need);
        if (got < 0) {
            System.err.println("Can't read file from stream...");
        }
        //输入流已经到末尾
        if (got < need) {
            EOF_read = true;
        }
        //缓冲区逻辑结束地址是动态变化的
        End_buf = starting_at + got;
        return got;
    }

    public static int NO_MORE_CHARS_TO_READ = 0;
    public static int FLUSH_OK = 1;
    public static int FLUSH_FAIL = -1;

    /**
     * 将缓冲区数据往前平移，并从输入流中重新读入新的数据
     * @param force 是否强制平移
     * @return FLUSH STATUS
     */
    public int ii_flush(boolean force) {
        /*
         * flush 缓冲区，如果 Next 没有越过 Danger 的话，那就什么都不做
         * 要不然像上一节所说的一样将数据进行平移，并从输入流中读入数据，写入平移后
         * 所产生的空间
         *                            pMark                     DANGER
         *                              |                          |
         *     Start_buf              sMark         eMark          | Next  End_buf
         *         |                    | |           |            |  |      |
         *         V                    V V           V            V  V      V
         *         +---------------------------------------------------------+---------+
         *         |    已经读取的区域   |          未读取的区域               |浪费的区域|
         *         +--------------------------------------------------------------------
         *         |<---- shift_amt---->|<---------- copy_amt -------------->|
         *         |<------------------------- BUFSIZE ------------------------------->|
         *
         *  未读取区域的左边界是 pMark 或 sMark(两者较小的那个)，把未读取区域平移到最左边覆盖已经读取区域，返回1
         *  如果 flush 操作成功，-1如果操作失败，0 如果输入流中已经没有可以读取的多余字符。如果 force 为 true
         *  那么不管 Next 有没有越过 Danger,都会引发 Flush 操作
         */
        int shift_amt, copy_amt, left_edge;
        //如果
        if (noMoreChars()) {
            return NO_MORE_CHARS_TO_READ;
        }
        //输入流已经没有多余信息了
        if (EOF_read) {
            return FLUSH_OK;
        }

        if (Next > DANGER || force) {
            left_edge = Math.min(pMark, sMark);
            shift_amt = left_edge;
            //如果可移动距离不足一个MAXLEX长度，则强制刷新流
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
     * 表示当前词法分析器分析的字符串
     * @return 当前字符串
     */
    public String ii_text() {
        return new String(Arrays.copyOfRange(Start_buf, sMark, sMark + ii_length()));
    }

    /**
     * 表示词法分析器分析的上一个字符串
     * @return 上一个字符串
     */
    public String ii_pText() {
        return new String(Arrays.copyOfRange(Start_buf, pMark, pMark + ii_pLength()));
    }
    /**
     * @return 返回词法分析器分析的当前字符串的长度
     */
    public int ii_length() {
        return eMark - sMark;
    }

    /**
     * @return 上一个被词法分析器分析的字符串长度
     */
    public int ii_pLength() {
        return pLength;
    }
    /**
     * @return 返回当前被词法分析器分析的字符串的行号
     */
    public int ii_lineno() {
        return Lineno;
    }

    /**
     * @return 上一个被词法分析器分析的字符串所在的行号
     */
    public int ii_pLineno() {
        return pLineno;
    }

    /**
     * 标记词法分析器当前分析的字符串的开始指针
     */
    public int ii_mark_start() {
        Mline = Lineno;
        eMark = sMark = Next;
        return sMark;
    }

    /**
     * 标记词法分析器当前分析的字符串的结尾指针
     */
    public int ii_mark_end() {
        Mline = Lineno;
        eMark = Next;
        return eMark;
    }

    /**
     * 执行这个函数后，上一个被词法解析器解析的字符串将无法在缓冲区中找到
     * @return 当前字符串的起始地址
     */
    public int ii_mark_prev() {
        pMark = sMark;
        pLineno = Lineno;
        pLength = eMark - sMark;
        return pMark;
    }

}
