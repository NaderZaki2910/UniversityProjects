package model.events;

import model.people.Citizen;
import simulation.Address;
import simulation.Simulatable;

public interface ChangeListener {
	public void newAddress(Address a, Simulatable s);
}
