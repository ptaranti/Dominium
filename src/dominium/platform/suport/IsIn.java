package dominium.platform.suport;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.WKTWriter;

import jade.content.Predicate;
import jade.util.leap.ArrayList;
import jade.util.leap.List;

public class IsIn implements Predicate {

	private String positionWKT;
	private List spatialContextSet;

	public IsIn() {

		this.positionWKT = WKTWriter.toPoint(new Coordinate());
		this.spatialContextSet = new ArrayList();
	}

	public String getPositionWKT() {
		return positionWKT;
	}

	public void setPositionWKT(String positionWKT) {
		this.positionWKT = positionWKT;
	}

	public List getSpatialContextSet() {
		return spatialContextSet;
	}

	public void setSpatialContextSet(List spatialContextSet) {
		this.spatialContextSet = spatialContextSet;
	}

}
