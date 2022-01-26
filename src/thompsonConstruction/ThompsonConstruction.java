package thompsonConstruction;

import inputSystem.Input;

import java.io.IOException;

/**
 * @author Cherry
 * @date 2022/1/23
 * @time 18:46
 * @brief ���� Thompson���취����������ʽת���� NFA
 */

public class ThompsonConstruction {

    private Input input = new Input();
    private MacroHandler macroHandler = null;
    private RegularExpressionHandler regularExpressionHandler = null;
    private Lexer lexer = null;

    /**
     * ��������ʽ����Ԥ�������� list
     */
    public void runMacroExample() throws IOException {
        //System.out.println("������궨�壺");
        renewInputBuffer("src/macro");
        macroHandler = new MacroHandler(input);
        macroHandler.printMacro();
    }

    /**
     * �������������ʽ���к�չ��
     */
    public void runMacroExpandExample() throws Exception {
        //System.out.println("\n������������ʽ��");
        renewInputBuffer("src/reg");

        System.out.println("\n�������������ʽ��");
        regularExpressionHandler = new RegularExpressionHandler(input, macroHandler);
        regularExpressionHandler.processRegularExpressions();

        for (int i = 0; i < regularExpressionHandler.getMacroListCount(); i++) {
            System.out.println("Line " + (i + 1) + ": " + regularExpressionHandler.getMacroListContent(i));
        }
    }

    /**
     * �Խ������������ʽ���дʷ�����
     */
    public void runLexerExample() {

    }

    /**
     * ������ϵͳ�Ļ��������г�ʼ��
     */
    public void renewInputBuffer(String fileName) throws IOException {
        input.ii_newFile(fileName); //�ӿ���̨��ȡ����
        input.ii_advance();
        input.ii_pushback();
    }


    public static void main(String[] args) throws Exception {
        ThompsonConstruction thompsonConstruction = new ThompsonConstruction();
        thompsonConstruction.runMacroExample();
        thompsonConstruction.runMacroExpandExample();
    }
}
