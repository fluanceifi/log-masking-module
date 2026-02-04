package strategy.masking;

public class Main {
	public static void main(String[] args) {
		MaskingStrategy ms = new ResidentNumberMaskingStrategy(); 
		
		System.out.println(ms.mask("999999-1231231"));
		
		
	}
}
