package strategy.masking;

public class Main {
	public static void main(String[] args) {
		MaskConvertor ms = new ResidentNumber(); 
		
		System.out.println(ms.convert("999999-1231231"));
		
		
	}
}
