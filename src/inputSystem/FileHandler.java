package inputSystem;

import java.io.IOException;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 15:44
 * @brief
 * 	提供接口用于从输入流中获取信息，由于输入对象可以是磁盘文件，也可以是控制台标准输入，
 * 	所以提供一组接口，将具体的输入方式与输入系统隔离开来，简化输入系统的设计
 */

public interface FileHandler {
    void Open() throws IOException;

    int Close();

    /**
     * 返回实际读取的字符长度
     * @param buf
     * @param begin
     * @param len
     * @return
     */
    int Read(byte[] buf, int begin, int len);
}
