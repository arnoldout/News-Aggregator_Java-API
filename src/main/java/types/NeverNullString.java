package main.java.types;

public class NeverNullString{
	private String str;

	public NeverNullString(String str) {
		super();
		if(str!=null)
		{
			this.str = str;
		}
		else{
			str = "";
		}
	}
	public NeverNullString(String str, String fallbackString) {
		super();
		if(str!=null)
		{
			this.str = str;
		}
		else{
			str = fallbackString;
		}
	}

	public String getString() {
		return str;
	}

	public void setString(String str) {
		this.str = str;
	}
	
}
