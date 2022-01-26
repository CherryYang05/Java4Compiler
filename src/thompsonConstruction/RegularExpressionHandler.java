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
    private boolean isQuoted = false;
    ArrayList<String> regularExpList = new ArrayList<>();

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
            //System.out.print("Line " + input.ii_lineno() + ": ");

            //����ǰ��Ļ��кͿո�
            while (input.ii_lookahead(1) == ' ' || input.ii_lookahead(1) == '\n') {
                input.ii_advance();
            }

            //��ʼ���պ궨��,���ҵ���һ��'{'
            StringBuilder regularExpr = new StringBuilder();
            char c = (char) input.ii_advance();

            while (c != ' ' && c != '\n') {
                if (c == '"') {
                    isQuoted = !isQuoted;
                }
                if (!isQuoted && c == '{') {
                    String macroName = getMacroNameFromInput();
                    regularExpr.append(expandMacro(macroName));
                } else {
                    regularExpr.append(c);
                }
                c = (char)input.ii_advance();
                //c = (char)input.ii_lookahead(1);
            }

            //����������ʽ����Ŀո�
            while (c == ' ') {
                c = (char) input.ii_advance();
            }
            //input.ii_advance();
            regularExpList.add(regularExpr.toString());
            //System.out.println(regularExpr);
        }
    }


    /**
     * �Ժ���׽���չ����ע�⣺��˫���� " " ֮��ĺ�����չ��
     * @param macroName ������
     * @return չ������ַ���
     */
    public String expandMacro(String macroName) throws Exception {
        String macroContent = macroHandler.expandMacro(macroName);
        String name = "";
        while (macroContent.contains("{")) {
            int beginBrace = macroContent.indexOf("{");
            int endBrace = macroContent.indexOf("}");
            if (endBrace == -1) {
                //û��ƥ��� '}'
                ErrorHandler.parseErr(ErrorHandler.Error.E_BADMAC);
            } else {
                macroName = macroContent.substring(beginBrace + 1, endBrace);
                String content = macroContent.substring(0, beginBrace);
                content += expandMacro(macroName);
                content += macroContent.substring(endBrace + 1);
                macroContent = content;
            }

        }
        return macroContent;
    }

    /**
     * ����⵽ '{' ʱ����ȡ '{}' �еĺ�����
     *
     * @return
     */
    public String getMacroNameFromInput() {
        String macroName = "";
        //input.ii_advance();
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
     * �ַ����Ƿ�������� " "�������ڵ��ַ���������滻
     * @return
     */
    private boolean isInQuoted() {
        return true;
    }

    /**
     * ��� MacroList �б�Ԫ�ظ���
     *
     * @return MacroListCount
     */
    public int getRegularExpressionCount() {
        return regularExpList.size();
    }

    /**
     * ��� MacroList �б�Ԫ��
     *
     * @param index �±�
     * @return MacroList �б�Ԫ��
     */
    public String getRegularExpression(int index) {
        return regularExpList.get(index);
    }

}
