package dominium.verify;

import dominium.platform.suport.AgentState;
import dominium.platform.suport.Norm;
import jade.core.AID;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class Verify {

	AID aid = new AID();
	AgentState agentState = new AgentState();
	Norm norm = new Norm();
	List listInformation = new ArrayList();

	public Verify(AID aid, AgentState agentState, Norm norm,
			List listInformation) {

		this.aid = aid;
		this.agentState = agentState;
		this.norm = norm;
		this.listInformation = listInformation;

	}

	/**
	 * @return boolean for norm verification (true is compliant to norm)
	 */
	public boolean result() {
		return true;
	}

}
