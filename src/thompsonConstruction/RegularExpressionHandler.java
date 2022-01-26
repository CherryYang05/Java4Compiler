package thompsonConstruction;

import inputSystem.Input;

import java.util.ArrayList;

/**
 * @author Cherry
 * @date 2022/1/23
 * @time 18:48
 * @brief 正则表达式的解析处理
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
     * 对正则表达式进行预处理，将表达式中的宏进行替换
     * 例如：{D}+ 进行替换后为 ([0-9])+
     * 注意：宏定义可以间套，例如定义：
     * D  [0-9]
     * A  [a-z]
     * AD {A}|{D}
     * {AD}+ 替换成 (([a-z])|([0-9]))+
     */
    public void processRegularExpressions() throws Exception {
        // 这里的缓冲区实际上还存有上一次读取的没有覆盖完的数据，
        // 但是可以根据 EOF_read = true 判断此时已经读到文件末尾了
        while (input.ii_lookahead(1) != input.EOF) {
            //System.out.print("Line " + input.ii_lineno() + ": ");

            //跳过前面的换行和空格
            while (input.ii_lookahead(1) == ' ' || input.ii_lookahead(1) == '\n') {
                input.ii_advance();
            }

            //开始接收宏定义,先找到第一个'{'
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

            //跳过正则表达式后面的空格
            while (c == ' ') {
                c = (char) input.ii_advance();
            }
            //input.ii_advance();
            regularExpList.add(regularExpr.toString());
            //System.out.println(regularExpr);
        }
    }


    /**
     * 对宏间套进行展开，注意：在双引号 " " 之间的宏无需展开
     * @param macroName 宏名称
     * @return 展开后的字符串
     */
    public String expandMacro(String macroName) throws Exception {
        String macroContent = macroHandler.expandMacro(macroName);
        String name = "";
        while (macroContent.contains("{")) {
            int beginBrace = macroContent.indexOf("{");
            int endBrace = macroContent.indexOf("}");
            if (endBrace == -1) {
                //没有匹配的 '}'
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
     * 当检测到 '{' 时，获取 '{}' 中的宏名称
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
        //跳过 '}'
        input.ii_advance();
        return macroName;
    }

    /**
     * 字符串是否包含引号 " "，引号内的字符串无需宏替换
     * @return
     */
    private boolean isInQuoted() {
        return true;
    }

    /**
     * 获得 MacroList 列表元素个数
     *
     * @return MacroListCount
     */
    public int getRegularExpressionCount() {
        return regularExpList.size();
    }

    /**
     * 获得 MacroList 列表元素
     *
     * @param index 下标
     * @return MacroList 列表元素
     */
    public String getRegularExpression(int index) {
        return regularExpList.get(index);
    }

}
