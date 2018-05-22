package neptuno;

import jade.content.ContentManager;
import jade.content.abs.AbsIRE;
import jade.content.abs.AbsPredicate;
import jade.content.abs.AbsTerm;
import jade.content.abs.AbsVariable;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.lang.sl.SLVocabulary;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

import com.hp.hpl.jena.ontology.impl.OntModelImpl;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.WKTWriter;

import dominium.ontology.DominiumOntoModels;
import dominium.ontology.OntoAddress;
import dominium.platform.Vocabulary;
import dominium.platform.suport.AgentState;
import dominium.platform.suport.PlatDominiumOntology;
import dominium.template.NormativeGeoAgent;

public class SeaAgent extends NormativeGeoAgent {

	private ContentManager manager = (ContentManager) getContentManager();
	// This agent "speaks" the SL language
	private Codec codec = new SLCodec();
	// This agent "knows" the PlatDominiumOntology()
	private Ontology ontology = PlatDominiumOntology.getInstance();

	Double speed;
	Double curse;
	Coordinate coord = new Coordinate();

	@Override
	protected void normativeAgentSetup() {

		OntModelImpl ontModel = DominiumOntoModels.getDominiumOntoModel();
		Resource thisAgentRsc = ontModel.getResource(OntoAddress.ontologyNS
				+ this.getLocalName());

		Property spdProp = ontModel.getDatatypeProperty(OntoAddress.ontologyNS
				+ "speed");
		Property crsProp = ontModel.getDatatypeProperty(OntoAddress.ontologyNS
				+ "curse");
		Property posXProp = ontModel.getDatatypeProperty(OntoAddress.ontologyNS
				+ "posX");
		Property posYProp = ontModel.getDatatypeProperty(OntoAddress.ontologyNS
				+ "posY");

		String spdRsc = ((ontModel.getProperty(thisAgentRsc, spdProp))
				.getObject()).toString(); //.asNode().getLocalName();
		spdRsc = spdRsc.substring(0, spdRsc.indexOf("^^"));
		speed = Double.parseDouble(spdRsc);

		String crsRsc = ((ontModel.getProperty(thisAgentRsc, crsProp))
				.getObject()).toString();// .asNode().getLocalName();
		crsRsc = crsRsc.substring(0, crsRsc.indexOf("^^"));
		curse = Double.parseDouble(crsRsc);

		String posXRsc = ((ontModel.getProperty(thisAgentRsc, posXProp))
				.getObject()).toString();// .asNode().getLocalName();
		posXRsc = posXRsc.substring(0, posXRsc.indexOf("^^"));
		coord.x = Double.parseDouble(posXRsc);

		String posYRsc = ((ontModel.getProperty(thisAgentRsc, posYProp))
				.getObject()).toString();// .asNode().getLocalName();
		posYRsc = posYRsc.substring(0, posYRsc.indexOf("^^"));
		coord.y = Double.parseDouble(posYRsc);

		addBehaviour(new SeaAgentCinematic(this, 10000));

		addBehaviour(new QuerySituationData(this, 10000));

	}

	private class QuerySituationData extends TickerBehaviour {
		Agent a;

		public QuerySituationData(Agent a, long period) {
			super(a, period);
			this.a = a;

		}

		public void onTick() {
			try {

				// Prepare the message
				ACLMessage msg = new ACLMessage(ACLMessage.QUERY_REF);
				AID receiver = new AID(Vocabulary.PROVIDER_AGENT,
						AID.ISLOCALNAME);

				msg.setSender(getAID());
				msg.addReceiver(receiver);
				msg.setLanguage(codec.getName());
				msg.setOntology(ontology.getName());

				// Fill the content
				Ontology onto = PlatDominiumOntology.getInstance();
				AbsVariable x = new AbsVariable("x",
						PlatDominiumOntology.HAS_SITUATION_DATA_SITUATION_DATA);

				AbsPredicate hasSituationData = new AbsPredicate(
						PlatDominiumOntology.HAS_SITUATION_DATA);
				hasSituationData.set(
						PlatDominiumOntology.HAS_SITUATION_DATA_AGENT,
						(AbsTerm) onto.fromObject(a.getAID()));
				hasSituationData.set(
						PlatDominiumOntology.HAS_SITUATION_DATA_SITUATION_DATA,
						x);

				AbsIRE iota = new AbsIRE(SLVocabulary.IOTA);
				iota.setVariable(x);
				iota.setProposition(hasSituationData);

				manager.fillContent(msg, iota);
				send(msg);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private class SeaAgentCinematic extends TickerBehaviour {
		SeaAgent a;
		double lastTime;

		public SeaAgentCinematic(SeaAgent a, long period) {
			super(a, period);
			this.a = a;
			lastTime = System.currentTimeMillis();

		}

		public void onTick() {

			double currentTime = System.currentTimeMillis();
			double deltaTime = currentTime - lastTime;

			//calculo de deslocamento
			//tempo em horas
			double deltaTimeHour;
			deltaTimeHour = deltaTime / (1000 * 60 * 60); //tempo em horas
			double curseRad = (curse * (2 * Math.PI)) / 360;
			double deltaSpace = speed * deltaTimeHour;
			Coordinate transCoord = new Coordinate();

			if (curseRad <= (Math.PI / 2)) {
				transCoord.y = deltaSpace * Math.cos(curseRad);
				transCoord.x = deltaSpace * Math.sin(curseRad);
			} else if (curseRad <= (Math.PI)) {
				curseRad = Math.PI - curseRad;
				transCoord.y = (-1) * deltaSpace * Math.cos(curseRad);
				transCoord.x = deltaSpace * Math.sin(curseRad);
			} else if (curseRad <= (Math.PI * 3 / 2)) {
				curseRad = (Math.PI * 3 / 2) - curseRad;
				transCoord.y = (-1) * deltaSpace * Math.sin(curseRad);
				transCoord.x = (-1) * deltaSpace * Math.cos(curseRad);
			} else if (curseRad <= (Math.PI * 2)) {
				curseRad = (Math.PI * 2) - curseRad;
				transCoord.y = deltaSpace * Math.cos(curseRad);
				transCoord.x = (-1) * deltaSpace * Math.sin(curseRad);
			}

			coord.x = coord.x + transCoord.x;
			coord.y = coord.y + transCoord.y;

			a.setAgentCoordinate(coord);

			AgentState agtStt = new AgentState();
			agtStt.setCourse(curse.intValue());
			agtStt.setSpeed(speed.intValue());
			agtStt.setPositionWKT(WKTWriter.toPoint(coord));

			a.setAgentState(agtStt);

		}
	}

}
