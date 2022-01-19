package inputSystem;

/**
 * @author Cherry
 * @date 2022/1/19
 * @time 15:44
 * @brief
 * 	�ṩ�ӿ����ڴ��������л�ȡ��Ϣ�����������������Ǵ����ļ���Ҳ�����ǿ���̨��׼���룬
 * 	�����ṩһ��ӿڣ�����������뷽ʽ������ϵͳ���뿪����������ϵͳ�����
 */

public interface FileHandler {
    void Open();

    int Close();

    /**
     * ����ʵ�ʶ�ȡ���ַ�����
     * @param buf
     * @param begin
     * @param len
     * @return
     */
    int Read(byte[] buf, int begin, int len);
}
