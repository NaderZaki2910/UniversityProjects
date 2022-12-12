package model.units;

import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.events.WorldListener;
import model.people.Citizen;
import model.people.CitizenState;
import simulation.Address;
import simulation.Rescuable;

public class DiseaseControlUnit extends MedicalUnit {

	public DiseaseControlUnit(String unitID, Address location,
			int stepsPerCycle, WorldListener worldListener) {
		super(unitID, location, stepsPerCycle, worldListener);
	}

	@Override
	public void treat() {
		getTarget().getDisaster().setActive(false);
		Citizen target = (Citizen) getTarget();
		if (target.getHp() == 0) {
			jobsDone();
			return;
		} else if (target.getToxicity() > 0) {
			target.setToxicity(target.getToxicity() - getTreatmentAmount());
			if (target.getToxicity() == 0)
				target.setState(CitizenState.RESCUED);
		}

		else if (target.getToxicity() == 0)
			heal();

	}

	public void respond(Rescuable r) throws IncompatibleTargetException,CannotTreatException{
		if(this.compatible(r)){	
			if (this.canTreat(r) && this.compatible2(r)){
		      if (getTarget() != null && ((Citizen) getTarget()).getToxicity() > 0
				  && getState() == UnitState.TREATING)
			  reactivateDisaster();
		      finishRespond(r);
			}
        	else{
        		String message="";
        		if (this.canTreat(r)){
        			message=this.targetMessage();
        		}
        		else{
        			message="The Target is Already Safe";
        		}
        		throw new CannotTreatException(this, r, message);
        	}
		}
		else{
			throw new IncompatibleTargetException(this, r,this.targetMessage());
		}
	}

}
