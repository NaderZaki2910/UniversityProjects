package view;

import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Icons {
	
	private ArrayList<ImageIcon> icSmall = new ArrayList<>();
	private ArrayList<ImageIcon> icBig = new ArrayList<>();
	private ArrayList<ImageIcon> icSmallInf = new ArrayList<>();
	private ArrayList<ImageIcon> icBigInf = new ArrayList<>();
	private ArrayList<ImageIcon> icSmallInj = new ArrayList<>();
	private ArrayList<ImageIcon> icBigInj = new ArrayList<>();
	private ArrayList<ImageIcon> icEva = new ArrayList<>();
	private ArrayList<ImageIcon> icFir = new ArrayList<>();
	private ArrayList<ImageIcon> icGas = new ArrayList<>();
	private ArrayList<ImageIcon> icInj = new ArrayList<>();
	private ArrayList<ImageIcon> icInf = new ArrayList<>();
	private ArrayList<ImageIcon> icEvaIdle = new ArrayList<>();
	private ArrayList<ImageIcon> icFirIdle = new ArrayList<>();
	private ArrayList<ImageIcon> icGasIdle = new ArrayList<>();
	private ArrayList<ImageIcon> icInjIdle = new ArrayList<>();
	private ArrayList<ImageIcon> icInfIdle = new ArrayList<>();
	private ArrayList<ImageIcon> icB2 = new ArrayList<>();
	private ArrayList<ImageIcon> icB3 = new ArrayList<>();
	public Icons() {
		AddImages();
	}
	
	private void AddImages() {
		for(int i = 1 ; i <= 14 ; i++) {
			icBig.add(new ImageIcon(i+".gif"));
			icSmall.add(new ImageIcon(i+"_small.png"));
			icBigInj.add(new ImageIcon(i+"inj.gif"));
			icSmallInj.add(new ImageIcon(i+"_smallInj.png"));
			icBigInf.add(new ImageIcon(i+"inf.gif"));
			icSmallInf.add(new ImageIcon(i+"_smallInf.png"));
		}
		icBig.add(new ImageIcon("dead.gif"));
		icBig.add(new ImageIcon("dead1.gif"));
		icSmall.add(new ImageIcon("dead_small.png"));
		icSmall.add(new ImageIcon("dead1_small.png"));
		icEva.add(new ImageIcon("eva1.gif"));
		icEva.add(new ImageIcon("eva2.gif"));
		icFir.add(new ImageIcon("fir1.gif"));
		icFir.add(new ImageIcon("fir2.gif"));
		icFir.add(new ImageIcon("fir3.gif"));
		icInf.add(new ImageIcon("inf1.gif"));
		icInf.add(new ImageIcon("inf2.gif"));
		icInj.add(new ImageIcon("amb1.gif"));
		icInj.add(new ImageIcon("amb2.gif"));
		icInj.add(new ImageIcon("amb3.gif"));
		icGas.add(new ImageIcon("gas1.gif"));
		icGas.add(new ImageIcon("gas2.gif"));
		icEvaIdle.add(new ImageIcon("eva1idle.png"));
		icEvaIdle.add(new ImageIcon("eva2idle.png"));
		icFirIdle.add(new ImageIcon("fir1idle.png"));
		icFirIdle.add(new ImageIcon("fir2idle.png"));
		icFirIdle.add(new ImageIcon("fir3idle.png"));
		icInfIdle.add(new ImageIcon("inf1idle.png"));
		icInfIdle.add(new ImageIcon("inf2idle.png"));
		icInjIdle.add(new ImageIcon("amb1idle.png"));
		icInjIdle.add(new ImageIcon("amb2idle.png"));
		icInjIdle.add(new ImageIcon("amb3idle.png"));
		icGasIdle.add(new ImageIcon("gas1idle.png"));
		icGasIdle.add(new ImageIcon("gas2idle.png"));
		
		icB2.add(new ImageIcon("b2.png"));//0
		icB2.add(new ImageIcon("b2burn.png"));//1
		icB2.add(new ImageIcon("b2gas.png"));//2
		icB2.add(new ImageIcon("b2col.png"));//3
		icB2.add(new ImageIcon("rubble.png"));//4
		
		icB3.add(new ImageIcon("b3.png"));//0
		icB3.add(new ImageIcon("b3burn.png"));//1
		icB3.add(new ImageIcon("b3gas.png"));//2
		icB3.add(new ImageIcon("b3col.png"));//3
		icB3.add(new ImageIcon("rubble.png"));//4
	}
	
	public ArrayList<ImageIcon> getIcSmall() {
		return icSmall;
	}
	
	public ArrayList<ImageIcon> getIcSmallInf() {
		return icSmallInf;
	}

	public ArrayList<ImageIcon> getIcBigInf() {
		return icBigInf;
	}

	public ArrayList<ImageIcon> getIcSmallInj() {
		return icSmallInj;
	}

	public ArrayList<ImageIcon> getIcBigInj() {
		return icBigInj;
	}

	public ArrayList<ImageIcon> getIcBig() {
		return icBig;
	}

	public ArrayList<ImageIcon> getIcEva() {
		return icEva;
	}

	public ArrayList<ImageIcon> getIcFir() {
		return icFir;
	}

	public ArrayList<ImageIcon> getIcGas() {
		return icGas;
	}

	public ArrayList<ImageIcon> getIcInj() {
		return icInj;
	}

	public ArrayList<ImageIcon> getIcInf() {
		return icInf;
	}

	public ArrayList<ImageIcon> getIcEvaIdle() {
		return icEvaIdle;
	}

	public ArrayList<ImageIcon> getIcFirIdle() {
		return icFirIdle;
	}

	public ArrayList<ImageIcon> getIcGasIdle() {
		return icGasIdle;
	}

	public ArrayList<ImageIcon> getIcInjIdle() {
		return icInjIdle;
	}

	public ArrayList<ImageIcon> getIcInfIdle() {
		return icInfIdle;
	}

	public ArrayList<ImageIcon> getIcB2() {
		return icB2;
	}

	public ArrayList<ImageIcon> getIcB3() {
		return icB3;
	}
	
}
