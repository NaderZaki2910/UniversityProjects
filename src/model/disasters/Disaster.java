package model.disasters;
 
import model.events.DisasterListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CitizenAlreadyDeadException;
import simulation.Rescuable;
import simulation.Simulatable;

public abstract class Disaster implements Simulatable{
	private int startCycle;
	private Rescuable target;
	private boolean active;
	private DisasterListener Dlistener;
	public Disaster(int startCycle, Rescuable target) {
		this.startCycle = startCycle;
		this.target = target;
	}
	public DisasterListener getDlistener() {
		return Dlistener;
	}
	public void setDlistener(DisasterListener dlistener) {
		Dlistener = dlistener;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		boolean temp = this.isActive();
		this.active = active;
		if(temp != this.active)
			Dlistener.ChangeHappened(target, active);
	}
	public int getStartCycle() {
		return startCycle;
	}
	public Rescuable getTarget() {
		return target;
	}
	public void strike() throws CitizenAlreadyDeadException,BuildingAlreadyCollapsedException  
	{   
		///////Citizen Already Dead Exception
		if (target instanceof Citizen && ((Citizen) target).getState()==CitizenState.DECEASED){
        	throw new CitizenAlreadyDeadException(this,"The Citizen is already dead");
        }
		///// Building Already Collapsed Exception
        else if (target instanceof ResidentialBuilding && ((ResidentialBuilding)target).getStructuralIntegrity()==0){
        	throw new BuildingAlreadyCollapsedException(this,"The Building is collpased");
        }
        else{
		    target.struckBy(this);
		    active=true;
		    if(this instanceof Collapse || this instanceof Fire || this instanceof GasLeak)
		    	Dlistener.ChangeHappened(target, active);
        }
	}
	public String getType() {
		String x = "";
		if(this instanceof Collapse) {
			x = x + "Collapse";
		}
		else {
			if(this instanceof Infection)
			{
				x= x + "Infection";
			}
			else {
				if(this instanceof Injury)
				{
					x= x + "Injury";
				}
				else {
					if(this instanceof GasLeak)
					{
						x= x + "Gas Leak";
					}
					else {
						if(this instanceof Fire)
						{
							x= x + "Fire";
						}
						else {
							if(this == null) {
								x = x + "No Disaster";
							}
						}
					}
				}
			}
		}
		return x;
	}
}
