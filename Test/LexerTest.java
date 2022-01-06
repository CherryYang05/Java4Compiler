import org.junit.Test;

import java.io.*;
import java.util.Scanner;

/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:39
 * @brief
 */

public class LexerTest {

    @Test
    public void LexTest() throws FileNotFoundException {
        System.setIn(new FileInputStream("Test/in"));       //输入重定向
        Scanner sc = new Scanner(System.in);
        String str = sc.next();
        //System.setOut(new PrintStream(new FileOutputStream("Test/in")));
        System.out.print(str);
    }
}

