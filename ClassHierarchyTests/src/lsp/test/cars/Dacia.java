package lsp.test.cars;

public class Dacia extends Car {
	// Dacia is oldish, it can speed up to 80 km/h
	@Override
	public void setSpeed(int speed) {
		if (speed > DACIA_MAX_SPEED)
			speed = 80;
		super.setSpeed(speed);
	}
}
