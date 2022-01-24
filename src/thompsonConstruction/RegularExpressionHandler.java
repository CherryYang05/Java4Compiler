package thompsonConstruction;

import inputSystem.Input;

import java.util.ArrayList;

/**
 * @author Cherry
 * @date 2022/1/23
 * @time 18:48
 * @brief ������ʽ�Ľ�������
 */

public class RegularExpressionHandler {

    private Input input = null;
    private MacroHandler macroHandler = null;
    ArrayList<String> macroList = new ArrayList<>();

    public RegularExpressionHandler(Input input, MacroHandler macroHandler) throws Exception {
        this.input = input;
        this.macroHandler = macroHandler;
    }


    /**
     * ��������ʽ����Ԥ���������ʽ�еĺ�����滻
     * ���磺{D}+ �����滻��Ϊ ([0-9])+
     * ע�⣺�궨����Լ��ף����綨�壺
     * D  [0-9]
     * A  [a-z]
     * AD {A}|{D}
     * {AD}+ �滻�� (([a-z])|([0-9]))+
     */
    public void processRegularExpressions() throws Exception {
        // ����Ļ�����ʵ���ϻ�������һ�ζ�ȡ��û�и���������ݣ�
        // ���ǿ��Ը��� EOF_read = true �жϴ�ʱ�Ѿ������ļ�ĩβ��
        while (input.ii_lookahead(1) != input.EOF) {
            System.out.print("Line " + input.ii_lineno() + ": ");

            //����ǰ��Ļ��кͿո�
            while (input.ii_lookahead(1) == ' ' || input.ii_lookahead(1) == '\n') {
                input.ii_advance();
            }

            //��ʼ���պ궨��,���ҵ���һ��'{'
            String regularExpr = "";
            char c = (char) input.ii_lookahead(1);
            while (c != ' ' && c != '\n') {
                if (c == '{') {
                    String macroName = getMacroNameFromInput();
                    regularExpr += macroHandler.expandMacro(macroName);
                } else {
                    regularExpr += c;
                }
                c = (char) input.ii_advance();
            }

            //����������ʽ����Ŀո�
            while (c == ' ') {
                c = (char) input.ii_advance();
            }
            //input.ii_advance();
            macroList.add(regularExpr);
            System.out.println(regularExpr);
        }
    }


    /**
     * �Ժ���׽���չ����ע�⣺��˫���� " " ֮��ĺ�����չ��
     *
     * @param macroName ������
     * @return չ������ַ���
     */
    public String expandMacro(String macroName) {
        return "0";
    }

    /**
     * ����⵽ '{' ʱ����ȡ '{}' �еĺ�����
     *
     * @return
     */
    public String getMacroNameFromInput() {
        String macroName = "";
        input.ii_advance();
        char c = (char) input.ii_lookahead(1);
        while (c != '}') {
            macroName += c;
            input.ii_advance();
            c = (char) input.ii_lookahead(1);
        }
        //���� '}'
        input.ii_advance();
        return macroName;
    }

    /**
     * ��� MacroList �б�Ԫ�ظ���
     *
     * @return MacroListCount
     */
    public int getMacroListCount() {
        return macroList.size();
    }

    /**
     * ��� MacroList �б�Ԫ��
     *
     * @param index �±�
     * @return MacroList �б�Ԫ��
     */
    public String getMacroListContent(int index) {
        return macroList.get(index);
    }

}
