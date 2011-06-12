public class Violation2 {
	public String dayOfWeek(java.util.Date date) {
		return date.toString();
	}
	
	public static void main(String[] args) {
		System.out.println("Instance #1: " + new Violation2().dayOfWeek(new java.util.Date()));
		System.out.println("Instance #2: " + new Violation2().dayOfWeek(new java.sql.Date(System.currentTimeMillis())));
	}
}
