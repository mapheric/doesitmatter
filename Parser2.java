import java.util.ArrayList;

import java.util.ListIterator;
import java.util.*;

public class Parser2
{
    private ArrayList<Token> tokens;
    private boolean isValid;
    private static Token curToken;
    private static ArrayList<String> operator = new ArrayList<String>();
    private static ArrayList<String> operand1 = new ArrayList<String>();
    private static ArrayList<String> operand2 = new ArrayList<String>();
    private static ArrayList<String> result = new ArrayList<String>();
    private static ListIterator<String> o_rator = operator.listIterator();
    private static ListIterator<String> o_rand_1 = operand1.listIterator();
    private static ListIterator<String> o_rand_2 = operand2.listIterator();
    private static ListIterator<String> o_res = result.listIterator();
    private static int k = 0;			//temp variable master count
    private static int z = 0;			//count for keeping track of the temp variable
    private static int paramCount = 0;	//keep track of how many parameters in a function
    private static String alpha = "";	//for keeping track of the last tokens
    private static String beta = "";
    private static String gamma = "";
    private static String functionName = "";
    private static String temp = "";
    private static int a = 0;			//for keeping track of the last temp variables.	
    private static int b = 0;
    private static int c = 0;
    private static int three = 0;	//keeps track of the current token, for function
    private static int current = 0;	//keeps track of the current token in copy array (below)
    private static int addCount = 0;
    private static Token lastToken; 
    private static ArrayList<String> copy = new ArrayList<String>();
    private static ArrayList<String> copyTokenType = new ArrayList<String>();
    private static int lineNumber = 0;
    private static boolean isAssignment = false;
    private static String assignToThis = "";
    
    private static Stack<String> tempVars = new Stack<String> ();
    


    /**
     * Constructs a new <code>Parser</code>.
     * @param tokens - an <code>ArrayList</code> containing the contents of the
     *                 source program being parsed, which the lexical analyser
     *                 has converted into parsable tokens.
     */
    public Parser2(ArrayList<Token> tokens)
    {
        if(!tokens.isEmpty())
        {
            this.tokens = new ArrayList<Token>(tokens);
            tokens.add(new Token("$", "EOF"));
            this.isValid = true;
            this.curToken = null;
        }
        else
        {
            throw new IllegalArgumentException("Provided token list is null.");
        }
        makeTokenArrayCopy(tokens);
        tempVars.push("$");
        tempVars.push("$");
       // tempVars.push("$");
        
    }

    /**
     * Performs a recursive-descent parse of the provided source code.
     * It will output either "ACCEPT" or "REJECT" to the console,
     * depending on whether the source code's structure adheres to
     * the C- grammar.
     */
    public void parse()
    {
        // Insert first production rule here.
        program();

        if(isValid)
        {
        	print();
            System.out.println("ACCEPT");
        }
        else
        {
            System.out.println("REJECT");
        }

    }

    /*
     * Returns the token corresponding to the parser's current position in
     * the source code. If there are no more tokens to process, getToken()
     * returns an empty Token.
     */
    private Token getToken()
    {    	
        if(!tokens.isEmpty())
        {
            return tokens.get(0);
        }
        else
        {
//        	if(z != 0 || curToken.getToken() != lastToken.getToken()){
//        		System.out.printf("%-35s %-15s %n", curToken.getTokenType(), curToken.getToken());
//        	}
//        	z++;
        	if(getNum() == 1){				/*** if-statements store the current token in oldest of 3 variables**/
    			alpha = curToken.getToken();
    		}else if(getNum() == 2){
    			beta = curToken.getToken();
    		}else{
    			gamma = curToken.getToken();
    		}
        	z++;
        	//original purpose of this else statement
            return new Token("", "");
        }
    }

    /*
     * Removes the token corresponding to the parser's current position in
     * the source code. This method is executed only when the parser
     * encounters a terminal.
     */
    private Token removeToken()
    {
    	current++;
    	//System.out.println("....in removeToken. Current: " + current + ". currentToken: " + curToken.getToken());
        if(!tokens.isEmpty())
        {
//        	if(z != 0) System.out.printf("%-35s %-15s %n", curToken.getTokenType(), curToken.getToken());
//        	z++;
            return tokens.remove(0);
        }
        else
        {
        	//System.out.printf("%-35s %-15s %n", curToken.getTokenType(), curToken.getToken());
            return new Token("", "");
        }
    }

    /*
     * The first production rule of the C- grammar.
     * In BNF, this rule is defined as
     * program -> declaration-list
     */
    private void program()
    {
    	//for formatting of test lines
    	System.out.printf("%-35s %-15s %-15s %n", "Location", "Token", "Details");
    	System.out.println("-------------------------------------------------------------");;
    	System.out.println();
        curToken = getToken();

        // Implement the production.
        if(curToken.getToken().equals("float") || curToken.getToken().equals("int") || curToken.getToken().equals("void"))
        {
            declarationList();
            return;
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The second production rule of the C- grammar.
     * In BNF, this rule is defined as
     * declaration-list -> declaration declaration-list-1
     */
    private void declarationList()
    {
        curToken = getToken();

        // Implement the production.
        if(curToken.getToken().equals("float") || curToken.getToken().equals("int") || curToken.getToken().equals("void"))
        {
            declaration();
            if(isValid)
            {
                declarationList1();
                return;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The third production rule of the C- grammar.
     * In BNF, this rule is defined as
     * declaration-list-1 -> declaration declaration-list-1 | empty
     */
    private void declarationList1()
    {
        curToken = getToken();

        // Implement the epsilon production.
        if(curToken.getToken().equals("$"))
        {
            return;
        }
        // Implement the "declaration declaration-list-1" production.
        else if(curToken.getToken().equals("float") || curToken.getToken().equals("int") || curToken.getToken().equals("void"))
        {
            declaration();
            if(isValid)
            {
                declarationList1();
                return;
            }
        }
    }

    /*
     * The fourth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * declaration -> int ID declaration-1 | float ID declaration-1 | void ID declaration-1
     */
    private void declaration()
    {
        curToken = getToken();

        // Implement the "int ID declaration-1" production.
        if(curToken.getToken().equals("int"))
        {
        	//adds ALLOC if the current token isn't an array, and isn't a function declaration
        	if(!nextToken().equals("[") && !secondToken().equals("[") && !secondToken().equals("(")){
        		add("ALLOC", "4", "", nextToken());
        	}else if(secondToken().equals("(")){
        		add("FUNC", "0", "int", nextToken());//function declaration
        	}
        	trace("declaration.intcheck");
        	
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {
//            	addResult(curToken.getToken());	//add name of int
//            	trace();
            	
                removeToken();
                declaration1();
                return;
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Implement the "float ID declaration-1" production.
        else if(curToken.getToken().equals("float"))
        {
        	//adds ALLOC if the current token isn't an array, and isn't a function declaration
        	if(!nextToken().equals("[") && !secondToken().equals("[") && !secondToken().equals("(")){
        		add("ALLOC", "4", "", nextToken());
        	}else if(secondToken().equals("(")){		//if it's a function..
        		add("FUNC", "0", "float",nextToken());//function declaration
        	}
        	trace("declaration.floatcheck");
        	
        	
        	
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {            	
                removeToken();
                declaration1();
                return;
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Implement the "void ID declaration-1" production.
        else if(curToken.getToken().equals("void"))
        {
        	//---FUNC added below, at the point of the 
        	
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))		//,,,,,,,,,,,,,,,,,,,,TODO: back add params
            {
            	add("FUNC", "0", "void",curToken.getToken());//function declaration
            	trace("declaration.void.IDcheck");
            	
                removeToken();
                declaration1();
                return;
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The fifth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * declaration-1 -> ; | [ INT ] ; | ( params ) compound-stmt
     */
    private void declaration1()
    {
        curToken = getToken();

        // Implement the ";" production.
        if(curToken.getToken().equals(";"))
        {
            removeToken();
            return;
        }
        // Implement the "[ INT ] ;" production.
        else if(curToken.getToken().equals("["))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("INT"))
            {	

            	/***array space calcuation, then alloc**/
            	temp = getTempVar();						//get the temp variable
            	add("MULT", "4", curToken.getToken(), temp);//function declaration
            	add("ALLOC", temp, "", getOldToken(2));		//function declaration
            	
            	trace("declaration_1.[check.INTcheck");
            	
                removeToken();
                curToken = getToken();

                if(curToken.getToken().equals("]"))
                {
                    removeToken();
                    curToken = getToken();

                    if(curToken.getToken().equals(";"))
                    {
                        removeToken();
                        return;
                    }
                    // Invalid program detected.
                    else
                    {
                        isValid = false;
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Implement the "( params ) compound-stmt" production.
        else if(curToken.getToken().equals("("))
        {
        	functionName = getOldToken(1);
        	paramCount = 0;	//reintialize paramCount
        	insertParamCount();
        	
            removeToken();
            params();
            
            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals(")"))
                {    
                	//add("END", "FUNC", "", functionName); 		//------adds end of function statment
                    removeToken();
                    compoundStmt();
                    add("END", "FUNC", "", functionName); 		//------adds end of function statment
                    return;
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The sixth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * params -> int ID param-1 param-list-1 | float ID param-1 param-list-1 | void params-1
     */
    private void params()
    {
        curToken = getToken();

        // Implement the "int ID param-1 param-list-1" production.
        if(curToken.getToken().equals("int"))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {
            	alpha = curToken.getToken();
                removeToken();
                param1();

                if(isValid)
                {
                    paramList1();
                    return;
                }
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Implement the "float ID param-1 param-list-1" production.
        else if(curToken.getToken().equals("float"))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {
                removeToken();
                param1();

                if(isValid)
                {
                    paramList1();
                    return;
                }
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Implement the "void params-1" production.
        else if(curToken.getToken().equals("void"))
        {
            removeToken();
            curToken = getToken();
            params1();
        }
    }

    /*
     * The seventh production rule of the C- grammar.
     * In BNF, this rule is defined as
     * params-1 -> ID param-1 param-list-1 | empty
     */
    private void params1()
    {
        curToken = getToken();

        // Implement epsilon production.
        if(curToken.getToken().equals(")"))
        {
            return;
        }
        // Implement the "ID param-1 param-list-1" production.
        else if(curToken.getTokenType().equals("ID"))
        {
            removeToken();
            param1();

            if(isValid)
            {
                paramList1();
                return;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The eighth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * param-list-1 -> , param-list-2 | empty
     */
    private void paramList1()
    {
        curToken = getToken();

        // Implement epsilon production.
        if(curToken.getToken().equals(")"))
        {									//paramCount++; //-------paramCount at end of param list
            return;
        }
        // Implement the ", param-list-2" production.
        else if(curToken.getToken().equals(","))
        {									//paramCount++; //_------
            removeToken();
            paramList2();
            return;
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The ninth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * param-list-2 -> int ID param-1 param-list-1 | float ID param-1 param-list-1 | void ID param-1 param-list-1
     */
    private void paramList2()
    {
        curToken = getToken();

        // Implement the "int ID param-1 param-list-1" production.
        if(curToken.getToken().equals("int"))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {
                removeToken();
                param1();

                if(isValid)
                {
                    paramList1();
                    return;
                }
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Implement the "float ID param-1 param-list-1" production.
        else if(curToken.getToken().equals("float"))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {
                removeToken();
                param1();

                if(isValid)
                {
                    paramList1();
                    return;
                }
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Implement the "void ID param-1 param-list-1" production.
        if(curToken.getToken().equals("void"))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {
                removeToken();
                param1();

                if(isValid)
                {
                    paramList1();
                    return;
                }
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The tenth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * param-1 -> [ ] | empty
     */
    private void param1()
    {
        curToken = getToken();
        
        // Implement the epsilon production.
        if(curToken.getToken().equals(",") || curToken.getToken().equals(")"))
        {
//        	//paramCount++;
//        	//if the current token is a comma, increment the paramater count;
//        	//   else if end of paramters, then add the count to the table, 
//        	if(curToken.getToken().equals(",")){
//        		//paramCount++;
//        	}else if(curToken.getToken().equals(")")){
//        		//operand1.add(operand1.size()-1, Integer.toString(paramCount));
//        		//paramCount++;
//        	}
            return;
        }
        // Implement the "[ ]" production.
        else if(curToken.getToken().equals("["))
        {
            removeToken();
            curToken = getToken();
            
            if(curToken.getToken().equals("]"))
            {
                removeToken();
                return;
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The eleventh production rule of the C- grammar.
     * In BNF, this rule is defined as
     * compound-stmt -> { local-declarations statement-list }
     */
    private void compoundStmt()
    {
        curToken = getToken();

        // Implement the "{ local-declarations statement-list }" production.
        if(curToken.getToken().equals("{"))
        {
            removeToken();
            localDeclarations();

            if(isValid)
            {
                statementList();

                if(isValid)
                {
                    curToken = getToken();

                    if(curToken.getToken().equals("}"))
                    {
                        removeToken();
                        return;
                    }
                    // Invalid program detected.
                    else
                    {
                        isValid = false;
                    }
                }
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twelfth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * local-declarations -> local-declarations-1
     */
    private void localDeclarations()
    {
        curToken = getToken();

        // Implement the "local-declarations-1" production.
        if(
           curToken.getToken().equals("int") || curToken.getTokenType().equals("ID") || curToken.getToken().equals("float") || curToken.getToken().equals("void") ||
           curToken.getToken().equals(";") || curToken.getTokenType().equals("INT") || curToken.getToken().equals("(") || curToken.getToken().equals("{") ||
           curToken.getToken().equals("}") || curToken.getToken().equals("if") || curToken.getToken().equals("while") || curToken.getToken().equals("int") ||
           curToken.getToken().equals("return") || curToken.getTokenType().equals("FLOAT")
          )
        {
            localDeclarations1();
            return;
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirteenth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * local-declarations-1 -> int ID local-declarations-2 | float local-declarations-2 | void local-declarations-2 | empty
     */
    private void localDeclarations1()
    {
        // TODO Fix this.
        curToken = getToken();

        // Implement the epsilon production.
        if(
           curToken.getTokenType().equals("ID") || curToken.getToken().equals(";") || curToken.getTokenType().equals("INT") || curToken.getToken().equals("(") ||
           curToken.getToken().equals("{") || curToken.getToken().equals("}") || curToken.getToken().equals("while") || curToken.getToken().equals("return") ||
           curToken.getTokenType().equals("FLOAT") || curToken.getToken().equals("if")
          )
        {
            return;
        }
        // Implement the "int ID local-declarations-2" production.
        else if(curToken.getToken().equals("int"))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {
                removeToken();
                localDeclarations2();
                return;
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Implement the "float ID local-declarations-2" production.
        else if(curToken.getToken().equals("float"))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {
                removeToken();
                localDeclarations2();
                return;
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Implement the "void ID local-declarations-2" production.
        else if(curToken.getToken().equals("void"))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getTokenType().equals("ID"))
            {
                removeToken();
                localDeclarations2();
                return;
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The fourteenth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * local-declarations-2 -> ; local-declarations-1 | [ INT ] ; local-declarations-1
     */
    private void localDeclarations2()
    {
        curToken = getToken();
        
        // Implement the "; local-declarations-1" production.
        if(curToken.getToken().equals(";"))
        {
        	/***--------------------void declarations; 
        	 * POSSIBLE ERROR: previous grammar function accepts VOID, so might indicate function in code...?
        	 * **/
        	add("ALLOC", "4", "", getOldToken(1));		
            removeToken();
            localDeclarations1();
            return;
        }
        // Implement the "[ INT ] ; local-declarations-1" production.
        else if(curToken.getToken().equals("["))
        {
            removeToken();
            curToken = getToken();
            
            if(curToken.getTokenType().equals("INT"))
            {
            	/***array space calcuation, then alloc**/
            	temp = getTempVar();						//get the temp variable
            	add("MULT", "4", curToken.getToken(), temp);//function declaration
            	add("ALLOC", temp, "", getOldToken(2));		//function declaration
            	
                removeToken();
                curToken = getToken();
                
                if(curToken.getToken().equals("]"))
                {
                    removeToken();
                    curToken = getToken();
                    
                    if(curToken.getToken().equals(";"))
                    {
                        removeToken();
                        localDeclarations1();
                        return;
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The fifteenth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * statement-list ->  statement-list-1
     */
    private void statementList()
    {
        curToken = getToken();

        // Implement the "statement-list-1" production.
        if(
           curToken.getTokenType().equals("ID") || curToken.getToken().equals(";") || curToken.getTokenType().equals("INT") || curToken.getToken().equals("(") ||
           curToken.getToken().equals("{") || curToken.getToken().equals("}") || curToken.getToken().equals("if") || curToken.getToken().equals("while") ||
           curToken.getToken().equals("return") || curToken.getTokenType().equals("FLOAT")
          )
        {
            statementList1();
            return;
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The sixteenth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * statement-list-1 -> statement statement-list-1 | empty
     */
    private void statementList1()
    {
        curToken = getToken();

        // Implement the epsilon production.
        if(curToken.getToken().equals("}"))
        {
            return;
        }
        // Implement the "statement statement-list-1" production.
        else if(
                curToken.getTokenType().equals("ID") || curToken.getToken().equals(";") || curToken.getTokenType().equals("INT") || curToken.getToken().equals("(") ||
                curToken.getToken().equals("{") || curToken.getToken().equals("if") || curToken.getToken().equals("while") || curToken.getToken().equals("return") ||
                curToken.getTokenType().equals("FLOAT")
               )
        {
            statement();

            if(isValid)
            {
                statementList1();
                return;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The seventeenth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * statement -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt
     */
    private void statement()
    {
        curToken = getToken();

        // Implement the "expression-stmt" production.
        if(
           curToken.getTokenType().equals("ID") || curToken.getToken().equals(";") || curToken.getTokenType().equals("INT") || curToken.getToken().equals("(") ||
           curToken.getTokenType().equals("FLOAT")
          )
        {
        	trace("first if true, inside Statement");	//----------------------variable assignments and function calls in functions
        	
        	if(!curToken.getToken().equals("(") && !curToken.getToken().equals(";")){
        		
        		//System.out.println("curToken.Type: " + curToken.getTokenType());
        		if((curToken.getTokenType().equals("INT") || curToken.getTokenType().equals("FLOAT")) && !nextToken().equals("[")){
        			add("ALLOC", "4", "", copy.get(current + 1));
        		}else if(curToken.getTokenType().equals("ID") && nextToken().equals("=")){
        			assignToThis = curToken.getToken();
        			add("ASSIGN", parseExpression(secondToken(),current + 2), "", assignToThis);
        			
        		}
        	}
        	
            expressionStmt();
            return;
        }
        // Implement the "compound-stmt" production.
        else if(curToken.getToken().equals("{"))
        {
            compoundStmt();
            return;
        }
        // Implement the "selection-stmt" production.
        else if(curToken.getToken().equals("if"))
        {
            selectionStmt();
            return;
        }
        // Implement the "iteration-stmt" production.
        else if(curToken.getToken().equals("while"))
        {
            iterationStmt();
            return;
        }
        // Implement the "return-stmt" production.
        else if(curToken.getToken().equals("return"))
        {
            returnStmt();
            return;
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The eighteenth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * expression-stmt -> expression ; | ;
     */
    private void expressionStmt()
    {
        curToken = getToken();

        // Implement the "expression ;" production.
        if(curToken.getTokenType().equals("ID") || curToken.getTokenType().equals("INT") || curToken.getToken().equals("(") || curToken.getTokenType().equals("FLOAT"))
        {
//        	if(!curToken.getToken().equals("(")){
//        		//TODO: if not LP, then it's either a variable declration, or a statement-----------------------
//        		trace("is there anything here?");
//        		if((curToken.getTokenType().equals("INT") || curToken.getTokenType().equals("FLOAT")) && !nextToken().equals("[")){
//        			add("ALLOC", "4", "", copy.get(current + 1));
//        			trace("^^^^^in EXPRESSIONSTMT..., firt if");
//        		}else if(curToken.getTokenType().equals("ID") && nextToken().equals("=")){
//        			
//        		}
//        	}
        	//parseExpression(curToken.getToken(), current);/////-----------------------------------
            expression();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals(";"))
                {
                    removeToken();
                    return;
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the ";" production.
        else if(curToken.getToken().equals(";"))
        {
            removeToken();
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The nineteenth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * selection-stmt -> if ( expression ) statement selection-stmt-1
     */
    private void selectionStmt()
    {
        curToken = getToken();

        // Implement the "if ( expression ) statement selection-stmt-1" production.
        if(curToken.getToken().equals("if"))
        {
            removeToken();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals("("))
                {
                    removeToken();
                    expression();

                    if(isValid)
                    {
                        curToken = getToken();

                        if(curToken.getToken().equals(")"))
                        {
                            removeToken();
                            statement();
                            
                            if(isValid)
                            {
                                selectionStmt1();
                                return;
                            }
                        }
                        // Invalid program detected.
                        else
                        {
                            isValid = false;
                        }
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twentieth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * selection-stmt-1 -> else statement | empty
     */
    private void selectionStmt1()
    {
        curToken = getToken();
        Token tempToken = (tokens.size() > 1) ? tokens.get(1) : new Token("", "");

        // Implement the epsilon production.
        if(
           curToken.getTokenType().equals("ID") || curToken.getToken().equals(";") || curToken.getTokenType().equals("INT") || curToken.getToken().equals("(") ||
           curToken.getToken().equals("{") || curToken.getToken().equals("}") || curToken.getToken().equals("if") || curToken.getToken().equals("while") || 
           curToken.getToken().equals("return") || curToken.getTokenType().equals("FLOAT")
          )
        {
            return;
        }
        // Implement the else productions (both epsilon and "else statement").
        else if(curToken.getToken().equals("else"))
        {
            // Implement the "else statement" productions.
            // This exploit uses the first items of the "statement" non-terminal.
            if(
               tempToken.getTokenType().equals("ID") || tempToken.getToken().equals(";") || tempToken.getTokenType().equals("INT") || tempToken.getToken().equals("(") ||
               tempToken.getToken().equals("{") || tempToken.getToken().equals("if") || tempToken.getToken().equals("while") || tempToken.getToken().equals("return") ||
               tempToken.getTokenType().equals("FLOAT")
              )
            {
                removeToken();
                statement();
                return;
            }
            // Implement the epsilon production.
            else
            {
                return;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twenty-first production rule of the C- grammar.
     * In BNF, this rule is defined as
     * iteration-stmt -> while ( expression ) statement
     */
    private void iterationStmt()
    {
        curToken = getToken();

        // Implement the "while ( expression ) statement" production.
        if(curToken.getToken().equals("while"))
        {
            removeToken();
            curToken = getToken();

            if(curToken.getToken().equals("("))
            {
                removeToken();
                expression();

                if(isValid)
                {
                    curToken = getToken();

                    if(curToken.getToken().equals(")"))
                    {
                        removeToken();
                        statement();
                        return;
                    }
                    // Invalid program detected.
                    else
                    {
                        isValid = false;
                    }
                }
            }
            // Invalid program detected.
            else
            {
                isValid = false;
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twenty-second production rule of the C- grammar.
     * In BNF, this rule is defined as
     * return-stmt -> return return-stmt-1
     */
    private void returnStmt()
    {
        curToken = getToken();

        // Implement the "return return-stmt-1" production.
        if(curToken.getToken().equals("return"))
        {
            removeToken();
            returnStmt1();
            return;
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twenty-third production rule of the C- grammar.
     * In BNF, this rule is defined as
     * return-stmt-1 -> ; | expression ;
     */
    private void returnStmt1()
    {
        curToken = getToken();

        // Implement the ";" production.
        if(curToken.getToken().equals(";"))
        {
            removeToken();
        }
        // Implement the "expression ;" production.
        else if(curToken.getTokenType().equals("INT") || curToken.getTokenType().equals("ID") || curToken.getTokenType().equals("FLOAT") || curToken.getToken().equals("("))
        {
            expression();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals(";"))
                {
                    removeToken();
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twenty-fourth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * expression -> ID expression-2 | ( expression ) term-1 additive-expression-1 simple-expression-1 | INT term-1 additive-expression-1 simple-expression-1 | FLOAT term-1 additive-expression-1 simple-expression-1
     */
    private void expression()
    {
        curToken = getToken();
        
        // Implement the "ID expression-2" production.
        if(curToken.getTokenType().equals("ID"))
        {
        	//trace("~~~inside expression(), ID");
            removeToken();
            expression2();
        }
        // Implement the "( expression ) term-1 additive-expression-1 simple-expression-1" production.
        else if(curToken.getToken().equals("("))
        {
            removeToken();
            expression();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals(")"))
                {
                    removeToken();
                    term1();

                    if(isValid)
                    {
                        additiveExpression1();

                        if(isValid)
                        {
                            simpleExpression1();
                        }
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "INT term-1 additive-expression-1 simple-expression-1" production.
        else if(curToken.getTokenType().equals("INT"))
        {
            removeToken();
            term1();

            if(isValid)
            {
                additiveExpression1();

                if(isValid)
                {
                    simpleExpression1();
                }
            }
        }
        // Implement the "FLOAT term-1 additive-expression-1 simple-expression-1" production.
        else if(curToken.getTokenType().equals("FLOAT"))
        {
            removeToken();
            term1();

            if(isValid)
            {
                additiveExpression1();

                if(isValid)
                {
                    simpleExpression1();
                }
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twenty-fifth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * expression-2 -> = expression | [ expression-3 | ( args ) term-1 additive-expression-1 simple-expression-1 | term-1 additive-expression-1 simple-expression-1
     */
    private void expression2()
    {
        curToken = getToken();
        
        // Implement the "= expression" production.
        if(curToken.getToken().equals("="))
        {
            removeToken();
            expression();
            return;
        }
        // Implement the "[ expression-3" production.
        else if(curToken.getToken().equals("["))
        {
            removeToken();
            expression3();
            return;
        }
        // Implement the "( args ) term-1 additive-expression-1 simple-expression-1" production.
        else if(curToken.getToken().equals("("))
        {
            removeToken();
            args();
            
            if(isValid)
            {
                curToken = getToken();
                
                if(curToken.getToken().equals(")"))
                {
                    removeToken();
                    term1();

                    if(isValid)
                    {
                        additiveExpression1();
        
                        if(isValid)
                        {
                            simpleExpression1();
                            return;
                        }
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "term-1 additive-expression-1 simple-expression-1" production.
        else if(
                curToken.getToken().equals(";") || curToken.getToken().equals("]") || curToken.getToken().equals(")") || curToken.getToken().equals(",") ||
                curToken.getToken().equals("<=") || curToken.getToken().equals("<") || curToken.getToken().equals(">") || curToken.getToken().equals(">=") ||
                curToken.getToken().equals("==") || curToken.getToken().equals("!=") || curToken.getToken().equals("+") || curToken.getToken().equals("-") ||
                curToken.getToken().equals("*") || curToken.getToken().equals("/")
               )
        {
            term1();

            if(isValid)
            {
                additiveExpression1();

                if(isValid)
                {
                    simpleExpression1();
                    return;
                }
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twenty-sixth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * expression-3 -> expression ] expression-4
     */
    private void expression3()
    {
        curToken = getToken();
        
        // Implement the "expression ] expression-4" production.
        if(curToken.getTokenType().equals("ID") || curToken.getTokenType().equals("INT") || curToken.getTokenType().equals("FLOAT") || curToken.getToken().equals("("))
        {
            expression();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals("]"))
                {
                    removeToken();
                    expression4();
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twenty-seventh production rule of the C- grammar.
     * In BNF, this rule is defined as
     * expression-4 -> = expression | term-1 additive-expression-1 simple-expression-1
     */
    private void expression4()
    {
        curToken = getToken();
        
        // Implement the "= expression" production.
        if(curToken.getToken().equals("="))
        {
            removeToken();
            expression();
        }
        // Implement the "term-1 additive-expression-1 simple-expression-1" production.
        else if(
                curToken.getToken().equals(";") || curToken.getToken().equals("]") || curToken.getToken().equals(")") || curToken.getToken().equals(",") ||
                curToken.getToken().equals("<=") || curToken.getToken().equals("<") || curToken.getToken().equals(">") || curToken.getToken().equals(">=") ||
                curToken.getToken().equals("==") || curToken.getToken().equals("!=") || curToken.getToken().equals("+") || curToken.getToken().equals("-") ||
                curToken.getToken().equals("*") || curToken.getToken().equals("/")
               )
        {
            term1();

            if(isValid)
            {
                additiveExpression1();

                if(isValid)
                {
                    simpleExpression1();
                }
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twenty-eighth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * simple-expression-1 -> relop simple-expression-2 | empty
     */
    private void simpleExpression1()
    {
        curToken = getToken();
        
        // Implement the epsilon production.
        if(curToken.getToken().equals(",") || curToken.getToken().equals(";") || curToken.getToken().equals("]") || curToken.getToken().equals(")"))
        {
            return;
        }
        // Implement the "relop simple-expression-2" production.
        else if(
           curToken.getToken().equals("<") || curToken.getToken().equals("<=") || curToken.getToken().equals(">") || curToken.getToken().equals(">=") || 
           curToken.getToken().equals("==") || curToken.getToken().equals("!=")
          )
        {
            relop();
            
            if(isValid)
            {
                simpleExpression2();
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The twenty-ninth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * simple-expression-2 -> ( expression ) term-1 additive-expression-1 | INT term-1 additive-expression-1 | FLOAT term-1 additive-expression-1 | ID simple-expression-3
     */
    private void simpleExpression2()
    {
        curToken = getToken();

        // Implement the "( expression ) term-1 additive-expression-1" production.
        if(curToken.getToken().equals("("))
        {
            removeToken();
            expression();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals(")"))
                {
                    term1();

                    if(isValid)
                    {
                        additiveExpression1();
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "INT term-1 additive-expression-1" production.
        else if(curToken.getTokenType().equals("INT"))
        {
            removeToken();
            term1();

            if(isValid)
            {
                additiveExpression1();
            }
        }
        // Implement the "FLOAT term-1 additive-expression-1" production.
        else if(curToken.getTokenType().equals("FLOAT"))
        {
            removeToken();
            term1();

            if(isValid)
            {
                additiveExpression1();
            }
        }
        // Implement the "ID simple-expression-3" production.
        else if(curToken.getTokenType().equals("ID"))
        {
            removeToken();
            simpleExpression3();
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirtieth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * simple-expression-3 -> [ expression ] term-1 additive-expression-1 | ( args ) term-1 additive-expression-1 | term-1 additive-expression-1
     */
    private void simpleExpression3()
    {
        curToken = getToken();

        // Implement the "[ expression ] term-1 additive-expression-1" production.
        if(curToken.getToken().equals("["))
        {
            removeToken();
            expression();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals("]"))
                {
                    removeToken();
                    term1();

                    if(isValid)
                    {
                        additiveExpression1();
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "( args ) term-1 additive-expression-1" production.
        else if(curToken.getToken().equals("("))
        {
            removeToken();
            args();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals(")"))
                {
                    removeToken();
                    term1();

                    if(isValid)
                    {
                        additiveExpression1();
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "term-1 additive-expression-1" production.
        else if(
                curToken.getToken().equals("*") || curToken.getToken().equals("/") || curToken.getToken().equals("+") || curToken.getToken().equals("-") ||
                curToken.getToken().equals(")") || curToken.getToken().equals(",") || curToken.getToken().equals("]") || curToken.getToken().equals(";")
               )
        {
            term1();

            if(isValid)
            {
                additiveExpression1();
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirty-first production rule of the C- grammar.
     * In BNF, this rule is defined as
     * relop -> <= | < | > | >= | == | !=
     */
    private void relop()
    {
        curToken = getToken();

        // Implement the "<=" production.
        if(curToken.getToken().equals("<="))
        {
            removeToken();
        }
        // Implement the "<" production.
        else if(curToken.getToken().equals("<"))
        {
            removeToken();
        }
        // Implement the ">" production.
        else if(curToken.getToken().equals(">"))
        {
            removeToken();
        }
        // Implement the ">=" production.
        else if(curToken.getToken().equals(">="))
        {
            removeToken();
        }
        // Implement the "==" production.
        else if(curToken.getToken().equals("=="))
        {
            removeToken();
        }
        // Implement the "!=" production.
        else if(curToken.getToken().equals("!="))
        {
            removeToken();
        }
        // Invalid program detected
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirty-second production rule of the C- grammar.
     * In BNF, this rule is defined as
     * additive-expression-1 -> addop additive-expression-2 | empty
     */
    private void additiveExpression1()
    {
        curToken = getToken();

        // Implement epsilon production.
        if(
           curToken.getToken().equals(";") || curToken.getToken().equals("]") || curToken.getToken().equals(")") || curToken.getToken().equals(",") ||
           curToken.getToken().equals("<=") || curToken.getToken().equals("<") || curToken.getToken().equals(">") || curToken.getToken().equals(">=") ||
           curToken.getToken().equals("==") || curToken.getToken().equals("!=")
          )
        {
            return;
        }
        // Implement "addop additive-expression-2" production.
        else if(curToken.getToken().equals("+") || curToken.getToken().equals("-"))
        {
            addop();

            if(isValid)
            {
                additiveExpression2();
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirty-third production rule of the C- grammar.
     * In BNF, this rule is defined as
     * additive-expression-2 -> ( expression ) term-1 additive-expression-1 | INT term-1 additive-expression-1 | FLOAT term-1 additive-expression-1 | ID additive-expression-3
     */
    private void additiveExpression2()
    {
        curToken = getToken();
        
        // Implement "( expression ) term-1 additive-expression-1" production.
        if(curToken.getToken().equals("("))
        {
            removeToken();
            expression();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals(")"))
                {
                    term1();

                    if(isValid)
                    {
                        additiveExpression1();
                    }
                }
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement "INT term-1 additive-expression-1" production.
        else if(curToken.getTokenType().equals("INT"))
        {
            removeToken();
            term1();

            if(isValid)
            {
                additiveExpression1();
            }
        }
        // Implement "FLOAT term-1 additive-expression-1" production.
        else if(curToken.getTokenType().equals("FLOAT"))
        {
            removeToken();
            term1();

            if(isValid)
            {
                additiveExpression1();
            }
        }
        // Implement "ID additive-expression-3" production.
        else if(curToken.getTokenType().equals("ID"))
        {
            removeToken();
            additiveExpression3();
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirty-fourth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * additive-expression-3 -> [ expression ] term-1 additive-expression-1 | ( args ) term-1 additive-expression-1 | term-1 additive-expression-1
     */
    private void additiveExpression3()
    {
        curToken = getToken();
        
        // Implement the "[ expression ] term-1 additive-expression-1" production.
        if(curToken.getToken().equals("["))
        {
            removeToken();
            expression();
            
            if(isValid)
            {
                curToken = getToken();
                
                if(curToken.getToken().equals("]"))
                {
                    removeToken();
                    term1();
                    
                    if(isValid)
                    {
                        additiveExpression1();
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "( args ) term-1 additive-expression-1" production.
        else if(curToken.getToken().equals("("))
        {
            removeToken();
            args();
            
            if(isValid)
            {
                curToken = getToken();
                
                if(curToken.getToken().equals(")"))
                {
                    removeToken();
                    term1();
                    
                    if(isValid)
                    {
                        additiveExpression1();
                    }
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "term-1 additive-expression-1" production.
        else if(
                curToken.getToken().equals(";") || curToken.getToken().equals("]") || curToken.getToken().equals(")") || curToken.getToken().equals(",") ||
                curToken.getToken().equals(";") || curToken.getToken().equals("<") || curToken.getToken().equals("<=") || curToken.getToken().equals(">") ||
                curToken.getToken().equals(">=") || curToken.getToken().equals("==") || curToken.getToken().equals("!=") || curToken.getToken().equals("+") ||
                curToken.getToken().equals("-") || curToken.getToken().equals("*") || curToken.getToken().equals("/")
               )
        {
            term1();

            if(isValid)
            {
                additiveExpression1();
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirty-fifth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * addop -> + | -
     */
    private void addop()
    {
        curToken = getToken();

        // Implement the "+" production.
        if(curToken.getToken().equals("+"))
        {
        	
            removeToken();
        }
        // Implement the "-" production.
        else if(curToken.getToken().equals("-"))
        {
            removeToken();
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirty-sixth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * term-1 -> mulop term-2 | EPSILON
     */
    private void term1()
    {
        curToken = getToken();

        // Implement the epsilon production.
        if(
           curToken.getToken().equals(";") || curToken.getToken().equals("]") || curToken.getToken().equals(")") || curToken.getToken().equals(",") ||
           curToken.getToken().equals("<=") || curToken.getToken().equals("<") || curToken.getToken().equals(">=") || curToken.getToken().equals(">") ||
           curToken.getToken().equals("==") || curToken.getToken().equals("!=") || curToken.getToken().equals("+") || curToken.getToken().equals("-")
          )
        {
            return;
        }
        // Implement the "mulop term-2" production.
        else if(curToken.getToken().equals("*") || curToken.getToken().equals("/"))
        {
            mulop();

            if(isValid)
            {
                term2();
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirty-seventh production rule of the C- grammar.
     * In BNF, this rule is defined as
     * term-2 -> ( expression ) term-1 | INT term-1 | FLOAT term-1 | ID term-3
     */
    private void term2()
    {
        curToken = getToken();
        
        // Implement the "( expression ) term-1" production.
        if(curToken.getToken().equals("("))
        {
            removeToken();
            expression();
            
            if(isValid)
            {
                curToken = getToken();
                
                if(curToken.getToken().equals(")"))
                {
                    removeToken();
                    term1();
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "INT term-1" production.
        else if(curToken.getTokenType().equals("INT"))
        {
            removeToken();
            term1();
        }
        // Implement the "FLOAT term-1" production.
        else if(curToken.getTokenType().equals("FLOAT"))
        {
            removeToken();
            term1();
        }
        // Implement the "ID term-3" production.
        else if(curToken.getTokenType().equals("ID"))
        {
            removeToken();
            term3();
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirty-eighth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * term-3 -> [ expression ] term-1 | ( args ) term-1 | term-1
     */
    private void term3()
    {
        // TODO Fix this
        curToken = getToken();

        // Implement the "[ expression ] term-1" production.
        if(curToken.getToken().equals("["))
        {
            removeToken();
            expression();
            
            if(isValid)
            {
                curToken = getToken();
                
                if(curToken.getToken().equals("]"))
                {
                    removeToken();
                    term1();
                    return;
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "( args ) term-1" production.
        else if(curToken.getToken().equals("("))
        {
            removeToken();
            args();
            
            if(isValid)
            {
                curToken = getToken();
                
                if(curToken.getToken().equals(")"))
                {
                    removeToken();
                    term1();
                    return;
                }
                // Invalid program detected.
                else
                {
                    isValid = false;
                }
            }
        }
        // Implement the "term-1" production.
        else if(
                curToken.getToken().equals(";") || curToken.getToken().equals("]") || curToken.getToken().equals(")") || curToken.getToken().equals(",") ||
                curToken.getToken().equals("<=") || curToken.getToken().equals("<") || curToken.getToken().equals(">=") || curToken.getToken().equals(">") ||
                curToken.getToken().equals("==") || curToken.getToken().equals("!=") || curToken.getToken().equals("+") || curToken.getToken().equals("-") ||
                curToken.getToken().equals("*") || curToken.getToken().equals("/")
               )
        {
            term1();
            return;
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The thirty-ninth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * mulop -> * | /
     */
    private void mulop()
    {
        curToken = getToken();
        
        // Implement the "*" production.
        if(curToken.getToken().equals("*"))
        {
            removeToken();
        }
        // Implement the "/" production.
        else if(curToken.getToken().equals("/"))
        {
            removeToken();
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The fortieth production rule of the C- grammar.
     * In BNF, this rule is defined as
     * args -> arg-list | empty
     */
    private void args()
    {
        curToken = getToken();
        
        // Implement the epsilon production.
        if(curToken.getToken().equals(")"))
        {
            return;
        }
        // Implement the "arg-list" production.
        else if(curToken.getTokenType().equals("ID") || curToken.getTokenType().equals("INT") || curToken.getTokenType().equals("FLOAT") || curToken.getToken().equals("("))
        {
            argList();
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The forty-first production rule of the C- grammar.
     * In BNF, this rule is defined as
     * arg-list -> expression arg-list-1
     */
    private void argList()
    {
        curToken = getToken();

        // Implement the "expression arg-list-1" production.
        if(curToken.getTokenType().equals("ID") || curToken.getTokenType().equals("INT") || curToken.getTokenType().equals("FLOAT") || curToken.getToken().equals("("))
        {
            expression();

            if(isValid)
            {
                argList1();
            }
        }
        // Invalid program detected.
        else
        {
            isValid = false;
        }
    }

    /*
     * The forty-second production rule of the C- grammar.
     * In BNF, this rule is defined as
     * arg-list-1 -> , expression arg-list-1 | EPSILON
     */
    private void argList1()
    {
        curToken = getToken();

        // Implement the epsilon production.
        if(curToken.getToken().equals(")"))
        {
            return;
        }
        // Implement the ", expression arg-list-1" production.
        else if(curToken.getToken().equals(","))
        {
            removeToken();
            expression();

            if(isValid)
            {
                argList1();
            }
        }
    }
    
    //-----------------------------------------------------------------------------------
    public static void addPrevious(String oper, String op1, String op2, String res){
    	int place = operator.size() - 1;    	
    	operator.add(place, oper);
    	operand1.add(place, op1);
    	operand2.add(place, op2);
    	result.add(place, res);
    }
    public static void addPrevious(String oper, String op1, String op2){
    	int place = operator.size() - 1;
    	operator.add(place, oper);
    	operand1.add(place, op1);
    	operand2.add(place, op2);
    }
    public static void addPlace(int place, String oper, String op1, String op2, String res){
    	addCount++;
    	operator.add(place, oper);
    	operand1.add(place, op1);
    	operand2.add(place, op2);
    	result.add(place, res);
    }
    public static void add(String oper, String op1, String op2, String res){
    	addCount++;
    	operator.add(oper);
    	operand1.add(op1);
    	operand2.add(op2);
    	result.add(res);
    }
    public static void add(String oper, String op1, String op2){
    	operator.add(oper);
    	operand1.add(op1);
    	operand2.add(op2);
    }
    public static void addResult(String res){
    	result.add(res);
    }
    public static void addOperator(String res){
    	operator.add(res);
    }
    
    /***insertsParameterCount
     * Called from inside parameters, in grammar
     * First checks to ensure no void or empty params
     * Counts number of tokens in copy, from current position until right paranthases
     * Simultaneasly counts number of commas, indicating multiple parameters; increments paramCount with each
     * Uses token count to look for keywords, and adds those to OutputArray (alloc, etc)
     */
    public static void insertParamCount(){
    	//System.out.println("...current: " + current + "...curToken: " + curToken.getToken() + "...other: " + paramCount);
    	int lll = current;
    	if(!copyTokenType.get(current + 1).equals("RP") && !copy.get(current + 1).equals("void")){		//makes sure the function isn't of type func()
	    	while(!copyTokenType.get(lll).equals("RP")){
	    		//System.out.println("<<<<<<<<   Inside while loop");
	    		if(copy.get(lll).equals(",")){ paramCount++;} 	//if it's a comma, then that's another parameter
	    		lll++;
	    	}
	    	paramCount++;	//because last 
    	}
    	//insert paramcount
    	if(operator.get(operand1.size()-1).equals("FUNC")){
    		operand1.set(operand1.size()-1, Integer.toString(paramCount)); 
    	}
    	
    	for(int a = current; a<lll; a++){
    		if(copyTokenType.get(a).equals("KEYWORD") /**&& !copy.get(a).equals("void")**/){
    			add("PARAM","","","");
    			add("ALLOC", "4", "", copy.get(a+1));    			
    		}
    	}
    }
    
    
    //----------------------------------------------------------------------------
    public static void remove(){
    	operator.remove(operator.size()-1);
    	operand1.remove(operator.size()-1);
    	operand2.remove(operator.size()-1);
    	result.remove(operator.size()-1);
    	//o_rator = operator.listIterator();
//		ListIterator<String> o_rand_1 = operand1.listIterator();
//		ListIterator<String> o_rand_2 = operand2.listIterator();
//		ListIterator<String> o_res = result.listIterator();
    }
    public static void remove3(){
    	operator.remove(operator.size()-1);
    	operand1.remove(operator.size()-1);
    	operand2.remove(operator.size()-1);
    }
    
    
    
    
    public static int getPrevious(){
    	return operator.size() - 1;
    }
    public static String getTempVar(){
    	k++;
    	return "temp_" + String.valueOf(k);
    }
    public static int getNum(){
    	if(three == 3){
    		three = 1;;
    	}else{
    		three++;
    	}
    	return three;
    }
    
    //-------------------------------Print & Trace-----------------------
    public static void print(){
    	System.out.println();
		ListIterator<String> o_rator = operator.listIterator();
		ListIterator<String> o_rand_1 = operand1.listIterator();
		ListIterator<String> o_rand_2 = operand2.listIterator();
		ListIterator<String> o_res = result.listIterator();
		int jjj = 0;
		String or = "";
		String or1 = "";
		String or2 = "";
		String res = "";
//		while(o_rand_1.hasNext()) {
//			or = o_rator.next();
//			or1 = o_rand_1.next();
//			or2 = o_rand_2.next();
//			res = o_res.next();
//			System.out.printf("%-15s %-15s %-15s %-15s %n", or, or1, or2, res);
//			jjj++;
//		}//end of while loop
				
		/***Note: If running two while loops, need to re-zero jjj**/
		System.out.println();
		while(o_rand_1.hasNext()) {
			if(o_rator.hasNext()){or = o_rator.next();}
			if(o_rand_1.hasNext()){or1 = o_rand_1.next();}
			if(o_rand_2.hasNext()){or2 = o_rand_2.next();}
			if(o_res.hasNext()){res = o_res.next();}
			System.out.printf("%-5s %-15s %-15s %-15s %-15s %n",jjj+1 + ".", or, or1, or2, res);
			jjj++;
		}//end of while loop
		//System.out.println("-----" + z);
		    	
    }
    public static void trace(String tracer){
    	//System.out.println("In " + tracer + "--------> " + curToken.getToken());
    	System.out.printf("%-35s %-15s %n", tracer, curToken.getToken());
    }
    public static void trace(){
    	//System.out.println("--------> " + curToken.getToken());
    }
    
    public static void makeTokenArrayCopy(ArrayList <Token> array){
    	Token temp;
    	for(int q = 0; q < array.size(); q++){
    		temp = array.get(q);
    		copy.add(temp.getToken());
    		copyTokenType.add(temp.getTokenType());
    		//System.out.printf("%-5s %n",copy.get(q));
    		System.out.printf("%-5s %-8s %-10s %n", q + ". ", copy.get(q), copyTokenType.get(q));    		
    	}
    }
    public static String secondToken(){
    	//return copy.get(current+2);
    	if(copy.size() >= current +2){
    		//System.out.println("``````````````````" + copy.size());
    		return copy.get(current + 2);
    	}else{
    		return "";
    	}
    }
    public static String nextToken(){
    	if(copy.size() >= current +1){
    		return copy.get(current + 1);
    	}else{
    		return "";
    	}
    }
    public static String getOldToken(int location){
    	if((current-location) >= 0){
    		return copy.get(current - location);
    	}else{
    		return "";
    	}
    }
    public static int getStop(String delimiter){
    	for(int h = current; h<copy.size(); h++){
    		if(copy.get(h).equals(delimiter)){
    			return h;
    		}
    	}
    	return 0;
    }
    
    /***TODO: use expression flag variable?**/
    public static String parseExpression(String id, int place){			//parses expressions
    	String aTempVar = "";
    	//System.out.println("z = " + z + ". Current token: " + copy.get(place) + " at position: " + place + ". Peek: " + tempVars.peek());
    	if(id.equals(";")){
    		return "00";	//error code
    	}else if(copy.get(place + 1).equals(";")){						//if it's the end, return sum of balance
    		System.out.println("z = " + z + ". Current token: " + copy.get(place) + " at position: " + place + ". Peek: " + tempVars.peek());
    		return parseAdd();
    	}else if(copy.get(place + 1).equals("(")){						//if it's a function  
    		return parseFunction(id,place);
    	}else if(id.equals(copy.get(place + 1))){			//if it's the end of a bracketed computation				
    		return parseExpression(id, place + 2);
    	}else if(copy.get(place + 1).equals("[")){
    		return parseArr(id, place);
    	}
//    	else if(copyTokenType.get(place+1).equals("ADD")){
////    		aTempVar = getTempVar();
////    		add("ADD", id, parseExpression(copy.get(place+2), place+2), aTempVar);
////    		return aTempVar;
//    		tempVars.push(id);
//    		tempVars.push(copy.get(place+1));
//    		return parseExpression(copy.get(place + 2), place + 2);
//    	}
    	else if(copyTokenType.get(place+1).equals("MINUS") && copyTokenType.get(place+1).equals("ADD")){
//    		aTempVar = getTempVar();
//    		add("SUB", id, parseExpression(copy.get(place+2), place+2), aTempVar);
//    		return aTempVar;
    		tempVars.push(id);
    		tempVars.push(copy.get(place+1));
    		return parseExpression(copy.get(place + 2), place + 2);
    	}else if(copyTokenType.get(place+1).equals("DIV")){
    		aTempVar = getTempVar();
    		add("MULT", id, parseExpression(copy.get(place+2), place+2), aTempVar);
    		return aTempVar;
    	}else if(copyTokenType.get(place+1).equals("TIMES")){
    		aTempVar = getTempVar();
    		add("DIV", id, parseExpression(copy.get(place+2), place+2), aTempVar);
    		return aTempVar;
    	}else{
    		return id;
    	}
//    	if((copy.size() >= place+2) && copyTokenType.get(place).equals("ID") && copy.get(place + 1).equals("(") && copy.get(place + 2).equals(")")){
//			return getTempVar();    	
//    	}
    }
    
    public static String parseFunction(String id, int place){
    	/***Lots of challenges here. Need to parse
    	 * ---empty functions
    	 * ---functions with computations
    	 * ---functions with multiple inputs (passed in)
    	 * ---functions with mixed inputs - i.e., calculations and variables
    	 * ----- ex: return function(x, y, 5*4-3/6*(4/x=z))
    	 * Takes an id, that's the start of a function
    	 * Triage:start with most common - empty functions, functions with computations, function with inputs
    	 */
    	
    	if(!(copy.size() >= place+2) || !copyTokenType.get(place).equals("ID") || !copy.get(place + 1).equals("(")){
    		return getTempVar();						//if it's not actually a function, return temp var...not useful, just triage for now
    	}
    	String curr = copy.get(place + 2);
    	String aTempVar = "";
    	int countArgs = 1;
    	int cycle = place + 2;
    	if(copy.get(place + 2).equals(")")){		//if it's an empty function 
    		aTempVar = getTempVar();
    		add("CALL", id, "0", aTempVar);
    		return aTempVar; 
    	}
    	while(!curr.equals(")")){
    		if(curr.equals(",")){
    			countArgs++;
    		}
    		cycle++;
    		curr = copy.get(cycle);
    	}
    	aTempVar = getTempVar();
		add("CALL", id, Integer.toString(countArgs), aTempVar);
		return aTempVar; 
    }
    
    public static String parseAdd(){					//parses the addition/subtraction part of an expression

		System.out.println("-----");
    	String bTempVars = "";
    	//String curr = "";
    	System.out.println("This is PEEK: " + tempVars.peek());
    	String curr = tempVars.pop();
//    	if(tempVars.peek().equals("$") && !tempVars.peek().equals(null)){
//    		curr = tempVars.pop();
//    		return "--";
//    	}
    	String id = "";
//    	if(!tempVars.peek().equals("$")){
//    		//end of file
//    		return id;
//    	}
    	while(!curr.equals("$") && !curr.equals(null)){
    		if(curr.equals("+")){
    			bTempVars = getTempVar();
    			curr = tempVars.pop();
    			add("ADD", id, curr, bTempVars);    			
    		}else if(curr.equals("-")){
    			bTempVars = getTempVar();
    			curr = tempVars.pop();
    			add("SUB", id, curr, bTempVars);
    		}else{
    			//System.out.println("z = " + z + ". Peek: " + tempVars.peek());
    			id = curr;
    			curr = tempVars.pop();
    		}
    	}
    	tempVars.add("$");
    	return id;
    }
    public static String parseArr(String id, int place){					//parses an array term, returning the temp value
    	
    	/***array space calculation, then alloc**/
    	String aTempVar = getTempVar();						//get the temp variable
    	String curr = copy.get(place + 2);
    	
    	//if next is [ and next after isn't ] (meaning could be var or int or...)
    	//---then MULT the var by 4 and store in temp;
    	//---then return the tmep
    	if(curr.equals("]")){
    		add("MULT", "4", curToken.getToken(), aTempVar);	//if just id[], allocate space...shouldn't be true
        	add("ALLOC", aTempVar, "", id);						
        	return id;
    	}else{
        	String anotherTempVar = getTempVar();
    		add("MULT", "4", curr, aTempVar);			//calculate the internal value of the given variable
    		add("DISP", id, aTempVar, anotherTempVar);	//displace
    		return anotherTempVar;
    	}
    	
    }
    
}
