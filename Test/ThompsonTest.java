import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cherry
 * @date 2022/1/29
 * @time 14:24
 * @brief Thompson ππ‘Ï∑®≤‚ ‘
 */

public class ThompsonTest {

    @Test
    public void Test1() {
        String inputString = "13456";
        System.out.printf("The input string %s is illegal ", inputString);
    }

    @Test
    public void Test2() {
        List<Integer> list = new ArrayList<>();
        list.add(12);
        //list.add(23);
        Integer integer = list.get(1);
        System.out.println(integer == null);
    }
}
