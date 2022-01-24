package thompsonConstruction;

import inputSystem.Input;

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

    /**
     * ��������ʽ����Ԥ�������� list
     */
    public void runMacroExample() {
        System.out.println("������궨�壺");
        renewInputBuffer();
        macroHandler = new MacroHandler(input);
        macroHandler.printMacro();
    }

    /**
     * �������������ʽ���к�չ��
     */
    public void runMacroExpandExample() throws Exception {
        System.out.println("\n������������ʽ��");
        renewInputBuffer();

        System.out.println("�������������ʽ��");
        regularExpressionHandler = new RegularExpressionHandler(input, macroHandler);
        regularExpressionHandler.processRegularExpressions();

        //for (int i = 0; i < regularExpressionHandler.getMacroListCount(); i++) {
        //    System.out.println(regularExpressionHandler.getMacroListContent(i));
        //}
    }

    /**
     * ������ϵͳ�Ļ��������г�ʼ��
     */
    public void renewInputBuffer() {
        input.ii_newFile(null); //�ӿ���̨��ȡ����
        input.ii_advance();
        input.ii_pushback();
    }


    public static void main(String[] args) throws Exception {
        ThompsonConstruction thompsonConstruction = new ThompsonConstruction();
        thompsonConstruction.runMacroExample();
        thompsonConstruction.runMacroExpandExample();
    }
}
