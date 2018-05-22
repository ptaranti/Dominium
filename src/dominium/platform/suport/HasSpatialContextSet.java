package dominium.platform.suport;

import jade.content.Predicate;
import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class HasSpatialContextSet implements Predicate {

	private AID regulatedAgent;
	private List spatialContextSet;

	public HasSpatialContextSet() {
		this.regulatedAgent = new AID();
		this.spatialContextSet = new ArrayList();
	}

	public List getSpatialContextSet() {
		return spatialContextSet;
	}

	public void setSpatialContextSet(List spatialContextSet) {
		this.spatialContextSet = spatialContextSet;
	}

	public AID getRegulatedAgent() {
		return regulatedAgent;
	}

	public void setRegulatedAgent(AID regulatedAgent) {
		this.regulatedAgent = regulatedAgent;
	}

}