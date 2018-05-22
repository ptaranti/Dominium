package dominium.platform.suport;

import jade.content.Concept;
import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class AgentAndSpatialContext implements Concept {

	private AID regulatedAgent;
	private List spatialContextSet;

	public AgentAndSpatialContext() {
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