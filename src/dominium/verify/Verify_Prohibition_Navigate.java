package dominium.verify;

import jade.core.AID;
import jade.util.leap.List;
import dominium.platform.suport.AgentState;
import dominium.platform.suport.Norm;

public class Verify_Prohibition_Navigate extends Verify {

	public Verify_Prohibition_Navigate(AID aid, AgentState agentState,
			Norm norm, List listInformation) {

		super(aid, agentState, norm, listInformation);

	}

	/**
	 * @return bollean for norm verification (true is compliant to norm)
	 */
	public boolean result() {

		System.out.println("Verificado aderencia de "
				+ super.aid.getLocalName()
				+ " a norma Verify_Prohibition_Navigate - ");

		String str = "OilService";

		for (int i = 0; i < super.listInformation.size(); i++) {

			String inf = (String) super.listInformation.get(i);
			if (str.equals(inf)) {
				System.out
						.println("apesar da área ser proibida a navegação , este agente esta a serviço de uma compania petrolifera, e por isso tem autorização para navegar aqui.");
				return true;
			}

		}
		System.out.println("A norma está sendo violada.");
		return false;
	}
}
