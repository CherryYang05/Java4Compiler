package inputSystem;

import java.io.*;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 16:18
 * @brief 从磁盘文件中读入缓冲区
 */

public class DiskFileHandler implements FileHandler {

    private String filename;
    //接收所有输入的字符串，将所有语句连成一行字符串
    public StringBuilder input_buffer = new StringBuilder();
    //input_buffer当前指针位置
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
