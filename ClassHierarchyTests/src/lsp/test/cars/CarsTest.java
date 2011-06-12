package lsp.test.cars;

import lsp.test.Util;

public class CarsTest {
	public <T extends BMW> void speedUp(Car car, T anotherCar) {
		car.setSpeed(180);
		if (car.getSpeed() != 180) {
			Util.log(car.getClass(), "getSpeed");
		}
		System.out.println(anotherCar.getClass());
	}

	public <T> T foo(T x) {
		return x;
	}

	public <T> void bar(Class<T> ctor) {

	}

	public static void mainLSPChecker(String[] args) {
		Car[] cars = new Car[] { new BMW(), new BMWX5(), new Dacia() };

		for (Car car : cars)
			System.out.println(car.sayYourName());

		(new CarsTest()).speedUp(new BMW(), new BMW());
		(new CarsTest()).speedUp(new BMWX5(), new BMW());
		(new CarsTest()).speedUp(new Dacia(), new BMWX5());
	}
}
