package parser;

/* ?????????????????? in the body of a method means you need to change the production rule based on three method 
 *  that I motioned in class: Eliminating left recursion, left factorization and/or elimination of null string.
 *  There are 8 test programs:
 *  Test2.txt, Test3.txt, Test4.txt, and Test5.txt that grammatically are correct.
 *  Test22.txt, Test33.txt, Test44.txt, and Test55.txt that grammatically are NOT correct.
EBNF of programming language Pascal:
Note: {M} means zero or more Ms.
Note: [M] means zero or one M.
Program = "program" ProgramName ";" BlockBody ".".
BlockBody = [ConstantDefinitionPart] [TypeDefinitionPart] 
            [VariableDefinitionPart] {ProcedureDefinition} 
            CompoundStatement.
ConstantDefinitionPart = "const" ConstantDefinition { ConstantDefinition }.
ConstantDefinition = constantName "=" Constant ";".
Type = ArrayType | RecordType | SimpleType.
ArrayType = "array" "[" IndexRange "]" SimpleType.
IndexRange = Constant ".." Constant.
RecordType = "record" FieldList "end".
FieldList = RecordSection { ";" RecordSection }.
RecordSection = FieldName { "," FieldName  } ":" Type.
VariableDefinitionPart = "var" VariableDefinition {VariableDefinition}.
VariableDefinition = VariableGroup ";".
VariableGroup = VariableName  { "," VariableName } ":" Type.
SimpleType = "integer" | "boolean"
ProcedureDefinition = "procedure" ProcedureName ProcedureBlock ";".
ProcedureBlock = [ "(" FormalParameterList ")" ] ";" BlockBody.
FormalParameterList = ParameterDefinition {";" ParameterDefinition }.
ParameterDefinition = ["var"] VariableGroup. 
Statement = AssignmentStatement | ProcedureStatement | IfStatement
          | WhileStatement | CompoundStatement | Empty.

Note for the parser: Because of: Empty we do not write: new Error(...).
AssignmentStatement = VariableAccess ":=" Expression.
Selector = IndexSelector | FieldSelector.
IndexSelector = "[" Expression "]".
FieldSelector = "." FieldName.
ProcedureStatement = ProcedureName [ "(" ActualParameterList ")" ].
ActualParameterList = ActualParameter {"," ActualParameter}.
ActualParameter = Expression | VariableAccess.
VariableAccess = VariableName {Selector}.
ActualParameter = Expression.
CompoundStatement = "begin" Statement { ";" Statement } "end". 
WhileStatement = "while" Expression "do" Statement. 
IfStatement = "if" Expression "then" Statement [ "else" Statement ].
Expression = SimpleExpression [ RelationalOperator SimpleExpression ]. 
SimpleExpression = [ SignOperator ] Term { AddingOperator Term }.
RelationalOprator = "<" | "=" | ">" | "<>".
SignOperator = "+" | "-".
Term = Factor { MultiplyOperator Factor }. 
AddingOperator = "+" | "-" | "or".
Factor = Constant | VariableAccess | "(" Expression ")" |"not" Factor. 
MultiplyOperator = "*" | "div" | "mod" | "and".
Constant = Numeral | "false" | "true".
*/

public class Parser
{
  private Token currentToken;
  private Lexer lexer;

  private void expect(int expectedToken) {
    if (currentToken.kind == expectedToken)
    	currentToken = lexer.scan();
    else
    	new Error("Syntax Error: " + currentToken.spelling + " is not expected", currentToken.line);
    }

  private void expectIt() 
  {
	  currentToken = lexer.scan();
  }

  public void parse() {
    lexer = new Lexer();
    currentToken = lexer.scan();
    parseProgram();
    if (currentToken.kind != Token.EOT)
      new Error("Syntax error: Redundant characters at the end of program.", currentToken.line);
  }

  //Program = "program" ProgramName ";" BlockBody ".".
  private void parseProgram()
  {
	  expect(Token.PROGRAM);
	  expect(Token.IDENTIFIER);
	  expect(Token.SEMICOLON);
	  parseBlockBody();
	  expect(Token.PERIOD);
  }
  
  //BlockBody = [ ConstantDefinitionPart ][TypeDefinitionPart] 
  	//[ VariableDefinitionPart ] { ProcedureDefinition } CompoundStatement.
  private void parseBlockBody()
  {
	  if(currentToken.kind == Token.CONST)
		  parseConstantDefinitionPart();
	  if(currentToken.kind == Token.TYPE)
		  parseTypeDefinitionPart();
	  if(currentToken.kind == Token.VAR)
		  parseVariableDefinitionPart();
	  while(currentToken.kind == Token.PROCEDURE)
		  parseProcedureDefinition();
	  parseCompoundStatement();
  }
  //N						=		E
  // ConstantDefinitionPart = "const" ConstantDefinition { ConstantDefinition } .
  private void parseConstantDefinitionPart()
  {	 
	 expect(Token.CONST);
	 parseConstantDefinition();
	 while (currentToken.kind == Token.IDENTIFIER)
		 parseConstantDefinition();
  }

  // ConstantDefinition = ConstantName  "=" Constant ";" .
  private void parseConstantDefinition()
  {
	  expect(Token.IDENTIFIER);
	  expect(Token.EQUAL);
	  parseConstant(); 
	  expect(Token.SEMICOLON);
  }
  
//Constant = Numeral | ConstantName.
private void parseConstant()
{
	if(currentToken.kind == Token.INT_VALUE)
		expectIt();
	else if(currentToken.kind == Token.IDENTIFIER)
		expectIt();	
}

//TypeDefinitionPart = "type" TypeDefinition { TypeDefinition } .
 private void parseTypeDefinitionPart()
 {
	 expect(Token.TYPE);
	 parseTypeDefinition();
	 while(currentToken.kind == Token.IDENTIFIER)
		 parseTypeDefinition();
 }

 // TypeDefinition = TypeName "=" NewType ";" .
 private void parseTypeDefinition()
 {
	 expect(Token.IDENTIFIER);
	 expect(Token.EQUAL);
	 parseNewType();
	 expect(Token.SEMICOLON);
 }
  //NewType = NewArrayType | NewRecordType.
  private void parseNewType()
  {
	  if(currentToken.kind == Token.ARRAY)
		  parseNewArrayType();
	  else if(currentToken.kind == Token.RECORD)
		  parseNewRecordType();
  }

  // NewArrayType = "array" "[" IndexRange "]"  "of" TypeName .
  private void parseNewArrayType()
  {
	  expect(Token.ARRAY);
	  expect(Token.LBRACKET);
	  parseIndexRange();
	  expect(Token.RBRACKET);
	  expect(Token.OF);
	  expect(Token.IDENTIFIER);
  }
  
  // IndexRange = Constant ".." Constant .
  private void parseIndexRange()
  {
	  parseConstant();
	  expect(Token.RANGE);
	  parseConstant();
  }
  
//NewRecordType = "record" FieldList "end" .
 private void parseNewRecordType()
 {
	  expect(Token.RECORD);
	  parseFieldList();
	  expect(Token.END);
 }
 
 // FieldList = RecordSection { ";" RecordSection } .
 private void parseFieldList()
 {
	  parseRecordSection();
	  while(currentToken.kind == Token.SEMICOLON)
	  {
		  expectIt();
		  parseRecordSection();  
	  }
 }
 
 // RecordSection = FieldName { "," FieldName  } ":" TypeName .
 private void parseRecordSection()
 {
	  expect(Token.IDENTIFIER);
	  while(currentToken.kind == Token.COMMA) 
	  {
		expectIt();
	  	expect(Token.IDENTIFIER);  
	  }
	  expect(Token.COLON);
	  expect(Token.IDENTIFIER);
 }
 
//VariableDefinitionPart = "var" VariableDefinition { VariableDefinition} .
private void parseVariableDefinitionPart()
{
	  expect(Token.VAR);
	  parseVariableDefinition();
	  while(currentToken.kind == Token.IDENTIFIER)
	  parseVariableDefinition();
}

// VariableDefinition = VariableGroup ";" .
void parseVariableDefinition()
{
	  parseVariableGroup();
	  expect(Token.SEMICOLON);
}

// VariableGroup = VariableName  { "," VariableName } ":" TypeName .
private void parseVariableGroup()
{
	  expect(Token.IDENTIFIER);
	  while(currentToken.kind == Token.COMMA)
	  {
		  expectIt();
		  expect(Token.IDENTIFIER);
	  }
	  expect(Token.COLON);
	  expect(Token.IDENTIFIER);
}

// ProcedureDefinition = "procedure" ProcedureName ProcedureBlock ";" .
void parseProcedureDefinition()
{
	  expect(Token.PROCEDURE);
	  expect(Token.IDENTIFIER);
	  parseProcedureBlock();
	  expect(Token.SEMICOLON);
}

// ProcedureBlock = [ "(" FormalParameterList ")" ] ";" BlockBody.
private void parseProcedureBlock()
{
	  if(currentToken.kind == Token.LPAREN)
	  {
		  expectIt();
		  parseFormalParameterList();
		  expect(Token.RPAREN);
	  }
	  expect(Token.SEMICOLON);
	  parseBlockBody();
}

// FormalParameterList = ParameterDefinition { ";" ParameterDefinition }.
private void  parseFormalParameterList()
{
	  parseParameterDefinition();
	  while(currentToken.kind == Token.SEMICOLON)
	  {
		  expectIt();
		  parseParameterDefinition();
	  }
}

// ParameterDefinition = ["var"] VariableGroup 
private void parseParameterDefinition()
{
	if (currentToken.kind == Token.VAR)
		expect(Token.VAR);
	parseVariableGroup();
	
	
}

//CompoundStatement = "begin" Statement { ";" Statement } "end".
private void parseCompoundStatement()
{
	expect(Token.BEGIN);
	parseStatement();
	while(currentToken.kind == Token.SEMICOLON)
	{
		expectIt();
		parseStatement();
	}
	expect(Token.END);

}

/*Statement = AssignmentStatement | ProcedureStatement | IfStatement | WhileStatement | CompoundStatement | Empty.
*Note: Since we have Empty we do not write else new Error(...)
* /*Statement = [AssignmentStatement | ProcedureStatement | IfStatement | WhileStatement | CompoundStatement].
 */
private void parseStatement()
{//???????????????????
	if(currentToken.kind == Token.IDENTIFIER)//Assign/procedureStatement have same alternative
		parseAssignmentOrProcedureStatement();
	else if(currentToken.kind == Token.IF)
		parseIfStatement();
	else if(currentToken.kind == Token.WHILE)
		parseWhileStatement();
	else if(currentToken.kind == Token.LBRACE)
		parseCompoundStatement();
}

//******AssignmentOrProcedureStatement= AssignmentStatement|ProcedureStatement.(new production rule)
private void parseAssignmentOrProcedureStatement()
{
/*left factorize to simplify
AssignmentStatement = Identifier {Selector} ":=" Expression.
ProcedureStatement = Identifier [ "(" ActualParameterList ")" ].
				N					    = 	  A				 B                   |    A 			C
AssignmentStatementOrProcedureStatement = Identifier {Selector} ":=" Expression. |Identifier [ "(" ActualParameterList ")" ].
				N						=     A		 (		B					 |				C				 )
AssignmentStatementOrProcedureStatement = Identifier ({Selector} ":=" Expression.|[ "(" ActualParameterList ")" ]).
Identifier ({Selector} ":=" Expression. |[ "(" ActualParameterList ")" ]).
*/
	expect(Token.IDENTIFIER);
	while(currentToken.kind == Token.LBRACKET || currentToken.kind == Token.PERIOD)
		parseSelector();
	if(currentToken.kind == Token.ASSIGN)
	{
		expectIt();
		parseExpression();
	}
	else if(currentToken.kind == Token.LPAREN)
	{
	expectIt();
	parseActualParameterList();
	expect(Token.RPAREN);
	}	
}
/* These production rules are not directly used and are never called
// AssignmentStatement = VariableAccess ":=" Expression.
private void parseAssignmentStatement()
{
	  parseVariableAccess();
	  expect(Token.ASSIGN);
	  parseExpression();
}

//ProcedureStatement = ProcedureName [ "(" ActualParameterList ")" ].
private void parseProcedureStatement()
{//??????????????????????????????
	expect(Token.IDENTIFIER);
	if(currentToken.kind == Token.LPAREN)
	{
		expectIt();
		parseActualParameterList();
		expect(Token.RPAREN);

	}
}*/

//  Selector = IndexSelector | FieldSelector.
private void parseSelector()
{
	if(currentToken.kind == Token.LBRACKET)
		parseIndexSelector();
	else if(currentToken.kind == Token.PERIOD)
		parseFieldSelector();
}

//IndexSelector = "[" Expression "]".
private void parseIndexSelector()
{
	expect(Token.LBRACKET);
	parseExpression();
	expect(Token.RBRACKET);
}


// FieldSelector = "." FieldName.
private void parseFieldSelector()
{
	expect(Token.PERIOD);
	expect(Token.IDENTIFIER);
}	

//WhileStatement = "while" Expression "do" Statement.
private void parseWhileStatement()
{
	expect(Token.WHILE);
	parseExpression();
	expect(Token.DO);
	parseStatement();
}

//IfStatement = "if" "Expression "then" Statement [ "else" Statement ].
private void parseIfStatement()
{
	expect(Token.IF);
	parseExpression();
	expect(Token.THEN);
	parseStatement();
	if(currentToken.kind == Token.ELSE)
	{
		expectIt();
		parseStatement();
	}	
}

//ActualParameterList = ActualParameter {"," ActualParameter}.
private void parseActualParameterList()
{
	parseActualParameter();
	while(currentToken.kind == Token.COMMA)
	{
		expectIt();
		parseActualParameter();
	}
}

//ActualParameter = Expression //| VariableAccess.
private void parseActualParameter()
{
		parseExpression();
}

//Expression = SimpleExpression [ RelationalOperator SimpleExpression ].
private void parseExpression()
{
	parseSimpleExpression();
	if(currentToken.kind == Token.LESS || currentToken.kind == Token.EQUAL 
			||currentToken.kind == Token.GREATER || currentToken.kind == Token.NOTEQUAL )
	{
		parseRelationalOperator();
		parseSimpleExpression();
	}	
}

//SimpleExpression = [ SignOperator ] Term { AddingOperator Term }.
private void parseSimpleExpression()
{
	if(currentToken.kind == Token.PLUS || currentToken.kind == Token.MINUS)
		parseSignOperator();
	parseTerm();
	while(currentToken.kind == Token.PLUS || currentToken.kind == Token.MINUS 
			|| currentToken.kind == Token.OR)
	{
		parseAddingOperator();
		parseTerm();
	}
}


//Term = Factor { MultiplyOperator Factor }.
private void parseTerm()
{
	parseFactor();
	while(currentToken.kind == Token.MULTIPLY || currentToken.kind == Token.DIV 
			|| currentToken.kind == Token.MOD || currentToken.kind == Token.AND)
	{
		parseMultiplyOperator();
		parseFactor();
	}
}
//Factor = Constant | VariableAccess | "(" Expression ")" | "not" Factor.
private void parseFactor()
{
	if(currentToken.kind == Token.INT_VALUE ||currentToken.kind == Token.IDENTIFIER)
		parseConstant();
	else if(currentToken.kind == Token.IDENTIFIER && lexer.scan().kind == Token.LBRACKET)
		parseVariableAccess();
	else if (currentToken.kind == Token.LPAREN)
	{
		expectIt();
		parseExpression();
		expect(Token.RPAREN);
	}
	else if(currentToken.kind == Token.NOT)
	{
		expectIt();
		parseFactor();
	}
}

//VariableAccess = VariableName {Selector}.
void parseVariableAccess()
{
	expect(Token.IDENTIFIER);
	while(currentToken.kind == Token.LBRACKET)
		parseSelector();
}

//RelationalOperator = "<" | "=" | ">" | "<>".
private void parseRelationalOperator()
{
	if(currentToken.kind == Token.LESS)
		expectIt();
	else if(currentToken.kind == Token.EQUAL)
		expectIt();	
	else if(currentToken.kind == Token.GREATER)
		expectIt();
	else if(currentToken.kind == Token.NOTEQUAL)
		expectIt();
}

//SignOperator = "+" | "-".
private void parseSignOperator()
{
	if(currentToken.kind == Token.PLUS)
		expectIt();
	else if(currentToken.kind == Token.MINUS)
		expectIt();
}


//AddingOperator = "+" | "-" | "or".
private void parseAddingOperator()
{
	if(currentToken.kind == Token.PLUS)
		expectIt();
	else if(currentToken.kind == Token.MINUS)
		expectIt();
	else if(currentToken.kind == Token.OR)
		expectIt();
}

//MultiplyOperator = "*" | "div" | "mod" | "and".
private void parseMultiplyOperator()
{
	if(currentToken.kind == Token.MULTIPLY)
		expectIt();
	else if(currentToken.kind == Token.DIV)
		expectIt();
	else if(currentToken.kind == Token.MOD)
		expectIt();
	else if(currentToken.kind == Token.AND)
		expectIt();
}

}
