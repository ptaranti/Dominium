package dominium.platform.suport;

import jade.content.Predicate;
import jade.core.AID;
import jade.util.leap.ArrayList;

public class Cause implements Predicate {

	private AID regulatedAgent;
	private String consequenceName;

	public Cause() {
		this.regulatedAgent = new AID();
		this.consequenceName = new String();
	}

	public AID getRegulatedAgent() {
		return regulatedAgent;
	}

	public void setRegulatedAgent(AID regulatedAgent) {
		this.regulatedAgent = regulatedAgent;
	}

	public String getConsequenceName() {
		return consequenceName;
	}

	public void setConsequenceName(String consequenceName) {
		this.consequenceName = consequenceName;
	}

}