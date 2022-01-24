package thompsonConstruction;

import inputSystem.Input;

/**
 * @author Cherry
 * @date 2022/1/23
 * @time 18:46
 * @brief 利用 Thompson构造法，将正则表达式转化成 NFA
 */

public class ThompsonConstruction {

    private Input input = new Input();
    private MacroHandler macroHandler = null;
    private RegularExpressionHandler regularExpressionHandler = null;

    /**
     * 对正则表达式进行预处理，存入 list
     */
    public void runMacroExample() {
        System.out.println("请输入宏定义：");
        renewInputBuffer();
        macroHandler = new MacroHandler(input);
        macroHandler.printMacro();
    }

    /**
     * 将输入的正则表达式进行宏展开
     */
    public void runMacroExpandExample() throws Exception {
        System.out.println("\n请输入正则表达式：");
        renewInputBuffer();

        System.out.println("解析后的正则表达式：");
        regularExpressionHandler = new RegularExpressionHandler(input, macroHandler);
        regularExpressionHandler.processRegularExpressions();

        //for (int i = 0; i < regularExpressionHandler.getMacroListCount(); i++) {
        //    System.out.println(regularExpressionHandler.getMacroListContent(i));
        //}
    }

    /**
     * 对输入系统的缓冲区进行初始化
     */
    public void renewInputBuffer() {
        input.ii_newFile(null); //从控制台获取输入
        input.ii_advance();
        input.ii_pushback();
    }


    public static void main(String[] args) throws Exception {
        ThompsonConstruction thompsonConstruction = new ThompsonConstruction();
        thompsonConstruction.runMacroExample();
        thompsonConstruction.runMacroExpandExample();
    }
}
