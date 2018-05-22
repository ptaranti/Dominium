package dominium.platform.suport;

import jade.content.Predicate;
import jade.core.AID;

public class HasSituationData implements Predicate {

	private AID regulatedAgent;
	private SituationData situationData;

	public HasSituationData() {
		this.regulatedAgent = new AID();
		this.situationData = new SituationData();
	}

	public AID getRegulatedAgent() {
		return regulatedAgent;
	}

	public void setRegulatedAgent(AID regulatedAgent) {
		this.regulatedAgent = regulatedAgent;
	}

	public SituationData getSituationData() {
		return situationData;
	}

	public void setSituationData(SituationData situationData) {
		this.situationData = situationData;
	}

}
