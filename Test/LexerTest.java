import org.junit.Test;

import java.io.*;
import java.util.Scanner;

/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:39
 * @brief ��Ԫ����Ӧ����ȫ�Զ�ִ�еģ����ҷǽ���ʽ�ġ���������ͨ���Ǳ�����ִ�еģ�ִ
 * �й��̱�����ȫ�Զ����������塣
 * ��������Ҫ�˹����Ĳ��Բ���һ���õĵ�Ԫ���ԡ���Ԫ �����в�׼ʹ�� System.out ������������֤������ʹ�� assert ����֤��
 */

public class LexerTest {

    @Test
    public void LexTest() throws FileNotFoundException {
        //�����ض���
        System.setIn(new FileInputStream("Test/in"));
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        //����ض���
        //System.setOut(new PrintStream("Test/in"));
        //System.setOut(new PrintStream(new FileOutputStream("Test/in")));
        System.out.print(str);
    }

}

