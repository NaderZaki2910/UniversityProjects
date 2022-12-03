package Program;

import java.lang.invoke.SwitchPoint;
import java.util.Hashtable;

import static Program.PipelineStages.*;

public class PipelineRegister {
    public Hashtable<String, Boolean> controls = new Hashtable<>();
    public Hashtable<String, String> data = new Hashtable<>();
    Hashtable<String, Boolean> controlsTemp = new Hashtable<>();
    Hashtable<String, String> dataTemp = new Hashtable<>();
    PipelineStages type;
    boolean empt = true;

    public PipelineRegister(PipelineStages type) {
        this.type = type;
        controls.put("RegDst", false);
        controls.put("RegWrite", false);
        controls.put("AluSrc", false);
        controls.put("Branch", false);
        controls.put("MemRead", false);
        controls.put("MemWrite", false);
        controls.put("MemToReg", false);
        controls.put("BNE", false);
        controls.put("BranchGreater", false);
        controls.put("Jump", false);
        controls.put("zFlag",false);
        controls.put("Positive",false);
        data.put("Instruction","");
        data.put("AluOp","");
        data.put("ReadData1","");
        data.put("ReadData2","");
        data.put("SignExtended","");
        data.put("AluResult","");
        data.put("Branch","");
        data.put("PC incremented","");
        controlsTemp.put("RegDst", false);
        controlsTemp.put("RegWrite", false);
        controlsTemp.put("AluSrc", false);
        controlsTemp.put("Branch", false);
        controlsTemp.put("MemRead", false);
        controlsTemp.put("MemWrite", false);
        controlsTemp.put("MemToReg", false);
        controlsTemp.put("BNE", false);
        controlsTemp.put("BranchGreater", false);
        controlsTemp.put("Jump", false);
        controlsTemp.put("zFlag",false);
        controlsTemp.put("Positive",false);
        dataTemp.put("Instruction","");
        dataTemp.put("AluOp","");
        dataTemp.put("ReadData1","");
        dataTemp.put("ReadData2","");
        dataTemp.put("SignExtended","");
        dataTemp.put("AluResult","");
        dataTemp.put("Branch","");
        dataTemp.put("PC incremented","");
    }

    public void passData(PipelineRegister p){
        p.controlsTemp = this.controls;
        p.dataTemp = this.data;
        p.empt = false;
        this.empt = true;
    }

    public void putData(Hashtable results, PipelineRegister stageDone){
        if(stageDone != null){
            stageDone.passData(this);
        }
        switch(type){
            case IFtoID:
                this.dataTemp.replace("Instruction",(String)results.get("Instruction"));
                this.dataTemp.replace("PC incremented",(String)results.get("PC incremented"));
                this.empt = false;
                break;
            case IDtoEX:
                this.dataTemp.replace("AluOp",(String)results.get("AluOp"));
                this.dataTemp.replace("ReadData1",(String)results.get("ReadData1"));
                this.dataTemp.replace("ReadData2",(String)results.get("ReadData2"));
                this.dataTemp.replace("SignExtended",(String)results.get("SignExtended"));
                this.controlsTemp.replace("RegDst", (Boolean) results.get("RegDst"));
                this.controlsTemp.replace("RegWrite", (Boolean) results.get("RegWrite"));
                this.controlsTemp.replace("AluSrc", (Boolean) results.get("AluSrc"));
                this.controlsTemp.replace("Branch", (Boolean) results.get("Branch"));
                this.controlsTemp.replace("MemRead", (Boolean) results.get("MemRead"));
                this.controlsTemp.replace("MemWrite", (Boolean) results.get("MemWrite"));
                this.controlsTemp.replace("MemToReg", (Boolean) results.get("MemToReg"));
                this.controlsTemp.replace("BNE", (Boolean) results.get("BNE"));
                this.controlsTemp.replace("BranchGreater", (Boolean) results.get("BranchGreater"));
                this.controlsTemp.replace("Jump", (Boolean) results.get("Jump"));
                this.empt = false;
                break;
            case EXtoMEM:
                this.dataTemp.replace("AluResult",(String)results.get("AluResult"));
                this.dataTemp.replace("Branch",(String)results.get("Branch"));
                this.controlsTemp.replace("zFlag",(Boolean) results.get("zFlag"));
                this.controlsTemp.replace("Positive",(Boolean) results.get("Positive"));
                this.empt = false;
                break;
            case MEMtoWR:
                this.dataTemp.replace("AluResult",(String)results.get("AluResult"));
                this.dataTemp.replace("ReadData2",(String)results.get("ReadData2"));
                this.empt = false;
                break;
        }
    }

    public void finished(){
        data = dataTemp;
        controls = controlsTemp;
    }

    public void printReg(){
        System.out.println("-----------------------------------------");
        System.out.println("Type = "+type);
        System.out.println("Control Signals :");
        System.out.println(controls);
        System.out.println("Data :");
        System.out.println(data);
    }
}
