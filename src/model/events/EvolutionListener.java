package model.events;

import model.units.Unit;

public interface EvolutionListener {
	public void evolve(Unit u, int ev);
}
