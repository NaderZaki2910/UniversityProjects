package model.events;

import model.units.Unit;
import simulation.Rescuable;

public interface UnitArrivedListener {
	public void UnitArrived(Unit u, Rescuable r, Rescuable preR);
}
