package Cycle;

import Program.PipelineRegister;
import Program.Registers;

public class WriteBack {
	boolean writeFlag;
	public Registers registers;
	public WriteBack(Registers registers) {
		this.writeFlag = false;
		this.registers = registers;
	}
	
	public void WriteBack(PipelineRegister p) {
		String AluResult = p.data.get("AluResult");
		String destReg = p.data.get("ReadData2");
		boolean MemToReg = p.controls.get("MemToReg");
		boolean RegDst = p.controls.get("RegDst");
		System.out.println("----------------------------------------");
		System.out.println("======================");
		System.out.println("Write Back");
		System.out.println("======================");
		System.out.println("Write Back Instruction : "+p.data.get("Instruction"));
		System.out.println("ALUResult = " + AluResult);
		System.out.println("Read data (destination register) = " + destReg);
		System.out.println("MemToReg = " + MemToReg);
		System.out.println("RegDst = " + RegDst);
		System.out.println("----------------------------------------");
		if(MemToReg || RegDst) {
			int reg = Integer.parseInt(destReg, 2);
			if(reg < 32 && reg >= 0) {
				registers.addValue(reg,AluResult);
			}
			else {
				System.out.println("-------------------------------------------");
				System.out.println("No WriteBack");
				System.out.println("No such register exist...(Choose from 0 to 31)");
				System.out.println("-------------------------------------------");
			}
		}
		else {
				System.out.println("-------------------------------------------");
				System.out.println("No WriteBack");
				System.out.println("-------------------------------------------");

		}
	}
}
