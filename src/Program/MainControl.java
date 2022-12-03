package Program;

import Cycle.*;
import Program.Cache.Cache;

import java.util.Hashtable;

public class MainControl {
	Registers registers;
	String[] instruction;
	Cache c;
	int cycle;
	public boolean branchAct = false;
	boolean firstInst = true;
	public String pc = "0";
	int instCount = 0;

	public MainControl(Registers r, Cache c, Memory m) {
		this.registers = r;
		this.instruction = m.getInstructions();
		this.c = c;
		cycle = 1;
	}
	
	public void run() {
		boolean runFlag = true;
		InstructionFetch f = new InstructionFetch(instruction,this);
		PipelineRegister first = new PipelineRegister(PipelineStages.IFtoID);
		PipelineRegister second = new PipelineRegister(PipelineStages.IDtoEX);
		PipelineRegister third = new PipelineRegister(PipelineStages.EXtoMEM);
		PipelineRegister fourth = new PipelineRegister(PipelineStages.MEMtoWR);
		while(runFlag) {
			Hashtable out1 = null, out2 = null, out3 = null, out4 = null;
			System.out.println("-------------------------------------------");
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("Cycle no: " +cycle);
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("-------------------------------------------");
			first.finished();
			second.finished();
			third.finished();
			fourth.finished();
			System.out.println("At the beginning of the Cycle :");
			first.printReg();
			second.printReg();
			third.printReg();
			fourth.printReg();
			System.out.println("-------------------------------------------");
			InstructionDecode d = new InstructionDecode(registers);
			Execute e = new Execute();
			MemoryAccess m = new MemoryAccess(c,f);
			WriteBack w = new WriteBack(registers);
			if(!first.data.get("PC incremented").equals("") && f.ProgCount(first.data.get("PC incremented")) && !branchAct){
				instCount++;
				out1 = f.InstFetch(first.data.get("PC incremented"));
			}
			else{
				if(firstInst && f.ProgCount(pc)){
					instCount++;
					firstInst = false;
					out1 = f.InstFetch(pc);
				}
				else{
					if(branchAct && f.ProgCount(pc)){
						instCount++;
						out1 = f.InstFetch(pc);
						branchAct = false;
					}
				}
			}
			if(!first.empt) {
				out2 = d.InstDecode(first);
			}
			if(!second.empt) {
				out3 = e.Execute(second);
			}
			if(!third.empt) {
				out4 = m.MemAccess(third);
			}
			if(!fourth.empt)
				w.WriteBack(fourth);
			if(out4 != null){
				fourth.putData(out4,third);
				third = new PipelineRegister(PipelineStages.EXtoMEM);
			}
			else{
				fourth.empt = true;
			}
			if(out3 != null) {
				third.putData(out3,second);
				second = new PipelineRegister(PipelineStages.IDtoEX);
			}
			else {
				third.empt = true;
			}
			if(out2 != null) {
				second.putData(out2,first);
				first = new PipelineRegister(PipelineStages.IFtoID);
			}
			else {
				second.empt = true;
			}
			if(out1 != null) {
				first.putData(out1,null);
			}
			else {
				first.empt = true;
			}
			if(fourth.empt && third.empt && second.empt && first.empt){
				runFlag = false;
				System.out.println("Nothing to Execute...");
				System.out.println("-------------------------------------------");
			}
			System.out.println("Registers : \n"+registers.registers);
			cycle++;
		}
		System.out.println("instructions grabbed = "+instCount);
	}
}
