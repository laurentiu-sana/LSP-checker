package lsp.test.cars;

public abstract class Car {
	protected final static Integer DACIA_MAX_SPEED = 80;
	protected final static Integer CAR_MAX_SPEED = 200;
	private int m_speed;

	public void setSpeed(int speed) {
		m_speed = speed;
	}

	public int getSpeed() {
		return m_speed;
	}

	public String sayYourName() {
		return this.getClass().getName();
	}
}
