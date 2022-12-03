package Program;

import java.util.Hashtable;

public class Registers {
    Hashtable<String,String> registers = new Hashtable<>();
    Hashtable<Integer,String> regNo = new Hashtable<>();

    public Registers(){
        registers.put("$zero","00000000000000000000000000000000");//0
        registers.put("$temp1","00000000000000000000000000000000");//1
        registers.put("$temp2","00000000000000000000000000000000");//2
        registers.put("$temp3","00000000000000000000000000000000");//3
        registers.put("$temp4","00000000000000000000000000000000");//4
        registers.put("$temp5","00000000000000000000000000000000");//5
        registers.put("$temp6","00000000000000000000000000000000");//6
        registers.put("$temp7","00000000000000000000000000000000");//7
        registers.put("$temp8","00000000000000000000000000000000");//8
        registers.put("$var1","00000000000000000000000000000000");//9
        registers.put("$var2","00000000000000000000000000000000");//10
        registers.put("$var3","00000000000000000000000000000000");//11
        registers.put("$var4","00000000000000000000000000000000");//12
        registers.put("$results1","00000000000000000000000000000000");//13
        registers.put("$results2","00000000000000000000000000000000");//14
        registers.put("$results3","00000000000000000000000000000000");//15
        registers.put("$results4","00000000000000000000000000000000");//16
        registers.put("$saved1","00000000000000000000000000000000");//17
        registers.put("$saved2","00000000000000000000000000000000");//18
        registers.put("$saved3","00000000000000000000000000000000");//19
        registers.put("$saved4","00000000000000000000000000000000");//20
        registers.put("$saved5","00000000000000000000000000000000");//21
        registers.put("$saved6","00000000000000000000000000000000");//22
        registers.put("$var5","00000000000000000000000000000000");//23
        registers.put("$var6","00000000000000000000000000000000");//24
        registers.put("$var7","00000000000000000000000000000000");//25
        registers.put("$var8","00000000000000000000000000000000");//26
        registers.put("$final1","00000000000000000000000000000000");//27
        registers.put("$final2","00000000000000000000000000000000");//28
        registers.put("$final3","00000000000000000000000000000000");//29
        registers.put("$final4","00000000000000000000000000000000");//30
        registers.put("$final5","00000000000000000000000000000000");//31
        regNo.put(0,"$zero");//0
        regNo.put(1,"$temp1");//1
        regNo.put(2,"$temp2");//2
        regNo.put(3,"$temp3");//3
        regNo.put(4,"$temp4");//4
        regNo.put(5,"$temp5");//5
        regNo.put(6,"$temp6");//6
        regNo.put(7,"$temp7");//7
        regNo.put(8,"$temp8");//8
        regNo.put(9,"$var1");//9
        regNo.put(10,"$var2");//10
        regNo.put(11,"$var3");//11
        regNo.put(12,"$var4");//12
        regNo.put(13,"$results1");//13
        regNo.put(14,"$results2");//14
        regNo.put(15,"$results3");//15
        regNo.put(16,"$results4");//16
        regNo.put(17,"$saved1");//17
        regNo.put(18,"$saved2");//18
        regNo.put(19,"$saved3");//19
        regNo.put(20,"$saved4");//20
        regNo.put(21,"$saved5");//21
        regNo.put(22,"$saved6");//22
        regNo.put(23,"$var5");//23
        regNo.put(24,"$var6");//24
        regNo.put(25,"$var7");//25
        regNo.put(26,"$var8");//26
        regNo.put(27,"$final1");//27
        regNo.put(28,"$final2");//28
        regNo.put(29,"$final3");//29
        regNo.put(30,"$final4");//30
        regNo.put(31,"$final5");//31
    }

    public void addValue(int no, String value){
        if(no > 0 && no < 32){
            registers.replace(regNo.get(no),value);
        }
        else{
            if(no == 0)
                System.out.println("Cannot write the zero register...");
            else{
                System.out.println("Register not found...");
            }
        }
    }

    public String readValue(int no){
        if(no >= 0 && no < 32){
            return registers.get(regNo.get(no));
        }
        else{
            System.out.println("Register not found...");
            return "";
        }
    }
}
