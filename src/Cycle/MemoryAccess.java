package Cycle;

import Program.Cache.Cache;
import Program.PipelineRegister;

import java.util.Hashtable;

public class MemoryAccess {
	public Cache memory;
	InstructionFetch fetch;
	public MemoryAccess(Cache memory , InstructionFetch f) {
		this.fetch = f;
		this.memory = memory;
	}
	
	public Hashtable MemAccess(PipelineRegister p) {
		String ALUResult = p.data.get("AluResult");
		String ReadData = p.data.get("ReadData2");
		String SignExtended = p.data.get("SignExtended");
		String BranchAddRes = p.data.get("Branch");
		boolean zFlag = p.controls.get("zFlag");
		boolean positive = p.controls.get("Positive");
		boolean MemWrite = p.controls.get("MemWrite");
		boolean MemRead = p.controls.get("MemRead");
		boolean branchEq = p.controls.get("Branch");
		boolean branchNotEq = p.controls.get("BNE");
		boolean branchGrt = p.controls.get("BranchGreater");
		boolean jump = p.controls.get("Jump");
		Hashtable<String,String> out = new Hashtable<>();
		String[] output = new String[2];

		out.put("AluResult",ALUResult);
		out.put("ReadData2","");

		System.out.println("----------------------------------------");
		System.out.println("======================");
		System.out.println("Memory Access");
		System.out.println("======================");
		System.out.println("Instruction currently accessing memory : "+p.data.get("Instruction"));
		System.out.println("MemRead = " + MemRead);
		System.out.println("MemWrite = " + MemWrite);
		System.out.println("Branch equal = " + branchEq);
		System.out.println("Branch not equal = " + branchNotEq);
		System.out.println("Branch greater than = " + branchGrt);
		System.out.println("Positive Flag = " + positive);
		System.out.println("Jump = " + jump);
		System.out.println("Sign Extended = " + SignExtended);
		System.out.println("ALUResult = " + ALUResult);
		System.out.println("Branch Address Result = " + BranchAddRes);
		System.out.println("Z Flag = " + zFlag);
		System.out.println("Read Data 2 = " + ReadData);
		if(!branchEq && !branchNotEq && !branchGrt && !jump) {
			if(MemWrite) {
				fetch.control.pc = Integer.toBinaryString(Integer.parseInt(fetch.control.pc, 2)+4);
				memory.write(ALUResult,ReadData);
				System.out.println("----------------------------------------");
				return out;
			}else{
				if(MemRead) {
					fetch.control.pc = Integer.toBinaryString(Integer.parseInt(fetch.control.pc, 2)+4);
					String data = memory.read(ALUResult);
					System.out.println("Memory data read = " + data);
					System.out.println("----------------------------------------");
					out.replace("AluResult",data);
					out.replace("ReadData2",ReadData);
					return out;
				}
				else{
					out.replace("ReadData2",SignExtended.substring(14,19));
				}
			}
			fetch.control.pc = Integer.toBinaryString(Integer.parseInt(fetch.control.pc, 2)+4);
		}else {
			if(branchEq){
				if(!zFlag) {
					fetch.control.pc = Integer.toBinaryString(Integer.parseInt(fetch.control.pc, 2)+4);
				}
				else {
					fetch.control.pc = BranchAddRes;
					fetch.control.branchAct = true;
				}
				System.out.println("----------------------------------------");
				return out;
			}
			else{
				if(branchNotEq){
					if(zFlag) {
						fetch.control.pc = Integer.toBinaryString(Integer.parseInt(fetch.control.pc, 2)+4);
					}
					else {
						fetch.control.pc = BranchAddRes;
						fetch.control.branchAct = true;
					}
					System.out.println("----------------------------------------");
					return out;
				}
				else{
					if(branchGrt){
						if(zFlag || !positive) {
							fetch.control.pc = Integer.toBinaryString(Integer.parseInt(fetch.control.pc, 2)+4);
						}
						else {
							fetch.control.pc = BranchAddRes;
							fetch.control.branchAct = true;
						}
						System.out.println("----------------------------------------");
						return out;
					}
					else{
						fetch.control.pc = SignExtended;
						fetch.control.branchAct = true;
					}
				}
			}
		}
		return out;
	}
	
	
}
