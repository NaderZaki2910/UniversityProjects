package Program;

public class Memory {
    public String[] memory = new String[2048];

    public Memory() {
        memory[0] = "";
        for (int i = 1; i < 1024; i++) {
            memory[i] = "";
        }
        for (int i = 1024; i < 2048; i++) {
            memory[i] = "";
        }
    }

    public Memory(String[] instructions, String[] data) {
        if (data.length == 0)
            for (int i = 0; i < 1024; i++) {
                memory[i] = "";
            }
        else {
            for (int i = 0; i < 1024; i++) {
                memory[i] = "";
            }
            for (int i = 0; i < data.length; i++) {
                    if (data[i] != null && data[i].length() == 32) {
                        memory[i] = data[i];
                    } else {
                        System.out.println("Address ( " + i + " ) is not 32 bits");
                    }
            }
        }
        if (instructions.length == 0) {
            for (int i = 1024; i < 2048; i++) {
                memory[i] = "";
            }
        } else {
            for (int i = 1024; i < 2048; i++) {
                memory[i] = "";
            }
            int count = 1024;
            for (int i = 0; i < instructions.length; i++) {
                if (instructions[i] != null && instructions[i].length() == 32) {
                    memory[count++] = instructions[i];
                } else {
                    System.out.println("No Instruction found or Invalid Instruction length...");
                }
            }
        }

    }

    public String[] getInstructions() {
        String[] instructions = new String[1024];
        for (int i = 1024; i < 2048; i++) {
            instructions[i - 1024] = memory[i];
        }
        return instructions;
    }
}
