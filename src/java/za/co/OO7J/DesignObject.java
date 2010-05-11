package za.co.OO7J;

public class DesignObject {

	private int designId = 0;

	private String type = "";

	private long buildDate = -1;



	public long getBuildDate() {
		return buildDate;
	}

	public void setBuildDate(long buildDate) {
		this.buildDate = buildDate;
	}

	public int getDesignId() {
		return designId;
	}

	public void setDesignId(int id) {
		this.designId = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
