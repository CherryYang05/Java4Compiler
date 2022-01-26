package thompsonConstruction;

import inputSystem.Input;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cherry
 * @date 2022/1/23
 * @time 18:47
 * @brief 正则表达式的宏的相关处理
 */

public class MacroHandler {

    private Input input = null;
    private HashMap<String, String> macroMap = new HashMap<>();

    public MacroHandler(Input input) {
        this.input = input;
        while (input.ii_lookahead(1) != input.EOF) {
            newMacro();
        }
    }
    /**
     * 输入的正则表达式遵循以下格式：
     * 宏名称 <若干空格> 宏内容 [<若干空格>]
     * 例如： D   [0-9]
     *       A   [a-z]
     */
    public void newMacro() {
        //跳过前面的换行和空格
        while (input.ii_lookahead(1) == ' ' || input.ii_lookahead(1) == '\n') {
            input.ii_advance();
        }

        //开始接收宏定义
        String macroName = "";
        char c = (char)input.ii_lookahead(1);
        while (c != ' ' && c != '\n') {
            macroName += c;
            input.ii_advance();
            c = (char)input.ii_lookahead(1);
        }

        //跳过宏名称后面的空格
        while (input.ii_lookahead(1) == ' ') {
            input.ii_advance();
        }

        //开始接收宏内容
        String macroContent = "";
        c = (char)input.ii_lookahead(1);
        while (c != ' ' && c != '\n') {
            macroContent += c;
            input.ii_advance();
            c = (char)input.ii_lookahead(1);
        }

        //接收最后的换行符'\n'
        input.ii_advance();
        macroMap.put(macroName, macroContent);
    }

    /**
     * 将宏展开的字符串添加左右小括号，例如 [0-9]->([0-9])
     * @param macroName 宏名称
     * @return 添加左右括号后的字符串
     */
    public String expandMacro(String macroName) throws Exception {
        if (!macroMap.containsKey(macroName)) {
            ErrorHandler.parseErr(ErrorHandler.Error.E_NOMAC);
        } else {
            return "(" + macroMap.get(macroName) + ")";
        }
        return "ERROR";
    }

    /**
     * 打印宏定义
     */
    public void printMacro() {
        System.out.println("获取输入的宏定义如下：");
        for (Map.Entry<String, String> map : macroMap.entrySet()) {
            System.out.println(map.getKey() + "->" + map.getValue());
        }
    }
}
