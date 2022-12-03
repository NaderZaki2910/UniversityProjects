package Cycle;

import Program.PipelineRegister;

import java.util.HashMap;
import java.util.Hashtable;

public class Execute {
    public boolean zFlag = false;
    public boolean positive = false;
    public Execute() {
    }

    public Hashtable Execute(PipelineRegister p) {
        String Op = "";
        int z = -1;
        String ALUOp = p.data.get("AluOp");
        boolean AluSrc = p.controls.get("AluSrc");
        String Operand1 = p.data.get("ReadData1");
        String Operand2 = p.data.get("ReadData2");
        String pcInc = p.data.get("PC incremented");
        String SignExtended = p.data.get("SignExtended");
        String strZero = "00000000000000000000000000000000";
        String[] output = new String[2];
        Hashtable<String, Object> out = new Hashtable<>();
        System.out.println("----------------------------------------");
        System.out.println("======================");
        System.out.println("Execute");
        System.out.println("======================");
        System.out.println("Executing : "+ p.data.get("Instruction"));
        if (ALUOp.equals("00")) {
            Op = "0010";
        } else {
            if (ALUOp.equals("01")) {
                Op = "0110";
            } else {
                if (ALUOp.equals("10")) {
                    String funct = SignExtended.substring(24, 32);
                    switch (funct) {
                        case ("00000000"):
                            Op = "0010";
                            break;//add
                        case ("00000001"):
                            Op = "0110";
                            break;//sub
                        case ("00000010"):
                            Op = "0000";
                            break;//and
                        case ("00000011"):
                            Op = "0001";
                            break;//or
                        case ("00000100"):
                            Op = "0111";
                            break;//slt
                        case ("00000101"):
                            Op = "1101";
                            break;//mult
                        case ("00000110"):
                            Op = "1111";
                            break;//shift left
                        case ("00000111"):
                            Op = "1000";
                            break;//shift right
                    }
                } else {
                    if (ALUOp.equals("11"))
                        Op = "0001";
                    else {
                        out.put("AluResult", "");
                        out.put("Branch", "");
                    }
                }
            }
        }
        if (!AluSrc) {
            if (Operand1.length() <= 32 && Operand2.length() <= 32 && Op.length() == 4) {
                if (Op.equals("0000")) {
                    String result = and(Operand1, Operand2);
                    out.put("AluResult", result);
                    out.put("Branch", "");
                    z = (result.equals(strZero)) ? 1 : 0;
                    System.out.println("Operation Name: AND, ALU Op = " + Op);
                    System.out.println("Read Data 1: " + Operand1 + "/" + Integer.parseInt(Operand1, 2));
                    System.out.println("Read Data 2: " + Operand2 + "/" + Integer.parseInt(Operand2, 2));
                    System.out.println("ALU Result: " + result + "/" + Integer.parseInt(result, 2));
                    System.out.println("Branch Address result = ");
                    System.out.println("Z-Flag Value: " + z);
                } else {
                    if (Op.equals("0001")) {
                        String result = or(Operand1, Operand2);
                        out.put("AluResult", result);
                        out.put("Branch", "");
                        z = (result.equals(strZero)) ? 1 : 0;
                        System.out.println("Operation Name: OR, ALU Op = " + Op);
                        System.out.println("Read Data 1: " + Operand1 + "/" + Integer.parseInt(Operand1, 2));
                        System.out.println("Read Data 2: " + Operand2 + "/" + Integer.parseInt(Operand2, 2));
                        System.out.println("ALU Result: " + result + "/" + Integer.parseInt(result, 2));
                        System.out.println("Branch Address result = ");
                        System.out.println("Z-Flag Value: " + z);
                    } else {
                        if (Op.equals("0010")) {
                            String result = add(Operand1, Operand2);
                            out.put("AluResult", result);
                            out.put("Branch", "");
                            int out1 = Integer.parseUnsignedInt(result, 2);
                            z = (out1 == 0) ? 1 : 0;
                            System.out.println("Operation Name: ADD, ALU Op = " + Op);
                            System.out.println("Read Data 1: " + Operand1);
                            System.out.println("Read Data 2: " + Operand2);
                            System.out.println("ALU Result: " + result + "/" + out);
                            System.out.println("Branch Address result = " + output[1]);
                            System.out.println("Z-Flag Value: " + z);
                        } else {
                            if (Op.equals("0110")) {
                                String result = sub(Operand1, Operand2);
                                out.put("AluResult", result);
                                int out1 = Integer.parseUnsignedInt(result, 2);
                                String branch = Integer.toBinaryString(Integer.parseInt(pcInc, 2) + Integer.parseUnsignedInt(SignExtended.substring(16, 32), 2));
                                if ((ALUOp.equals("01"))) {
                                    if(out1 > 0)
                                        positive = true;
                                    out.put("Branch", branch);
                                } else {
                                    out.put("Branch", "");
                                }
                                z = (out1 == 0) ? 1 : 0;
                                System.out.println("Operation Name: SUB, ALU Op = " + Op);
                                System.out.println("Read Data 1: " + Operand1);
                                System.out.println("Read Data 2: " + Operand2);
                                System.out.println("ALU Result: " + result + "/" + out);
                                System.out.println("Branch Address result = " + out.get("Branch"));
                                System.out.println("Z-Flag Value: " + z);
                            } else {
                                if (Op.equals("0111")) {
                                    int oper1 = Integer.parseUnsignedInt(Operand1, 2);
                                    int oper2 = Integer.parseUnsignedInt(Operand2, 2);
                                    int Out = (oper1 < oper2) ? 1 : 0;
                                    String out1 = (Out == 0) ? strZero : "01111111111111111111111111111111";
                                    out.put("AluResult", out1);
                                    out.put("Branch", "");
                                    z = (Out == 0) ? 1 : 0;
                                    System.out.println("Operation Name: SLT, ALU Op = " + Op);
                                    System.out.println("Read Data 1: " + Operand1 + "/" + oper1);
                                    System.out.println("Read Data 2: " + Operand2 + "/" + oper2);
                                    System.out.println("ALU Result: " + out1 + "/" + out);
                                    System.out.println("Branch Address result = ");
                                    System.out.println("Z-Flag Value: " + z);
                                } else {
                                    if (Op.equals("1101")) {
                                        String result = mult(Operand1, Operand2);
                                        out.put("AluResult", result);
                                        out.put("Branch", "");
                                        int out1 = Integer.parseUnsignedInt(result, 2);
                                        z = (out1 == 0) ? 1 : 0;
                                        System.out.println("Operation Name: MULT, ALU Op = " + Op);
                                        System.out.println("Read Data 1: " + Operand1);
                                        System.out.println("Read Data 2: " + Operand2);
                                        System.out.println("ALU Result: " + result + "/" + out1);
                                        System.out.println("Branch Address result = ");
                                        System.out.println("Z-Flag Value: " + z);
                                    } else {
                                        if (Op.equals("1000")) {
                                            String result = shiftR(Operand1, SignExtended.substring(19, 24));
                                            out.put("AluResult", result);
                                            out.put("Branch", "");
                                            int out1 = Integer.parseUnsignedInt(result, 2);
                                            z = (out1 == 0) ? 1 : 0;
                                            System.out.println("Operation Name: SRL, ALU Op = " + Op);
                                            System.out.println("Read Data 1: " + Operand1);
                                            System.out.println("Read Data 2: " + Operand2);
                                            System.out.println("ALU Result: " + result + "/" + out1);
                                            System.out.println("Branch Address result = ");
                                            System.out.println("Z-Flag Value: " + z);
                                        } else {
                                            if (Op.equals("1111")) {
                                                String result = shiftL(Operand1, SignExtended.substring(19, 24));
                                                out.put("AluResult", result);
                                                out.put("Branch", "");
                                                int out1 = Integer.parseUnsignedInt(result, 2);
                                                z = (out1 == 0) ? 1 : 0;
                                                System.out.println("Operation Name: SLL, ALU Op = " + Op);
                                                System.out.println("Read Data 1: " + Operand1);
                                                System.out.println("Read Data 2: " + Operand2);
                                                System.out.println("ALU Result: " + result + "/" + out1);
                                                System.out.println("Branch Address result = ");
                                                System.out.println("Z-Flag Value: " + z);
                                            }
                                        }
                                        System.out.println("Invalid operation");
                                    }
                                }

                            }
                        }
                    }
                }
            }
            System.out.println("----------------------------------------");
        } else {
            if (Op.equals("0010")) {
                String result;
                if(p.controls.get("MemRead") || p.controls.get("MemWrite"))
                    result = addi(Operand1, SignExtended.substring(16, 32));
                else{
                    result = add(Operand1, SignExtended.substring(16, 32));
                }
                out.put("AluResult", result);
                out.put("Branch", "");
                z = (result.equals(strZero)) ? 1 : 0;
                System.out.println("Operation Name: ADD, ALU Op = " + Op);
                System.out.println("Read Data 1: " + Operand1);
                System.out.println("Read Data 2: " + SignExtended.substring(16, 32));
                System.out.println("ALU Result: " + result);
                System.out.println("Branch Address result = ");
                System.out.println("Z-Flag Value: " + z);
            } else {
                if (Op.equals("0001")) {
                    String result = or(Operand1, SignExtended.substring(16, 32));
                    out.put("AluResult", result);
                    out.put("Branch", "");
                    z = (result.equals(strZero)) ? 1 : 0;
                    System.out.println("Operation Name: ADDI, ALU Op = " + Op);
                    System.out.println("Read Data 1: " + Operand1);
                    System.out.println("Read Data 2: " + SignExtended.substring(16, 32));
                    System.out.println("ALU Result: " + result);
                    System.out.println("Branch Address result = ");
                    System.out.println("Z-Flag Value: " + z);
                    System.out.println("----------------------------------------");
                }
                System.out.println("Invalid operation");
            }
            System.out.println("----------------------------------------");
        }
        if (z != -1) {
            switch (z) {
                case (1):
                    zFlag = true;
                    break;
                case (0):
                    zFlag = false;
                    break;
                default:
                    System.out.println("Error with Z flag instance var");
            }
            System.out.println("----------------------------------------");
        }
        out.put("zFlag", zFlag);
        out.put("Positive", positive);
        return out;
    }

    public static String and(String op1, String op2) {
        String temp1 = "";
        String temp2 = "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (i < op1.length()) {
                temp1 = op1.substring(i, i + 1);
            } else {
                temp1 = "0";
            }
            if (i < op2.length()) {
                temp2 = op2.substring(i, i + 1);
            } else {
                temp2 = "0";
            }
            if (temp1.equals("1") && temp2.equals("1")) {
                result.append("1");
            } else {
                result.append("0");
            }
        }
        return result.toString();
    }

    public static String or(String op1, String op2) {
        String temp1 = "";
        String temp2 = "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            if (i < op1.length()) {
                temp1 = op1.substring(i, i + 1);
            } else {
                temp1 = "0";
            }
            if (i < op2.length()) {
                temp2 = op2.substring(i, i + 1);
            } else {
                temp2 = "0";
            }
            if (temp1.equals("1") || temp2.equals("1")) {
                result.append("1");
            } else {
                result.append("0");
            }
        }
        return result.toString();
    }

    public static String add(String op1, String op2) {
        int oper1 = Integer.parseUnsignedInt(op1, 2);
        int oper2 = Integer.parseUnsignedInt(op2, 2);
        int out = oper1 + oper2;
        String out1 = Integer.toBinaryString(out);
        StringBuilder result = new StringBuilder();
        for (int i = 32 - out1.length(); i > 0; i--) {
            result.append("0");
        }
        result.append(out1);
        return result.toString();
    }

    public static String addi(String op1, String op2) {
        int oper1 = Integer.parseInt(op1, 2);
        int oper2 = Integer.parseInt(op2, 2);
        int out = oper1 + oper2;
        String out1 = Integer.toBinaryString(out);
        StringBuilder result = new StringBuilder();
        for (int i = 10 - out1.length(); i > 0; i--) {
            result.append("0");
        }
        result.append(out1);
        return result.toString();
    }

    public static String mult(String op1, String op2) {
        int oper1 = Integer.parseUnsignedInt(op1, 2);
        int oper2 = Integer.parseUnsignedInt(op2, 2);
        int out = oper1 * oper2;
        String out1 = Integer.toBinaryString(out);
        StringBuilder result = new StringBuilder();
        for (int i = 32 - out1.length(); i > 0; i--) {
            result.append("0");
        }
        result.append(out1);
        return result.toString();
    }

    public static String sub(String op1, String op2) {
        int oper1 = Integer.parseUnsignedInt(op1, 2);
        int oper2 = Integer.parseUnsignedInt(op2, 2);
        int out = oper1 - oper2;
        String out1 = Integer.toBinaryString(out);
        StringBuilder result = new StringBuilder();
        for (int i = 32 - out1.length(); i > 0; i--) {
            result.append("0");
        }
        result.append(out1);
        return result.toString();
    }

    public static String shiftR(String op1, String op2) {
        StringBuilder b = new StringBuilder();
        int shAmt = Integer.parseInt(op2, 2);
        for (int i = 0; i < shAmt; i++) {
            b.append("0");
        }
        b.append(op1);
        return b.substring(0, 32);
    }

    public static String shiftL(String op1, String op2) {
        StringBuilder b = new StringBuilder();
        int shAmt = Integer.parseInt(op2, 2);
        b.append(op1);
        for (int i = 0; i < shAmt; i++) {
            b.append("0");
        }

        return b.substring(shAmt, 32 + shAmt);
    }
}
