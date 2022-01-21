package fsm;

import inputSystem.Input;

import java.util.Scanner;

/**
 * @author Cherry
 * @date 2022/1/21
 * @time 20:06
 * @brief 有限状态机具体实现，用于判断输入的数是整型还是浮点数
 */

public class FiniteStateMachine {
    private byte yylook;                            //预读取的字符
    private int yyState = 0;                        //当前状态
    private int yyNextState = FSM.STATUS_FAILURE;   //下一个状态
    private int yyPreState = FSM.STATUS_FAILURE;    //前一个状态
    private boolean endOfReads = false;             //是否读完



    Scanner sc = new Scanner(System.in);
    FSMTable fsmTable = new FSMTable();
    Input input = new Input();


    FiniteStateMachine() {
        input.ii_newFile(null);
        input.ii_advance();         //读取字符再放回去，这样使得缓冲区填满输入流中的数据
        input.ii_pushback();        //指针在 0 处，方便后面的 lookahead
        input.ii_mark_start();
    }

    /**
     *
     */
    public void yylex() {
        while (true) {
            if ((yylook = input.ii_lookahead(1)) != input.EOF) {
                //yyNextState可能是换行符，换行符表示当前数据输入完成
                yyNextState = fsmTable.yy_text(yyState, yylook);
            } else {
                endOfReads = true;
                if (yyPreState != FSM.STATUS_FAILURE) {
                    yyNextState = FSM.STATUS_FAILURE;
                }
            }

            //如果下一个状态不是非法状态，则输出转换步骤，并 advance，准备读入下一个字符
            if (yyNextState != FSM.STATUS_FAILURE) {
                System.out.println("Transition from ST." + yyState + " to ST." + yyNextState
                        + " Input char: " + (char)yylook);
                yyPreState = yyState;
                yyState = yyNextState;
                input.ii_advance();
            } else {
                input.ii_mark_end();
                //如果下一个字符是换行符'\n'，则输入结束，判断数据类型
                if (yylook == '\n') {
                    System.out.println("Accepting state: ST." + yyState);
                    System.out.print("Line: " + input.pLineno + ", accepting text: " + input.ii_text());
                    switch (yyState) {
                        case 1:
                            System.out.println("-> Integer");
                            break;
                        case 2:
                        case 4:
                            System.out.println("-> Float");
                            break;
                        default:
                            System.out.println("Unknown error!");
                    }
                    //状态机归位
                    yyPreState = FSM.STATUS_FAILURE;
                    yyState = 0;
                    yyNextState = FSM.STATUS_FAILURE;
                    System.out.println();
                    input.ii_advance();
                } else {
                    //如果下一个状态不是换行符，则表示输入非法，进入错误状态流程
                    System.out.println("Input error!");
                    input.ii_advance();
                    break;
                }


            }
            if (endOfReads) {
                return;
            }
        }
    }

    public static void main(String[] args) {
        FiniteStateMachine fms = new FiniteStateMachine();
        fms.yylex();
    }
}
