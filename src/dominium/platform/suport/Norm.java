package dominium.platform.suport;

import jade.content.Concept;

public class Norm implements Concept {

	private String normName;
	private int normDelay;
	private int normParameter;
	private String consequenceName;
	private String verifyName;

	public Norm() {

		this.normName = new String();
		this.consequenceName = new String();
		this.verifyName = new String();
		this.normDelay = 0;
		this.normParameter = 0;
	}

	public String getNormName() {
		return normName;
	}

	public void setNormName(String normName) {
		this.normName = normName;
	}

	public String getConsequenceName() {
		return consequenceName;
	}

	public void setConsequenceName(String consequenceName) {
		this.consequenceName = consequenceName;
	}

	public int getNormDelay() {
		return normDelay;
	}

	public void setNormDelay(int normDelay) {
		this.normDelay = normDelay;
	}

	public int getNormParameter() {
		return normParameter;
	}

	public void setNormParameter(int normParameter) {
		this.normParameter = normParameter;
	}

	public String getVerifyName() {
		return verifyName;
	}

	public void setVerifyName(String verifyName) {
		this.verifyName = verifyName;
	}

}
