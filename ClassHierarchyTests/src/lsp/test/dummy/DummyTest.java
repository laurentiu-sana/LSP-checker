package lsp.test.dummy;


public class DummyTest {
	public int computeArea(Rectangle r, int x, int y) {
		r.setWidth(x);
		r.setHeight(y);
		return r.getArea();
	}
}
