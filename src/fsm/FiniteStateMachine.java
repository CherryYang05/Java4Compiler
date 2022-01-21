package fsm;

import inputSystem.Input;

import java.util.Scanner;

/**
 * @author Cherry
 * @date 2022/1/21
 * @time 20:06
 * @brief ����״̬������ʵ�֣������ж�������������ͻ��Ǹ�����
 */

public class FiniteStateMachine {
    private byte yylook;                            //Ԥ��ȡ���ַ�
    private int yyState = 0;                        //��ǰ״̬
    private int yyNextState = FSM.STATUS_FAILURE;   //��һ��״̬
    private int yyPreState = FSM.STATUS_FAILURE;    //ǰһ��״̬
    private boolean endOfReads = false;             //�Ƿ����



    Scanner sc = new Scanner(System.in);
    FSMTable fsmTable = new FSMTable();
    Input input = new Input();


    FiniteStateMachine() {
        input.ii_newFile(null);
        input.ii_advance();         //��ȡ�ַ��ٷŻ�ȥ������ʹ�û����������������е�����
        input.ii_pushback();        //ָ���� 0 ������������ lookahead
        input.ii_mark_start();
    }

    /**
     *
     */
    public void yylex() {
        while (true) {
            if ((yylook = input.ii_lookahead(1)) != input.EOF) {
                //yyNextState�����ǻ��з������з���ʾ��ǰ�����������
                yyNextState = fsmTable.yy_text(yyState, yylook);
            } else {
                endOfReads = true;
                if (yyPreState != FSM.STATUS_FAILURE) {
                    yyNextState = FSM.STATUS_FAILURE;
                }
            }

            //�����һ��״̬���ǷǷ�״̬�������ת�����裬�� advance��׼��������һ���ַ�
            if (yyNextState != FSM.STATUS_FAILURE) {
                System.out.println("Transition from ST." + yyState + " to ST." + yyNextState
                        + " Input char: " + (char)yylook);
                yyPreState = yyState;
                yyState = yyNextState;
                input.ii_advance();
            } else {
                input.ii_mark_end();
                //�����һ���ַ��ǻ��з�'\n'��������������ж���������
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
                    //״̬����λ
                    yyPreState = FSM.STATUS_FAILURE;
                    yyState = 0;
                    yyNextState = FSM.STATUS_FAILURE;
                    System.out.println();
                    input.ii_advance();
                } else {
                    //�����һ��״̬���ǻ��з������ʾ����Ƿ����������״̬����
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
