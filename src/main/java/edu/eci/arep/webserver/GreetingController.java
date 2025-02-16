package edu.eci.arep.webserver;

@RestController
public class GreetingController {
    @GetMapping("/greeting")
	public static String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		return "Hola " + name;
	}

    @GetMapping("/pi")
	public static String pi() {
		return Double.toString(Math.PI);
	}

	@GetMapping("/")
	public static String index() {
		return "Greetings from Spring Boot!";
	}
		
	@GetMapping("/countChars")
	public static String countChars(@RequestParam(value="word", defaultValue="") String word) {
		return "La palabra '" + word + "' tiene " + word.length() + " caracteres.";
	}


	@GetMapping("/concat")
	public static String concat(@RequestParam(value="a", defaultValue="") String a, 
								@RequestParam(value="b", defaultValue="") String b) {
		return "Resultado: " + a + " " + b;
	}

	@GetMapping("/max")
	public static String max(@RequestParam(value="a", defaultValue="0") String a, 
								@RequestParam(value="b", defaultValue="0") String b, 
								@RequestParam(value="c", defaultValue="0") String c) {
		int numA = Integer.parseInt(a);
		int numB = Integer.parseInt(b);
		int numC = Integer.parseInt(c);
		int max = Math.max(numA, Math.max(numB, numC));
		return "El numero mayor es: " + max;
	}




}
