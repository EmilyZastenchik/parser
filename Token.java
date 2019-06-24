package parser;


public class Token{
  public int kind;
  public String spelling;
  int line;

  private final static String[] spellings = {
    "<identifier>", "<int_value>", "program", "const", "type", "array", "record", 
    "end", "var", "procedure", "while", "do", "if", "then", 
    "else", "or", "not", "div", "mod", "and", "of", "begin"
    };
  
  public Token(int kind, String spelling, int line){
    this.kind = kind;
    this.spelling = spelling;
    this.line = line;
    for(int k = IDENTIFIER; k <= BEGIN; k++)
      if(spelling.equals(spellings[k])){
        this.kind = (byte)k;
        break;
      }
  }

  public final static byte
    IDENTIFIER    	=  0,
    INT_VALUE     	=  1,
    PROGRAM		  	=  2,
    CONST         	=  3,
    TYPE          	=  4,
    ARRAY         	=  5,
    RECORD        	=  6,
    END           	=  7,
    VAR           	=  8,
    PROCEDURE     	=  9,
    WHILE         	= 10,
    DO            	= 11,
    IF            	= 12,
    THEN          	= 13,
    ELSE          	= 14,
    OR            	= 15,
    NOT           	= 16,
    DIV           	= 17,
    MOD           	= 18,
    AND			  	= 19,
    OF				= 20,
    BEGIN			= 21,

    SEMICOLON     	= 22,	//;
    PERIOD        	= 23,	//.
    ASSIGN        	= 24,	//:=
    LBRACKET	  	= 25,	//[
    RBRACKET      	= 26,	//]
    RANGE		  	= 27,	//..
    COMMA		  	= 28,	//,
    COLON			= 29,	//:
    LPAREN			= 30,	//(
    RPAREN			= 31,	//)
    LBRACE			= 32,	//{
    RBRACE			= 33,	//}
    LESS			= 34,	//<
    EQUAL			= 35,	//=
    GREATER			= 36,	//>
    NOTEQUAL		= 37,	//<>
    PLUS			= 38,	//+
    MINUS			= 39,	//-
    MULTIPLY		= 40,	//*
    EOT           	= 41,   //end of text
    NOTHING       	= 42;   //Needed for the parser
}