package Program.Cache;

import Program.Memory;

public class Cache {
    CacheBlock[] blocks = new CacheBlock[16];
    Memory m;

    public Cache(Memory m) {
        this.m = m;
        for (int i = 0; i < 16; i++) {
            blocks[i] = new CacheBlock();
        }
    }

    public void write(String address, String data) {
        //String address = addressIn.substring(22,32);
        if(address.length() == 10){
            String tag = address.substring(0, 6);
            String index = address.substring(6, 10);

            int ind = Integer.parseInt(index, 2);
            if (data.length() == 32) {
                blocks[ind].tag = tag;
                blocks[ind].data = data;
                blocks[ind].vBit = true;
                int addr = Integer.parseInt(address, 2);
                m.memory[addr] = data;
            }
            else {
                System.out.println("Data inputted too big (more than 32 bits)");
            }
        }
        else{
            System.out.println("Address size not correct");
        }
    }

    public String read(String address){
        if(address.length() == 10){
            String tag = address.substring(0, 6);
            String index = address.substring(6, 10);

            int ind = Integer.parseInt(index, 2);
            if (blocks[ind].vBit) {
                if(blocks[ind].tag != tag){
                    System.out.println("Miss!");
                    blocks[ind].tag = tag;
                    int addr = Integer.parseInt(address, 2);
                    String data = m.memory[addr];
                    blocks[ind].data = data;
                    return data;
                }
                else{
                    System.out.println("Hit!");
                    return blocks[ind].data;
                }
            }
            else {
                System.out.println("Miss!");
                blocks[ind].tag = tag;
                int addr = Integer.parseInt(address, 2);
                String data = m.memory[addr];
                blocks[ind].data = data;
                blocks[ind].vBit = true;
                return data;
            }
        }
        else{
            System.out.println("Address size not correct... returning default value zero...");
            return "00000000000000000000000000000000";
        }
    }
}
