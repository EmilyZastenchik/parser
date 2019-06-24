package parser;

public class Error{
  public Error(String message, int line){
    System.out.println("Line " + line + ": " + message);
    System.out.println("The grammatical structure of the source program is NOT correct!?!");
    System.exit(0);
  }
 public Error(String message){
	   System.out.println(message);
	   System.exit(0);
 }
}
