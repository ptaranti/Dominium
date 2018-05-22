package dominium.platform.suport;

import jade.content.Predicate;

public class HasSituationData2 implements Predicate {

	private AgentAndSpatialContext agentAndSpatialContext;
	private SituationData situationData2;

	public HasSituationData2() {
		this.agentAndSpatialContext = new AgentAndSpatialContext();
		this.situationData2 = new SituationData();
	}

	public SituationData getSituationData() {
		return situationData2;
	}

	public void setSituationData(SituationData situationData) {
		this.situationData2 = situationData;
	}

	public AgentAndSpatialContext getAgentAndSpatialContext() {
		return agentAndSpatialContext;
	}

	public void setAgentAndSpatialContext(
			AgentAndSpatialContext agentAndSpatialContext) {
		this.agentAndSpatialContext = agentAndSpatialContext;
	}

}
