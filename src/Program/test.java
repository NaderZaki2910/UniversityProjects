package Program;

import Program.Cache.Cache;
public class test {

	public static void main(String[] args) {
		Registers r = new Registers();

		String[] memory = new String[1024];

		for(int i = 0 ; i < 1024 ; i++) {
			memory[i] = "00000000000000000000000000000001";
		}
		
		for(int i = 0 ; i < 32 ; i++) {
			r.addValue(i,"00000000000000000000000000000001");
		}
		r.addValue(3,"00000000000000000000000100000001");
		String[] program = new String[4];

		program[3] = "00000001000010000100001000000110"; // sll r2 r2 2
		program[2] = "00000001000010000100000000000000"; // add r2 r2 r2
		program[1] = "01110001000011000000000000000100"; // bgt (r2) (r3) 4
		program[0] = "11110000000000000000000000000100"; // jump to 4

		Memory m = new Memory(program,memory);
		Cache c = new Cache(m);
		MainControl cont = new MainControl(r,c,m);
		
		cont.run();
	}

}
