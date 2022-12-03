package Cycle;

import Program.MainControl;

import java.util.Hashtable;

public class InstructionFetch {
	String[] instructionMemory;
	public MainControl control;

	public InstructionFetch(String[] instructions, MainControl control) {
		this.instructionMemory = instructions;
		this.control = control;
	}

	public Hashtable<String, String> InstFetch(String PCaddress) {
		System.out.println("----------------------------------------");
		System.out.println("======================");
		System.out.println("Instruction fetch");
		System.out.println("======================");
		int address = Integer.parseInt(PCaddress, 2);
		Hashtable<String,String> out = new Hashtable<>();
		out.put("PC incremented",Integer.toBinaryString(address+4));
		System.out.println("PC count = "+address);
		System.out.println("Next PC count = "+out.get("PC incremented"));
		if(instructionMemory.length > address/4 && instructionMemory[address/4] != null) {
			out.put("Instruction",instructionMemory[address/4]);
			System.out.println("Instruction = "+out.get("Instruction"));
			System.out.println("----------------------------------------");
			return out;
		}
		else {
			out.put("Instruction","");
			System.out.println("Instruction not found.");
			System.out.println("----------------------------------------");
			return out;
		}
	}
	
	public String[] check(String PCaddress) {
		int address = Integer.parseInt(PCaddress, 2);
		String[] output = new String[2];
		output[1] = Integer.toBinaryString(address+4);
		if(instructionMemory.length > address/4 && instructionMemory[address/4] != null) {
			output[0] = instructionMemory[address/4];
			return output;
		}
		else {
			output[0] = ""; 
			return output;
		}
	}
	
	public boolean ProgCount(String pc) {
		String[] output = check(pc);
		if(output[0].equals("")) {
			return false;
		}
		else {
			return true;
		}
	}
}
