package inputSystem;

import java.util.Scanner;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 15:48
 * @brief 从标准输入流中（控制台）中读入缓冲区
 */

public class StdInHandler implements FileHandler {

    //接收所有输入的字符串，将所有语句连成一行字符串
    public StringBuilder input_buffer = new StringBuilder();
    //input_buffer当前指针位置
    private int curPos = 0;

    @Override
    public void Open() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String line = sc.nextLine();
            if (line.equals("end")) {
                break;
            }
            input_buffer.append(line);      //StringBuilder线程不安全，但性能高于StringBuffer
        }
        sc.close();                         //一次输入完就关闭输入流
    }

    @Override
    public int Close() {
        return 0;
    }

    /**
     * 从 input_buffer 中读数据到缓冲区
     * @param buf 缓冲区 Start_buf
     * @param begin 缓冲区写入的起始地址
     * @param len 缓冲区要写入字符的长度
     * @return 返回实际上写入的字符数量
     */
    @Override
    public int Read(byte[] buf, int begin, int len) {
        int readCnt = 0;        //实际上读取的字符数量
        if (curPos >= input_buffer.length()) {
            return 0;
        }
        byte[] inputBuf = input_buffer.toString().getBytes();
        //当已经读取的字符串长度小于要求读取的长度 且 已经读取的长度小于输入流中剩余的长度
        while (readCnt < len && curPos + readCnt < input_buffer.length()) {
            buf[begin + readCnt] = inputBuf[curPos + readCnt];
            readCnt++;
        }
        curPos = curPos + readCnt;
        return readCnt;
    }
}
