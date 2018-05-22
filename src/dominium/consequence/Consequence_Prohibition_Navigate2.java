package dominium.consequence;

import dominium.platform.ConsequenceAgent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;



public class Consequence_Prohibition_Navigate2 extends OneShotBehaviour {

	ConsequenceAgent consequenceAgent;
	AID aid = new AID();
	

	public Consequence_Prohibition_Navigate2 (ConsequenceAgent consequenceAgent, AID aid){

		this.consequenceAgent = consequenceAgent;
		this.aid = aid;
		

	}

	/**
	 * @return bollean for norm verification (true is compliant to norm)
	 */
	 public void action(){
		 System.out.println("Sendo executadas as consequencias por viola��o de norma por" + aid.getLocalName());
	}


}




