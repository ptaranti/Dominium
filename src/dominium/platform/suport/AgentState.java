package dominium.platform.suport;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.io.WKTWriter;

import jade.content.Concept;

//Class associated to the AgentState schema

public class AgentState implements Concept {

	private int course;

	private int speed;

	private String positionWKT;

	public AgentState() {

		this.course = 0;
		this.speed = 0;
		this.positionWKT = WKTWriter.toPoint(new Coordinate());
	}

	public int getCourse() {
		return course;
	}

	public void setCourse(int course) {
		this.course = course;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public String getPositionWKT() {
		return positionWKT;
	}

	public void setPositionWKT(String positionWKT) {
		this.positionWKT = positionWKT;
	}

}
