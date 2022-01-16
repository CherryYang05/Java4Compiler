/**
 * @author Cherry
 * @date 2022/1/6
 * @time 11:21
 * @brief 代码生成
 * 修复了对于输入空格产生的错误
 */


public class Parser {
    private Lexer lexer;

    //寄存器空间（栈），设置8个寄存器
    String[] names = {"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"};
    //堆栈指针，指向各个寄存器
    private int nameP = 0;

    /**
     * 分配新的寄存器
     *
     * @return 寄存器名
     */
    private String newName() {
        if (nameP >= names.length) {
            System.out.println("Expression too complex: " + lexer.yylineno);
            System.exit(1);
        }

        String reg = names[nameP];
        nameP++;

        return reg;
    }

    private void freeNames(String s) {
        if (nameP > 0) {
            names[nameP] = s;
            nameP--;
        } else {
            System.out.println("(Internal error) Name stack underflow: " + lexer.yylineno);
        }
    }

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public void statements() {
        String tempvar;
        //expression(tempvar);

        while (!lexer.match(Lexer.EOI)) {
            //if (!lexer.match(Lexer.BLANK)) {
                tempvar = newName();
                expression(tempvar);
                freeNames(tempvar);
                if (lexer.match(Lexer.SEMI)) {
                    lexer.advance();
                } else {
                    System.out.println("Inserting missing semicolon: " + lexer.yylineno);
                }
            //}
        }
    }

    private void expression(String tempVar) {
        String tempVar2;
        term(tempVar);
        while (lexer.match(Lexer.PLUS)) {
            lexer.advance();
            tempVar2 = newName();
            term(tempVar2);
            System.out.println(tempVar + " += " + tempVar2);
            freeNames(tempVar2);
        }
    }


    private void term(String tempVar) {
        String tempVar2;
        factor(tempVar);
        while (lexer.match(Lexer.TIMES)) {
            lexer.advance();
            tempVar2 = newName();
            factor(tempVar2);
            System.out.println(tempVar + " *= " + tempVar2);
            freeNames(tempVar2);
        }

    }

    private void factor(String tempVar) {
        if (lexer.match(Lexer.NUM_OR_ID)) {
            System.out.println(tempVar + " = " + lexer.yytext);
            lexer.advance();
        } else if (lexer.match(Lexer.LP)) {
            lexer.advance();
            expression(tempVar);
            if (lexer.match(Lexer.RP)) {
                lexer.advance();
            } else {
                //括号不匹配
                System.out.println("Mismatched parenthesis: " + lexer.yylineno);
            }
        } else {
            System.out.println("Number or identifier expected: " + lexer.yylineno);
        }
    }
}
