package dominium.platform.suport;

import jade.content.Predicate;
import jade.core.AID;

public class HasState implements Predicate {

	private AID regulatedAgent;
	private AgentState agentState;

	public HasState() {
		this.regulatedAgent = new AID();
		this.agentState = new AgentState();
	}

	public AID getRegulatedAgent() {
		return regulatedAgent;
	}

	public void setRegulatedAgent(AID regulatedAgent) {
		this.regulatedAgent = regulatedAgent;
	}

	public AgentState getAgentState() {
		return agentState;
	}

	public void setAgentState(AgentState agentState) {
		this.agentState = agentState;
	}

}