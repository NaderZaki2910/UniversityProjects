package visualization;

import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import code.Grid;
import code.Hos;
import code.Loc;
import code.Pad;

import java.awt.event.*;

public class Visualize extends JFrame {
	public static gridPanel[][] grids;
	public nextCycleButtonListener ls;
	public static Grid grid;
	public displayPanel info;
	public nextCycleButton nextB;
	public ArrayList<String> move;
	public displayPanel movesList;
	
	
	public Visualize(String state, String moves) {
		super("PLS A+ TE3EBNA AWY :(");
		this.setResizable(true);
		grid = new Grid(state);
		grids = new gridPanel[grid.M][grid.N];
		this.setSize(1200, 700);
		setLocationRelativeTo(null);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(null);
		this.repaint();
		this.revalidate();
		Font font = new Font("SansSerif", Font.BOLD, 20);

		JLabel infoT = new JLabel("Carried Hostages", SwingConstants.CENTER);
		infoT.setFont(font);
		infoT.setBounds(10, 0, 200, 30);
		add(infoT);
		// use info.j (the JTextArea to write text
		info = new displayPanel();
		info.setBounds(10, 30, 200, 170);
		add(info);
		/////
		movesList=new displayPanel();
		movesList.setBounds(980, 0, 200,200);
		add(movesList);
		
		
		// NEXT MOVE BUTTON
		Clicklistener click= new Clicklistener();
		nextB = new nextCycleButton();
		nextB.addActionListener(click);
		nextB.setBounds(10, 200+30, 200, 30);
		add(nextB);
		/// GRID
		for (int i = 0; i < grid.M; i++) {
			for (int j = 0; j < grid.N; j++) {
				gridPanel b = new gridPanel();
				grids[j][i] = b;
				b.setOpaque(true);
				b.setBounds(225 + 70 * i, 15 + 70 * j, 70, 70);
				add(b);
			}
		}
		move=new ArrayList<String>();
		String[] split=moves.split(";");
		String m=split[0];
		String[] ms=m.split(",");
		for(int i=0;i<ms.length;i++) {
			move.add(ms[i]);
		}
		for(int i=0;i<move.size();i++) {
			//System.out.print(move.get(i)+",");
		}
		//System.out.println();
		
		updateGrids();
		this.repaint();
		this.revalidate();

		//grid.print();

	}

	public void updateGrids() {
		for (int i = 0; i < grid.M; i++) {
			for (int j = 0; j < grid.N; j++) {
				reset(grids[j][i]);
			}
		}
		// Add Neo
		grids[grid.Neo.x][grid.Neo.y].neo = true;
		grids[grid.Neo.x][grid.Neo.y].dam = grid.NeoDamage;
		// Add TB
		grids[grid.TB.x][grid.TB.y].TB = true;
		// Add Agents
		for (int i = 0; i < grid.Agents.size(); i++) {
			Loc cur = grid.Agents.get(i);
			grids[cur.x][cur.y].agent = true;
		}
		// Add new Agents
		for (int i = 0; i < grid.NewAgents.size(); i++) {
			Loc cur = grid.NewAgents.get(i);
			grids[cur.x][cur.y].newAgent = true;
		}

		// Add Pads
		for (int i = 0; i < grid.Pads.size(); i++) {
			Pad cur = grid.Pads.get(i);
			grids[cur.StartLoc.x][cur.StartLoc.y].pad = cur;
		}
		// Add Pills
		for (int i = 0; i < grid.Pills.size(); i++) {
			Loc cur = grid.Pills.get(i);
			grids[cur.x][cur.y].pill = true;
		}
		// Add Hostages
		for (int i = 0; i < grid.Hostages.size(); i++) {
			Hos cur = grid.Hostages.get(i);
			grids[cur.loc.x][cur.loc.y].hos = cur;
		}

		/////////////////////////////////

		for (int i = 0; i < grid.M; i++) {
			for (int j = 0; j < grid.N; j++) {
				grids[j][i].updateView();
				;
			}
		}
		// CARRIED
		String carried="";
		for(int i=0;i<grid.Carry.size();i++) {
			Hos cur=grid.Carry.get(i);
			carried+="H: "+cur.damage+"\n";	
		}
		info.j.setText(carried);
		// MOVES
		String mov="";
		for(int i=0;i<move.size();i++) {
			mov+=move.get(i)+"\n";
		}
		movesList.j.setText(mov);
		
////////////////////////////	
		this.repaint();
		this.revalidate();

	}

	public static void reset(gridPanel panel) {
		panel.neo = false;
		panel.agent = false;
		panel.pill = false;
		panel.hos = null;
		panel.newAgent = false;
		panel.pad = null;
	}

	public static void main(String[] args) {
	}

	private class Clicklistener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == nextB) {
				if(move.size()!=0) {
					String curMove=move.remove(0);
					grid.executeMove(curMove);
					updateGrids();
				
				}
			}
		}
	}

}
