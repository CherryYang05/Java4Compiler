package inputSystem;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 16:18
 * @brief �Ӵ����ļ��ж��뻺����
 */

public class DiskFileHandler implements FileHandler {

    private String filename;

    DiskFileHandler(String filename) {
        this.filename = filename;
    }

    @Override
    public void Open() {

    }

    @Override
    public int Close() {
        return 0;
    }

    @Override
    public int Read(byte[] buf, int begin, int len) {
        return 0;
    }
}
