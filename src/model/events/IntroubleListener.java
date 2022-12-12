package model.events;

import model.disasters.Disaster;
import simulation.Rescuable;

public interface IntroubleListener {
	public void inTrouble(Disaster d, Rescuable r);
}
