package dominium.platform.suport;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.WKTWriter;

import jade.content.Predicate;
import jade.core.AID;

public class HasPosition implements Predicate {

	private AID regulatedAgent;
	private String positionWKT;

	public HasPosition() {

		this.regulatedAgent = new AID();
		this.positionWKT = WKTWriter.toPoint(new Coordinate());
	}

	public AID getRegulatedAgent() {
		return regulatedAgent;
	}

	public void setRegulatedAgent(AID regulatedAgent) {
		this.regulatedAgent = regulatedAgent;
	}

	public String getpositionWKT() {
		return positionWKT;
	}

	public void setpositionWKT(String positionWKT) {
		this.positionWKT = positionWKT;
	}

}