package model.events;

import simulation.Rescuable;

public interface DisasterListener {
	public void ChangeHappened(Rescuable target, boolean active);
}
