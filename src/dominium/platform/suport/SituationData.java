package dominium.platform.suport;

import jade.content.Concept;
import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class SituationData implements Concept {

	private AID regulatedAgent;
	private List normSet;
	private List informationSet;

	public SituationData() {
		this.regulatedAgent = new AID();
		this.normSet = new ArrayList();
		this.informationSet = new ArrayList();
	}

	public List getNormSet() {
		return normSet;
	}

	public void setNormSet(List normSet) {
		this.normSet = normSet;
	}

	public List getInformationSet() {
		return informationSet;
	}

	public void setInformationSet(List informationSet) {
		this.informationSet = informationSet;
	}

	public AID getRegulatedAgent() {
		return regulatedAgent;
	}

	public void setRegulatedAgent(AID regulatedAgent) {
		this.regulatedAgent = regulatedAgent;
	}

}
