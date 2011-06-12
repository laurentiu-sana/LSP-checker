package lsp.test.dummy;

public class Square extends Rectangle {
	private int width;

	@Override
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getWidth() {
		return width;
	}

	@Override
	public void setHeight(int height) {
		this.width = height;
	}

	public int getHeight() {
		return width;
	}

	@Override
	public int getArea() {
		return width * width;
	}
}
