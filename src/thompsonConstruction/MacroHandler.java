package thompsonConstruction;

import inputSystem.Input;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cherry
 * @date 2022/1/23
 * @time 18:47
 * @brief ������ʽ�ĺ����ش���
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
     * �����������ʽ��ѭ���¸�ʽ��
     * ������ <���ɿո�> ������ [<���ɿո�>]
     * ���磺 D   [0-9]
     *       A   [a-z]
     */
    public void newMacro() {
        //����ǰ��Ļ��кͿո�
        while (input.ii_lookahead(1) == ' ' || input.ii_lookahead(1) == '\n') {
            input.ii_advance();
        }

        //��ʼ���պ궨��
        String macroName = "";
        char c = (char)input.ii_lookahead(1);
        while (c != ' ' && c != '\n') {
            macroName += c;
            input.ii_advance();
            c = (char)input.ii_lookahead(1);
        }

        //���������ƺ���Ŀո�
        while (input.ii_lookahead(1) == ' ') {
            input.ii_advance();
        }

        //��ʼ���պ�����
        String macroContent = "";
        c = (char)input.ii_lookahead(1);
        while (c != ' ' && c != '\n') {
            macroContent += c;
            input.ii_advance();
            c = (char)input.ii_lookahead(1);
        }

        //�������Ļ��з�'\n'
        input.ii_advance();
        macroMap.put(macroName, macroContent);
    }

    /**
     * ����չ�����ַ����������С���ţ����� [0-9]->([0-9])
     * @param macroName ������
     * @return ����������ź���ַ���
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
     * ��ӡ�궨��
     */
    public void printMacro() {
        System.out.println("��ȡ����ĺ궨�����£�");
        for (Map.Entry<String, String> map : macroMap.entrySet()) {
            System.out.println(map.getKey() + "->" + map.getValue());
        }
    }
}
