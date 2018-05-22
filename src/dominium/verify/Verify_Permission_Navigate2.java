package dominium.verify;

import jade.core.AID;
import jade.util.leap.List;
import dominium.platform.suport.AgentState;
import dominium.platform.suport.Norm;

public class Verify_Permission_Navigate2 extends Verify {

	public Verify_Permission_Navigate2(AID aid, AgentState agentState,
			Norm norm, List listInformation) {

		super(aid, agentState, norm, listInformation);

	}

	/**
	 * @return bollean for norm verification (true is compliant to norm)
	 */
	public boolean result() {

		System.out.println("Verificado aderencia de "
				+ super.aid.getLocalName()
				+ " a norma Verify_Permission_Navigate2 - resualtado TRUE");

		return true;
	}
}