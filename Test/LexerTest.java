import org.junit.Test;

import java.io.*;
import java.util.Scanner;

/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:39
 * @brief 单元测试应该是全自动执行的，并且非交互式的。测试用例通常是被定期执行的，执
 * 行过程必须完全自动化才有意义。
 * 输出结果需要人工检查的测试不是一个好的单元测试。单元 测试中不准使用 System.out 来进行人肉验证，必须使用 assert 来验证。
 */

public class LexerTest {

    @Test
    public void LexTest() throws FileNotFoundException {
        //输入重定向
        System.setIn(new FileInputStream("Test/in"));
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        //输出重定向
        //System.setOut(new PrintStream("Test/in"));
        //System.setOut(new PrintStream(new FileOutputStream("Test/in")));
        System.out.print(str);
    }

}

