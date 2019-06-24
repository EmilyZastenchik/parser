package parser;

import java.io.*;
import java.util.Scanner;

public class Lexer{
private char currentChar;
private static int line = 1;
private BufferedReader inFile = null;
private StringBuffer currentSpelling;
private int currentKind;

public Lexer(){
    try{
  	  System.out.print("Enter the file name: ");
  	  Scanner keyboard = new Scanner(System.in);
  	  String FileName = keyboard.next();//"test3.txt";
  	  inFile = new BufferedReader(new FileReader(FileName));
        int i = inFile.read();
        if(i == -1) //end of file
          currentChar = '\u0000';
        else
          currentChar = (char)i;
  	  takeIt();
  	  keyboard.close();
    }catch(Exception e){}
}

private void takeIt(){
  currentSpelling.append(currentChar);
  try{
  	int i = (int)inFile.read();
  	if(i == -1) //end of file
  		currentChar = '\u0000';
  	else
  		currentChar = (char)i;
  }catch(Exception e){}     
}

private void discard(){
	  try{
		  int i = (int)inFile.read();
		  if(i == -1) //end of file
			  currentChar = '\u0000';
	    else
	    	currentChar = (char)i;
	   }catch(Exception e){}
}

public byte scanToken()
{
    switch(currentChar)
    {
      //Replace your switch with this switch
    case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i':
    case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
    case 's': case 't': case 'u': case 'v': case 'w': case 'x': case 'y': case 'z':
    case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I':
    case 'J': case 'K': case 'L': case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
    case 'S': case 'T': case 'U': case 'V': case 'W': case 'X': case 'Y': case 'Z':
      takeIt();
      while(isLetter(currentChar) || isDigit(currentChar))
        takeIt();
      return Token.IDENTIFIER;
    case '0': case '1': case '2': case '3': case '4':
    case '5': case '6': case '7': case '8': case '9':
      takeIt();
      while(isDigit(currentChar))
        takeIt();
      return Token.INT_VALUE;    
    case '+': takeIt(); return Token.PLUS;
    case '-': takeIt(); return Token.MINUS; 
    case '*': takeIt(); return Token.MULTIPLY;
    case '(': takeIt(); return Token.LPAREN;
    case ')': takeIt(); return Token.RPAREN;
    case '[': takeIt(); return Token.LBRACKET;
    case ']': takeIt(); return Token.RBRACKET;
    case '{': takeIt(); return Token.LBRACE;
    case '}': takeIt(); return Token.RBRACE;
    case ';': takeIt(); return Token.SEMICOLON;
    case ',': takeIt(); return Token.COMMA;
    case ':': 
  	 takeIt(); 
  	 if(currentChar == '=') {
  		 takeIt();
  	 return Token.ASSIGN;
  	 }
  	 else return Token.COLON;
    case '.': 
    	takeIt(); 
    	if(currentChar == '.') {
    		takeIt();
    	return Token.RANGE;
    	}
    	else return Token.PERIOD;
    case '\u0000': takeIt(); return Token.EOT;    	  
    case '/':
  	  takeIt(); 
  	  if(currentChar == '/'){
  		  line++;
  		  while(currentChar != '\n' && currentChar != '\u0000')
  		    discard();
  		  if(currentChar == '\n')
  			  discard();
  		  return Token.NOTHING;
  	  }else
  		  return Token.DIV;
    case '<':
        takeIt();
        if(currentChar == '>') {
        	takeIt();
        	return Token.NOTEQUAL;
        }
    /*    if(currentChar == '='){
          takeIt();
        return Token.LEQ; 
        }*/ 
        return Token.LESS;
    case '>':
        takeIt();
       /* if(currentChar == '='){
          takeIt();
         return Token.GEQ;
        }*/ 
        return Token.GREATER;
    case '!':
        takeIt();
        if(currentChar == '='){
          takeIt();
          return Token.NOTEQUAL;
        }
        return Token.NOT;
    case '=':
        takeIt();
        return Token.EQUAL;
        
    case '&':
        takeIt();
        if(currentChar == '&'){
          takeIt();
          return Token.AND;
        }
        else{
      	  new Error("Error: The symbol & is not a token", line);
      	  return Token.NOTHING;
        }
    case '|':
        takeIt();
        if(currentChar == '|'){
          takeIt();
          return Token.OR;
        }
        else {
      	  new Error("Error: The symbol | is not a token", line);
      	  return Token.NOTHING;
        }
    	
      default: return (byte) -22;
    }
}

//  filter out everything that is syntactically unimportant to the compiler
private void scanSeparator()
{
    switch(currentChar)
    {
        //  ignore everything after the comment sign, until '\r' (return?) is reached
        case '$':
            discard();
            while(isGraphic(currentChar))
                discard();
            if(currentChar == '\r')
                discard();

            discard();
            line++;
            break;

        //  ignore all spacing
        case ' ': case '\n': case '\r': case '\t':
            if(currentChar == '\n')
                line++;
            discard();
    }
}

public Token scan()
{
    currentSpelling = new StringBuffer("");

    //  filter out everything that is syntactically unimportant to the compiler
    while(currentChar == '$' || currentChar == ' ' || currentChar == '\n'
      || currentChar == '\r'|| currentChar == '\t')
        scanSeparator();

    //  we've now reached a valid token, so read it in
    currentKind = scanToken();

    //  return a new token object with the necessary info
    return new Token(currentKind, currentSpelling.toString(), line);
}

private boolean isDigit(char c)
{
    //  if 'c' is between 0 and 9 in the ASCII chart bounds, it is a valid digit.
    return '0' <= c && c <= '9';
}

private boolean isLetter(char c)
{
    //  if 'c' is within the ASCII bounds of alphabets, it is a valid letter.
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
}

private boolean isGraphic(char c)
{
    //  if 'c' is within these ASCII chart bounds, it is graphic.
    return c == '\t' || (' ' <= c && c <= '~');
}

/*private boolean isUnderscore(char c)
{
    return c == '_';
}*/
}
