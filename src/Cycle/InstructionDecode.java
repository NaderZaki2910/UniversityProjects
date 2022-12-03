package Cycle;

import Program.PipelineRegister;
import Program.Registers;

import java.util.Hashtable;

public class InstructionDecode {

    public boolean RegDst = false, RegWrite = false, AluSrc = false,
            PCSrc = false, MemRead = false, MemWrite = false,
            MemToReg = false, BNE = false, BranchGreater = false, Jump = false;
    Registers registers;


    public InstructionDecode(Registers registers) {
        this.registers = registers;
    }

    public Hashtable InstDecode(PipelineRegister p) {
        String instruction = p.data.get("Instruction");
        String opcode = instruction.substring(0, 4);
        String rs = instruction.substring(4, 9);
        String rt = instruction.substring(9, 14);
        int rsNum = Integer.parseInt(rs, 2);
        int rtNum = Integer.parseInt(rt, 2);

		Hashtable<String,Object> out = new Hashtable<>();
        out.put("AluOp",ContUnit(opcode));
        if(out.get("AluOp").equals("")){
            out.put("ReadData1","");
            out.put("ReadData2","");
            String temp = SignExtend2(instruction.substring(4, 32));
            out.put("SignExtended",temp);
            out.put("RegDst", RegDst);
            out.put("RegWrite", RegWrite);
            out.put("AluSrc", AluSrc);
            out.put("Branch", PCSrc);
            out.put("MemRead", MemRead);
            out.put("MemWrite", MemWrite);
            out.put("MemToReg", MemToReg);
            out.put("BNE", BNE);
            out.put("BranchGreater", BranchGreater);
            out.put("Jump", Jump);
            return out;
        }
        if (rsNum < 32)
			out.put("ReadData1",registers.readValue(rsNum));
        else {
            System.out.println("invalid register for rs...(Choose from 0 to 31)");
			out.put("ReadData1","00000000000000000000000000000000");
        }
        if (rtNum <= 32 && !MemRead)
			out.put("ReadData2",registers.readValue(rtNum));
        else {
            if (out.get("AluOp").equals("00") && MemRead)
				out.put("ReadData2",rt);
            else {
                System.out.println("invalid register for rt...(Choose from 0 to 31)");
				out.put("ReadData2","00000000000000000000000000000000");
            }

        }
        String temp = SignExtend(instruction.substring(14, 32));
		out.put("SignExtended",temp);
        System.out.println("----------------------------------------");
        System.out.println("======================");
        System.out.println("Instruction Decode");
        System.out.println("======================");
        System.out.println("Decoding : "+p.data.get("Instruction"));
        System.out.println("ALU Op = " + out.get("AluOp"));
        System.out.println("Register rs (Read data 1) = " + out.get("ReadData1"));
        System.out.println("Register rt (Read data 2) = " + out.get("ReadData2"));
        System.out.println("Sign Extended Part = " + out.get("SignExtended"));
        System.out.println("======================");
        System.out.println("Signals");
        System.out.println("======================");
        System.out.println("RegDst = " + RegDst);
        System.out.println("RegWrite = " + RegWrite);
        System.out.println("AluSrc = " + AluSrc);
        System.out.println("Branch Equal = " + PCSrc);
        System.out.println("MemRead = " + MemRead);
        System.out.println("MemWrite = " + MemWrite);
        System.out.println("MemToReg = " + MemToReg);
        System.out.println("----------------------------------------");
        out.put("RegDst", RegDst);
        out.put("RegWrite", RegWrite);
        out.put("AluSrc", AluSrc);
        out.put("Branch", PCSrc);
        out.put("MemRead", MemRead);
        out.put("MemWrite", MemWrite);
        out.put("MemToReg", MemToReg);
        out.put("BNE", BNE);
        out.put("BranchGreater", BranchGreater);
        out.put("Jump", Jump);
        return out;
    }

    public String SignExtend(String extendee) {
        StringBuilder s = new StringBuilder();
        String temp = extendee.substring(0, 1);
        if (extendee.length() == 18) {
            while (s.length() < 14) {
                s.append(temp);
            }
            s.append(extendee);
            return s.toString();
        } else {
            return "";
        }
    }

    public String SignExtend2(String extendee) {
        StringBuilder s = new StringBuilder();
        String temp = extendee.substring(0, 1);
        if (extendee.length() == 28) {
            while (s.length() < 4) {
                s.append(temp);
            }
            s.append(extendee);
            return s.toString();
        } else {
            return "";
        }
    }

    public String ContUnit(String OpCode) {
        String twoBitOp = "";
        if (OpCode.equals("0000")) {
            //R-type
            AluSrc = false;
            RegWrite = true;
            MemRead = false;
            RegDst = true;
            PCSrc = false;
            MemWrite = false;
            MemToReg = false;
            twoBitOp = "10";
        } else {
            if (OpCode.equals("1000")) {
                //lw
                AluSrc = true;
                MemToReg = true;
                RegWrite = true;
                MemRead = true;
                RegDst = false;
                PCSrc = false;
                MemWrite = false;
                twoBitOp = "00";
            } else {
                if (OpCode.equals("1010")) {
                    //sw
                    AluSrc = true;
                    //MemToReg = true;
                    RegWrite = false;
                    MemRead = false;
                    //RegDst = false;
                    PCSrc = false;
                    MemWrite = true;
                    twoBitOp = "00";
                } else {
                    if (OpCode.equals("0001")) {
                        //branch eq
                        AluSrc = false;
                        //MemToReg = true;
                        RegWrite = false;
                        MemRead = false;
                        //RegDst = false;
                        PCSrc = true;
                        MemWrite = false;
                        twoBitOp = "01";
                    } else {
                        if (OpCode.equals("0010")) {
                            //addi
                            AluSrc = true;
                            RegWrite = true;
                            MemRead = false;
                            RegDst = false;
                            PCSrc = false;
                            MemWrite = false;
                            MemToReg = false;
                            twoBitOp = "00";
                        } else {
                            if (OpCode.equals("0011")) {
                                //ori
                                AluSrc = true;
                                RegWrite = true;
                                MemRead = false;
                                RegDst = false;
                                PCSrc = false;
                                MemWrite = false;
                                MemToReg = false;
                                twoBitOp = "11";
                            } else {
                                if (OpCode.equals("1001")) {
                                    //branch not eq
                                    AluSrc = false;
                                    //MemToReg = true;
                                    RegWrite = false;
                                    MemRead = false;
                                    //RegDst = false;
                                    PCSrc = false;
                                    BNE = true;
                                    MemWrite = false;
                                    twoBitOp = "01";
                                } else {
                                    if (OpCode.equals("0111")) {
                                        //branch greater
                                        AluSrc = false;
                                        //MemToReg = true;
                                        RegWrite = false;
                                        MemRead = false;
                                        //RegDst = false;
                                        PCSrc = false;
                                        BNE = false;
                                        BranchGreater = true;
                                        MemWrite = false;
                                        twoBitOp = "01";
                                    }
                                    else{
                                    	if(OpCode.equals("1111")){
											AluSrc = false;
											//MemToReg = true;
											RegWrite = false;
											MemRead = false;
											//RegDst = false;
											PCSrc = false;
											BNE = false;
											BranchGreater = false;
											Jump = true;
											MemWrite = false;
										}
									}
                                }
                            }
                        }
                    }
                }
            }
        }
		return twoBitOp;
}
	
	
}
