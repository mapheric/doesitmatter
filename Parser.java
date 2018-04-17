import java.util.ArrayList;

public class Parser
{
    private ArrayList<Token> tokens;
    private boolean isValid;
    private Token curToken;

    /**
     * Constructs a new <code>Parser</code>.
     * @param tokens - an <code>ArrayList</code> containing the contents of the
     *                 source program being parsed, which the lexical analyser
     *                 has converted into parsable tokens.
     */
    public Parser(ArrayList<Token> tokens)
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
        if(!tokens.isEmpty())
        {
            return tokens.remove(0);
        }
        else
        {
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
        // Implement the "float ID declaration-1" production.
        else if(curToken.getToken().equals("float"))
        {
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
            removeToken();
            params();

            if(isValid)
            {
                curToken = getToken();

                if(curToken.getToken().equals(")"))
                {
                    removeToken();
                    compoundStmt();
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
        {
            return;
        }
        // Implement the ", param-list-2" production.
        else if(curToken.getToken().equals(","))
        {
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
}