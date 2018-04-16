import java.util.ArrayList;

public class LexicalAnalyser
{
    
    public static final String INTEGER_REGEX = "[-]?(0|[1-9]+\\d*)";
    public static final String FLOATING_POINT_REGEX = "(\\d*[.])?\\d+(E[+-]?\\d+)?";
    public static final String CHARACTER_REGEX = "[a-zA-Z]+";

    private int depth;  // Keeps track of how many pairs of nested comment symbols are seen.
    private boolean inLineComment;
    private boolean inBlockComment;
    private boolean encounteredError;

    public LexicalAnalyser()
    {
         depth = 0;
         inLineComment = false;
         inBlockComment = false;
         encounteredError = false;
    }

    /**
     * 
     * @param fileContents
     * @return
     */
    public ArrayList<Token> tokenise(ArrayList<String> fileContents)
    {
        ArrayList<Token> tokenList = new ArrayList<Token>();
        ArrayList<Token> lineTokens;

        /*
         * This for loop traverses through each line of text
         * in the source file, as represented in fileContents.
         * Each line of text is displayed on the console before
         * the lexical analyser receives it.
         */
        for(String line : fileContents)
        {
            line = line.trim();
            inLineComment = false;
            //System.out.println("INPUT: " + line);
            lineTokens = getTokens(line);

            if(lineTokens.isEmpty())
            {
                //System.out.println("No tokens in this line.");
            }
            else
            {
                for(Token t : lineTokens)
                {
                    //System.out.println(t.token + " : " + t.tokenType);
                }
            }

            //System.out.println();
            
            tokenList.addAll(lineTokens);
        }

        return tokenList;
    }
    
    
    private ArrayList<Token> getTokens(String line)
    {
        ArrayList<Token> tokens = new ArrayList<Token>();
        
        if(line.isEmpty())
        {
            return tokens;
        }
        
        int curIndex = 0;
        char curChar;
        State curState = State.START;
        String[] candidateTokens = line.split("\\s+");
        
        String candidateToken = "";
        Token curToken;

        for(String s : candidateTokens)
        {
//System.out.println("s: " + s);
            if(inLineComment)
            {
                break;
            }

            while(curState != State.FINAL)
            {
//System.out.println("Current state: " + curState);
                
                switch(curState)
                {
                    case START:
                        curChar = s.charAt(curIndex);
                        if(inBlockComment)
                        {
                            curState = State.BLOCK_COMMENT;
                            if(curChar == '*' || curChar == '/')
                            {
                                candidateToken += curChar;
                            }
                        }
                        else if(Character.isLetter(curChar))
                        {
                            curState = State.CHARACTER;
                            candidateToken += curChar;
                        }
                        else if(Character.isDigit(curChar))
                        {
                            curState = State.INTEGER_NO_EXP;
                            candidateToken += curChar;
                        }
                        else if(curChar == '-')
                        {
                            curState = State.MINUS;
                            candidateToken += curChar;
                        }
                        else if(curChar == '+')
                        {
                            curState = State.PLUS;
                            candidateToken += curChar;
                        }
                        else if(curChar == '/')
                        {
                            curState = State.DIV;
                            candidateToken += curChar;
                        }
                        else if(curChar == '*')
                        {
                            curState = State.TIMES;
                            candidateToken += curChar;
                        }
                        else if(curChar == '(')
                        {
                            curState = State.LP;
                            candidateToken += curChar;
                        }
                        else if(curChar == ')')
                        {
                            curState = State.RP;
                            candidateToken += curChar;
                        }
                        else if(curChar == '[')
                        {
                            curState = State.LB;
                            candidateToken += curChar;
                        }
                        else if(curChar == ']')
                        {
                            curState = State.RP;
                            candidateToken += curChar;
                        }
                        else if(curChar == '{')
                        {
                            curState = State.LBR;
                            candidateToken += curChar;
                        }
                        else if(curChar == '}')
                        {
                            curState = State.LBR;
                            candidateToken += curChar;
                        }
                        else if(curChar == '<')
                        {
                            curState = State.LT;
                            candidateToken += curChar;
                        }
                        else if(curChar == '>')
                        {
                            curState = State.GT;
                            candidateToken += curChar;
                        }
                        else if(curChar == '=')
                        {
                            curState = State.ASSIGN;
                            candidateToken += curChar;
                        }
                        else if(curChar == '!')
                        {
                            curState = State.BANG;
                            candidateToken += curChar;
                        }
                        else if(curChar == ';')
                        {
                            curState = State.SEMI;
                            candidateToken += curChar;
                        }
                        else if(curChar == ',')
                        {
                            curState = State.COMMA;
                            candidateToken += curChar;
                        }
                        else
                        {
                            curState = State.ERROR;
                            candidateToken += curChar;
                        }
                        break;
                    case CHARACTER:
                        // The lexical analyser is currently processing
                        // a token containing alphabetical characters.
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(Character.isLetter(curChar))
                            {
                                // Still in a character string.
                                // Continue building it.
                                candidateToken += curChar;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // Found a digit.
                                // Extract a token from the current character string.
                                curState = State.INTEGER_NO_EXP;
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                            }
                            else if(curChar == '+')
                            {
                                // Found an addition operator.
                                // Extract a token from the current character string,
                                // and transition to the PLUS state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // Found a subtraction operator.
                                // Extract a token from the current character string,
                                // and transition to the MINUS state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // Found a multiplication operator.
                                // Extract a token from the current character string,
                                // and transition to the TIMES state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '/')
                            {
                                // Found a division operator.
                                // Extract a token from the current character string,
                                // and transition to the DIV state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.DIV;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Extract a token from the current character string,
                                // and transition to the ASSIGN state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Extract a token from the current character string,
                                // and transition to the GT state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Extract a token from the current character string,
                                // and transition to the LT state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Extract a token from the current character string,
                                // and transition to the BANG state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Extract a token from the current character string,
                                // and transition to the SEMI state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Extract a token from the current character string,
                                // and transition to the COMMA state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Extract a token from the current character string,
                                // and transition to the RP state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Extract a token from the current character string,
                                // and transition to the LP state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Extract a token from the current character string,
                                // and transition to the LB state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Extract a token from the current character string,
                                // and transition to the RB state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Extract a token from the current character string,
                                // and transition to the LBR state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Extract a token from the current character string,
                                // and transition to the RBR state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Extract a token from the
                                // current character string, and transition
                                // to the RBR state.
                                curToken = extractCharacterToken(candidateToken);
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case INTEGER_NO_EXP:
                        // TODO Integers.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "0|[1-9]+".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(Character.isDigit(curChar))
                            {
                                // Still in an integer string.
                                // Continue building it.
                                candidateToken += curChar;
                            }
                            else if(curChar == 'E')
                            {
                                // This integer string contains an exponent.
                                curState = State.INTEGER_UNSIGNED_EXP_START;
                                candidateToken += curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // Found a letter.
                                // Extract a token from the current number string.
                                curState = State.CHARACTER;
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                //curToken = extractCharacterToken(candidateToken);
                                //tokens.add(curToken);
                                candidateToken = "" + curChar;
                            }
                            else if(curChar == '.')
                            {
                                // May have found a part of a floating-point constant.
                                curState = State.FLOAT_NO_EXP;
                                candidateToken += curChar;
                            }
                            else if(curChar == '+')
                            {
                                // Found an addition operator.
                                // Extract the integer token, and transition
                                // to the PLUS state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // Found a subtraction operator.
                                // Extract the integer token, and transition
                                // to the MINUS state.
                                curToken = new Token(candidateToken, "INT");
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // Found a multiplication operator.
                                // Extract the integer token, and transition
                                // to the TIMES state.
                                curToken = new Token(candidateToken, "INT");
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '/')
                            {
                                // Found a division operator.
                                // Extract the integer token, and transition
                                // to the DIV state.
                                curToken = new Token(candidateToken, "INT");
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                                curState = State.DIV;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Extract the integer token, and transition
                                // to the ASSIGN state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Extract the integer token, and transition
                                // to the GT state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Extract the integer token, and transition
                                // to the LT state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Extract the integer token, and transition
                                // to the BANG state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Extract the integer token, and transition
                                // to the SEMI state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Extract the integer token, and transition
                                // to the COMMA state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Extract the integer token, and transition
                                // to the LP state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Extract the integer token, and transition
                                // to the RP state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Extract the integer token, and transition
                                // to the LB state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Extract the integer token, and transition
                                // to the RB state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Extract the integer token, and transition
                                // to the LBR state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Extract the integer token, and transition
                                // to the RBR state.
                                if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case INTEGER_UNSIGNED_EXP_START:
                        // TODO Floats with no decimal and unsigned exponents.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "0|[1-9]+E".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(curChar == '+' || curChar == '-')
                            {
                                // The exponent is signed.
                                // Continue building it.
                                curState = State.INTEGER_SIGNED_EXP_START;
                                candidateToken += curChar;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // Continue building the exponent.
                                curState = State.INTEGER_UNSIGNED_EXP_F;
                                candidateToken += curChar;
                            }
                            else if(curChar == 'E')
                            {
                                // There cannot be more than one exponent in a
                                // float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(curChar == '.')
                            {
                                // There cannot be a decimal point in the exponent
                                // of a float. Extract the valid integer and report
                                // the remaining string as an error.
                                if(candidateToken.matches(INTEGER_REGEX + "E"))
                                {
                                    String temp = "";
                                    for(int j = 0; j < candidateToken.length(); j++)
                                    {
                                        char c = candidateToken.charAt(j);
                                        if(c != 'E')
                                        {
                                            temp += c;
                                        }
                                    }
                                    candidateToken = temp;
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "E" + curChar;
                                curState = State.ERROR;
                            }
                            else
                            {
                                // The lexical analyser has encountered a valid
                                // float with an invalid exponent. Extract the
                                // non-exponential integer and report the remaining
                                // string as an error.
                                String temp = "";
                                curChar = s.charAt(curIndex);       // Get the sign of the exponent.
                                for(int j = 0; j < candidateToken.length(); j++)
                                {
                                    char c = candidateToken.charAt(j);
                                    if(c == 'E')
                                    {
                                        break;
                                    }
                                    else
                                    {
                                        temp += c;
                                    }
                                }
                                candidateToken = temp;
                                curToken = new Token(candidateToken, "INT");
                                tokens.add(curToken);
                                curState = State.ERROR;
                                candidateToken = "E"+ curChar;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case INTEGER_UNSIGNED_EXP_F:
                        // TODO Floats with no decimal and unsigned exponents.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "0|[1-9]+E[0-9]+".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(Character.isDigit(curChar))
                            {
                                // Continue building the exponent.
                                candidateToken += curChar;
                            }
                            else if(curChar == '+')
                            {
                                // The exponent cannot have a '+' in any
                                // position other than immediately after the
                                // 'E' character. Extract the valid float and 
                                // transition to the PLUS state, since the '+'
                                // represents the addition operator.
                            	if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The exponent cannot have a '-' in any
                                // position other than immediately after the
                                // 'E' character. Extract the valid float and 
                                // transition to the MINUS state, since the '-'
                                // represents the subtraction operator.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // Found a multiplication operator.
                                // Extract the integer token, and transition
                                // to the TIMES state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '/')
                            {
                                // Found a division operator.
                                // Extract the integer token, and transition
                                // to the DIV state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.DIV;
                            }
                            else if(curChar == 'E')
                            {
                                // There cannot be more than one exponent in a
                                // float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(curChar == '.')
                            {
                                // There cannot be a decimal point in the exponent
                                // of a float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case INTEGER_SIGNED_EXP_START:
                        // TODO Floats with no decimal and signed exponents.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "0|[1-9]+E[-+][0-9]".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(curChar == '+' || curChar == '-')
                            {
                            	// The exponent cannot have a '+' or a '-' in any
                                // position other than immediately after the
                                // 'E' character. Extract the valid float and report
                                // the remaining string as an error.
                            	if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // Continue building the exponent.
                                curState = State.INTEGER_SIGNED_EXP_F;
                                candidateToken += curChar;
                            }
                            else if(curChar == 'E')
                            {
                                // There cannot be more than one exponent in a
                                // float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(curChar == '.')
                            {
                                // There cannot be a decimal point in the exponent
                                // of a float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case INTEGER_SIGNED_EXP_F:
                        // TODO Floats with no decimal and signed exponents.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "0|[1-9]+E[-+][0-9]+".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(Character.isDigit(curChar))
                            {
                                // Continue building the exponent.
                                candidateToken += curChar;
                            }
//                            else if(curChar == '+' || curChar == '-')
//                            {
//                                // The exponent cannot have a '+' or a '-' in any
//                                // position other than immediately after the
//                                // 'E' character. Extract the valid float and report
//                                // the remaining string as an error.
//                            	if(candidateToken.matches(FLOATING_POINT_REGEX))
//                                {
//                                    curToken = new Token(candidateToken, "FLOAT");
//                                    tokens.add(curToken);
//                                }
//                                candidateToken = "" + curChar;
//                                curState = State.ERROR;
//                            }
                            else if(curChar == '+')
                            {
                                // Found an addition operator.
                                // Extract the float token, and transition
                                // to the PLUS state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // Found a subtraction operator.
                                // Extract the float token, and transition
                                // to the MINUS state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // Found a multiplication operator.
                                // Extract the float token, and transition
                                // to the TIMES state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '/')
                            {
                                // Found a division operator.
                                // Extract the float token, and transition
                                // to the DIV state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.DIV;
                            }
                            else if(curChar == 'E')
                            {
                                // There cannot be more than one exponent in a
                                // float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(curChar == '.')
                            {
                                // There cannot be a decimal point in the exponent
                                // of a float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character
                                // that is not the 'E' character. Extract the valid
                                // float and transition to the CHARACTER state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case FLOAT_NO_EXP:
                        // TODO Floats with decimals.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "(0|[1-9]+).[0-9]+".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(Character.isDigit(curChar))
                            {
                                // Still in a float string.
                                // Continue building it.
                                candidateToken += curChar;
                            }
                            else if(curChar == 'E')
                            {
                                // This float string contains an exponent.
                                curState = State.FLOAT_UNSIGNED_EXP_START;
                                candidateToken += curChar;
                            }
                            else if(curChar == '.')
                            {
                                // A float cannot contain more than one decimal
                                // point. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(curChar == '+')
                            {
                                // Found an addition operator.
                                // Extract the float token, and transition
                                // to the PLUS state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // Found a subtraction operator.
                                // Extract the float token, and transition
                                // to the MINUS state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // Found a multiplication operator.
                                // Extract the float token, and transition
                                // to the TIMES state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '/')
                            {
                                // Found a division operator.
                                // Extract the float token, and transition
                                // to the DIV state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.DIV;
                            }
                            else
                            {
                                // A float cannot contain any other characters after
                                // the initial decimal point. Extract the valid
                                // float or integer and report the remaining string
                                // as an error.
                                if(candidateToken.matches(INTEGER_REGEX + "."))
                                {
                                    String temp = "";
                                    for(int j = 0; j < candidateToken.length(); j++)
                                    {
                                        char c = candidateToken.charAt(j);
                                        if(c == '.')
                                        {
                                            break;
                                        }
                                        else
                                        {
                                            temp += c;
                                        }
                                    }
                                    candidateToken = temp;
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "." + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            // TODO add corrections for floats of the form "(0|[1-9]+)."
                            if(candidateToken.matches(INTEGER_REGEX + "."))
                            {
                                // A float cannot end with a decimal point.
                                // Extract the valid integer and report the remaining
                                // string as an error.
                                candidateToken = candidateToken.substring(0, candidateToken.length() - 1);
                                curToken = new Token(candidateToken, "INT");
                                tokens.add(curToken);
                                candidateToken = ".";
                                curState = State.ERROR;
                            }
                            else
                            {
                                curState = State.FINAL;
                            }
                        }
                        break;
                    case FLOAT_UNSIGNED_EXP_START:
                        // TODO Floats with decimals and unsigned exponents.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "(0|[1-9]+).[0-9]+E".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(curChar == '+' || curChar == '-')
                            {
                                // The exponent is signed.
                                // Continue building it.
                                curState = State.FLOAT_SIGNED_EXP_START;
                                candidateToken += curChar;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // Continue building the exponent.
                                curState = State.FLOAT_UNSIGNED_EXP_F;
                                candidateToken += curChar;
                            }
                            else if(curChar == 'E')
                            {
                                // There cannot be more than one exponent in a
                                // float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX + "E"))
                                {
                                    String temp = "";
                                    for(int j = 0; j < candidateToken.length(); j++)
                                    {
                                        char c = candidateToken.charAt(j);
                                        if(c != 'E')
                                        {
                                            temp += c;
                                        }
                                    }
                                    candidateToken = temp;
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                    candidateToken = "E" + curChar;
                                    curState = State.ERROR;
                                }
                            }
                            else if(curChar == '.')
                            {
                                // There cannot be a decimal point in the exponent
                                // of a float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches("(0|[1-9]+).[0-9]+E"))
                                {
                                    String temp = "";
                                    for(int j = 0; j < candidateToken.length(); j++)
                                    {
                                        char c = candidateToken.charAt(j);
                                        if(c != 'E')
                                        {
                                            temp += c;
                                        }
                                    }
                                    candidateToken = temp;
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                else if(candidateToken.matches(INTEGER_REGEX + "E"))
                                {
                                    String temp = "";
                                    for(int j = 0; j < candidateToken.length(); j++)
                                    {
                                        char c = candidateToken.charAt(j);
                                        if(c != 'E')
                                        {
                                            temp += c;
                                        }
                                    }
                                    candidateToken = temp;
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            if(candidateToken.matches(FLOATING_POINT_REGEX))
                            {
                                curState = State.FINAL;
                            }
                            else
                            {
                                // The lexical analyser has encountered a valid
                                // float with an invalid exponent. Extract the
                                // non-exponential float and report the remaining
                                // string as an error.
                                String temp = "";
                                for(int j = 0; j < candidateToken.length(); j++)
                                {
                                    char c = candidateToken.charAt(j);
                                    if(c == 'E')
                                    {
                                        break;
                                    }
                                    else
                                    {
                                        temp += c;
                                    }
                                }
                                candidateToken = temp;
                                curToken = new Token(candidateToken, "FLOAT");
                                tokens.add(curToken);
                                candidateToken = "E";
                                curState = State.ERROR;
                            }
                        }
                        break;
                    case FLOAT_UNSIGNED_EXP_F:
                        // TODO Floats with decimals and unsigned exponents.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "(0|[1-9]+).[0-9]+E[0-9]+".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(Character.isDigit(curChar))
                            {
                                // Continue building the exponent.
                                candidateToken += curChar;
                            }
                            // TODO Implement floating-point arithmetic
//                            else if(curChar == '+' || curChar == '-')
//                            {
//                                // The exponent cannot have a '+' or a '-' in any
//                                // position other than immediately after the
//                                // 'E' character. Extract the valid float and report
//                                // the remaining string as an error.
//                            	if(candidateToken.matches(FLOATING_POINT_REGEX))
//                                {
//                                    curToken = new Token(candidateToken, "FLOAT");
//                                    tokens.add(curToken);
//                                }
//                                candidateToken = "" + curChar;
//                                curState = State.ERROR;
//                            }
                            else if(curChar == '+')
                            {
                                // Found an addition operator.
                                // Extract the float token, and transition
                                // to the PLUS state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // Found a subtraction operator.
                                // Extract the float token, and transition
                                // to the MINUS state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // Found a multiplication operator.
                                // Extract the float token, and transition
                                // to the TIMES state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '/')
                            {
                                // Found a division operator.
                                // Extract the float token, and transition
                                // to the DIV state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.DIV;
                            }
                            else if(curChar == 'E')
                            {
                                // There cannot be more than one exponent in a
                                // float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(curChar == '.')
                            {
                                // There cannot be a decimal point in the exponent
                                // of a float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else
                            {
                                // A float cannot contain any other characters after
                                // the initial decimal point. Extract the valid
                                // float and report the remaining string
                                // as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
//                                    String temp = "";
//                                    for(int j = 0; j < candidateToken.length(); j++)
//                                    {
//                                        char c = candidateToken.charAt(j);
//                                        if(c != '.')
//                                        {
//                                            temp += c;
//                                        }
//                                    }
//                                    candidateToken = temp;
//                                    curToken = new Token(candidateToken, "INT");
//                                    tokens.add(curToken);
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case FLOAT_SIGNED_EXP_START:
                        // TODO Floats with decimals and signed exponents.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "(0|[1-9]+).[0-9]+E[-+]".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(curChar == '+' || curChar == '-')
                            {
                            	// The exponent cannot have a '+' or a '-' in any
                                // position other than immediately after the
                                // 'E' character. Extract the valid float and report
                                // the remaining string as an error.
                            	if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // Continue building the exponent.
                                curState = State.INTEGER_SIGNED_EXP_F;
                                candidateToken += curChar;
                            }
                            else if(curChar == 'E')
                            {
                                // There cannot be more than one exponent in a
                                // float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(curChar == '.')
                            {
                                // There cannot be a decimal point in the exponent
                                // of a float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            // The lexical analyser has encountered a valid
                            // float with an invalid exponent. Extract the
                            // non-exponential float and report the remaining
                            // string as an error.
                            String temp = "";
                            curChar = s.charAt(curIndex);       // Get the sign of the exponent.
                            for(int j = 0; j < candidateToken.length(); j++)
                            {
                                char c = candidateToken.charAt(j);
                                if(c == 'E')
                                {
                                    break;
                                }
                                else
                                {
                                    temp += c;
                                }
                            }
                            candidateToken = temp;
                            curToken = new Token(candidateToken, "FLOAT");
                            tokens.add(curToken);
                            curState = State.ERROR;
                            candidateToken = "E"+ curChar;
                        }
                        break;
                    case FLOAT_SIGNED_EXP_F:
                        // TODO Floats with decimals and signed exponents.
                        // The lexical analyser is currently processing
                        // a token containing numerical characters.
                        // This string is defined by the regular
                        // regular expression "(0|[1-9]+).[0-9]+E[-+][0-9]+".
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);

                            if(Character.isDigit(curChar))
                            {
                                // Continue building the exponent.
                                candidateToken += curChar;
                            }
//                            else if(curChar == '+' || curChar == '-')
//                            {
//                                // The exponent cannot have a '+' or a '-' in any
//                                // position other than immediately after the
//                                // 'E' character. Extract the valid float and report
//                                // the remaining string as an error.
//                            	if(candidateToken.matches(FLOATING_POINT_REGEX))
//                                {
//                                    curToken = new Token(candidateToken, "FLOAT");
//                                    tokens.add(curToken);
//                                }
//                                candidateToken = "" + curChar;
//                                curState = State.ERROR;
//                            }
                            // TODO Implement floating-point arithmetic
                            else if(curChar == '+')
                            {
                                // Found an addition operator.
                                // Extract the float token, and transition
                                // to the PLUS state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // Found a subtraction operator.
                                // Extract the float token, and transition
                                // to the MINUS state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // Found a multiplication operator.
                                // Extract the float token, and transition
                                // to the TIMES state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '/')
                            {
                                // Found a division operator.
                                // Extract the float token, and transition
                                // to the DIV state.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.DIV;
                            }
                            else if(curChar == 'E')
                            {
                                // There cannot be more than one exponent in a
                                // float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            else if(curChar == '.')
                            {
                                // There cannot be a decimal point in the exponent
                                // of a float. Extract the valid float and report
                                // the remaining string as an error.
                                if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case MINUS:
                        // TODO Implement '-' recognition
                        // The lexical analyser encountered a '-' character.
                        // It represents the subtraction operation in C-.
                        if(curIndex < s.length() - 1)
                        {
                            curToken = new Token(candidateToken, "MINUS");
                            tokens.add(curToken);
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            
                            if(curChar == '-')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case PLUS:
                        // TODO Implement '+' recognition
                        // The lexical analyser encountered a '+' character.
                        // It represents the addition operation in C-.
                        if(curIndex < s.length() - 1)
                        {
                            curToken = new Token(candidateToken, "PLUS");
                            tokens.add(curToken);
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            
                            if(curChar == '+')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case DIV:
                        //TODO Implement '/' recognition.
                        // The lexical analyser encountered a '/' character.
                        // It represents the division operation in C-.
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == '/')
                            {
                                // Found a single-line comment (i.e. "//"), ignore the rest of the line.
                                curState = State.LINE_COMMENT;
                                inLineComment = true;
                                candidateToken = "";
                            }
                            else if(curChar == '*')
                            {
                                // Found one-half of a standard C comment (i.e. "/*").
                                depth++;
                                inBlockComment = true;
                                curState = State.BLOCK_COMMENT;
                                candidateToken = "";
                            }
                            else
                            {
                                curToken = new Token(candidateToken, "DIV");
                                tokens.add(curToken);
                                
                                if(Character.isLetter(curChar))
                                {
                                    // The lexical analyser found a letter character.
                                    // Transition to the CHARACTER state.
                                    candidateToken = "" + curChar;
                                    curState = State.CHARACTER;
                                }
                                else if(Character.isDigit(curChar))
                                {
                                    // The lexical analyser found a digit.
                                    // Transition to the INTEGER_NO_EXP state.
                                    candidateToken = "" + curChar;
                                    curState = State.INTEGER_NO_EXP;
                                }
                                else if(curChar == '+')
                                {
                                    // The lexical analyser found a '+'.
                                    // Transition to the PLUS state.
                                    candidateToken = "" + curChar;
                                    curState = State.PLUS;
                                }
                                else if(curChar == '-')
                                {
                                    // The lexical analyser found a '-'.
                                    // Transition to the MINUS state.
                                    candidateToken = "" + curChar;
                                    curState = State.MINUS;
                                }
                                else if(curChar == '*')
                                {
                                    // The lexical analyser found a '*'.
                                    // This string represents one-half of a
                                    // C-style comment.
                                    // Transition to the BLOCK_COMMENT state.
                                    candidateToken = "" + curChar;
                                    depth++;
                                    inBlockComment = true;
                                    curState = State.BLOCK_COMMENT;
                                }
                                else if(curChar == '=')
                                {
                                    // The lexical analyser found a '='.
                                    // Transition to the ASSIGN state.
                                    candidateToken = "" + curChar;
                                    curState = State.ASSIGN;
                                }
                                else if(curChar == '>')
                                {
                                    // The lexical analyser found a '>'.
                                    // Transition to the GT state.
                                    candidateToken = "" + curChar;
                                    curState = State.GT;
                                }
                                else if(curChar == '<')
                                {
                                    // The lexical analyser found a '<'.
                                    // Transition to the LT state.
                                    candidateToken = "" + curChar;
                                    curState = State.LT;
                                }
                                else if(curChar == '!')
                                {
                                    // The lexical analyser found a '!'.
                                    // Transition to the BANG state.
                                    candidateToken = "" + curChar;
                                    curState = State.BANG;
                                }
                                else if(curChar == ';')
                                {
                                    // The lexical analyser found a ';'.
                                    // Transition to the SEMI state.
                                    candidateToken = "" + curChar;
                                    curState = State.SEMI;
                                }
                                else if(curChar == ',')
                                {
                                    // The lexical analyser found a ','.
                                    // Transition to the COMMA state.
                                    candidateToken = "" + curChar;
                                    curState = State.COMMA;
                                }
                                else if(curChar == '(')
                                {
                                    // The lexical analyser found a '('.
                                    // Transition to the LP state.
                                    candidateToken = "" + curChar;
                                    curState = State.LP;
                                }
                                else if(curChar == ')')
                                {
                                    // The lexical analyser found a ')'.
                                    // Transition to the RP state.
                                    candidateToken = "" + curChar;
                                    curState = State.RP;
                                }
                                else if(curChar == '[')
                                {
                                    // The lexical analyser found a '['.
                                    // Transition to the LB state.
                                    candidateToken = "" + curChar;
                                    curState = State.LB;
                                }
                                else if(curChar == ']')
                                {
                                    // The lexical analyser found a ']'.
                                    // Transition to the RB state.
                                    candidateToken = "" + curChar;
                                    curState = State.RB;
                                }
                                else if(curChar == '{')
                                {
                                    // The lexical analyser found a '{'.
                                    // Transition to the LBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.LBR;
                                }
                                else if(curChar == '}')
                                {
                                    // The lexical analyser found a '}'.
                                    // Transition to the RBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.RBR;
                                }
                                else
                                {
                                    // The lexical analyser found an undefined
                                    // character. Transition to the ERROR state.
                                    candidateToken = "" + curChar;
                                    curState = State.ERROR;
                                }
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case TIMES:
                        //TODO Implement '*' recognition.
                        // The lexical analyser encountered a '*' character.
                        // It represents the multiplication operation in C-.
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(candidateToken.equals("/"))
                            {
                                // Found one-half of a standard C comment (i.e. "/*").
                                depth++;
                                inBlockComment = true;
                                curState = State.BLOCK_COMMENT;
                                candidateToken = "";
                            }
                            else if(inBlockComment)
                            {
                                if(curChar == '/')
                                {
                                    //
                                    depth--;
                                    if(depth > 0)
                                    {
                                        // Still in a block comment.
                                        // Transition to the BLOCK_COMMENT state.
                                        curState = State.BLOCK_COMMENT;
                                    }
                                    else if(depth == 0)
                                    {
                                        // A complete block comment has been found.
                                        // Transition to the FINAL state.
                                        inBlockComment = false;
                                        curState = State.FINAL;
                                    }
                                }
                            }
                            else
                            {
                                curToken = new Token(candidateToken, "TIMES");
                                tokens.add(curToken);
                                if(curChar == '*')
                                {
                                    // Remain in the current state.
                                    candidateToken = "" + curChar;
                                }
                                else if(Character.isLetter(curChar))
                                {
                                    // The lexical analyser found a letter character.
                                    // Transition to the CHARACTER state.
                                    candidateToken = "" + curChar;
                                    curState = State.CHARACTER;
                                }
                                else if(Character.isDigit(curChar))
                                {
                                    // The lexical analyser found a digit.
                                    // Transition to the INTEGER_NO_EXP state.
                                    candidateToken = "" + curChar;
                                    curState = State.INTEGER_NO_EXP;
                                }
                                else if(curChar == '-')
                                {
                                    // The lexical analyser found a '-'.
                                    // Transition to the MINUS state.
                                    candidateToken = "" + curChar;
                                    curState = State.MINUS;
                                }
                                else if(curChar == '/')
                                {
                                    // The lexical analyser found a '*'.
                                    // Transition to the TIMES state.
                                    candidateToken = "" + curChar;
                                    curState = State.DIV;
                                }
                                else if(curChar == '=')
                                {
                                    // The lexical analyser found a '='.
                                    // Transition to the ASSIGN state.
                                    candidateToken = "" + curChar;
                                    curState = State.ASSIGN;
                                }
                                else if(curChar == '>')
                                {
                                    // The lexical analyser found a '>'.
                                    // Transition to the GT state.
                                    candidateToken = "" + curChar;
                                    curState = State.GT;
                                }
                                else if(curChar == '<')
                                {
                                    // The lexical analyser found a '<'.
                                    // Transition to the LT state.
                                    candidateToken = "" + curChar;
                                    curState = State.LT;
                                }
                                else if(curChar == '!')
                                {
                                    // The lexical analyser found a '!'.
                                    // Transition to the BANG state.
                                    candidateToken = "" + curChar;
                                    curState = State.BANG;
                                }
                                else if(curChar == ';')
                                {
                                    // The lexical analyser found a ';'.
                                    // Transition to the SEMI state.
                                    candidateToken = "" + curChar;
                                    curState = State.SEMI;
                                }
                                else if(curChar == ',')
                                {
                                    // The lexical analyser found a ','.
                                    // Transition to the COMMA state.
                                    candidateToken = "" + curChar;
                                    curState = State.COMMA;
                                }
                                else if(curChar == '(')
                                {
                                    // The lexical analyser found a '('.
                                    // Transition to the LP state.
                                    candidateToken = "" + curChar;
                                    curState = State.LP;
                                }
                                else if(curChar == ')')
                                {
                                    // The lexical analyser found a ')'.
                                    // Transition to the RP state.
                                    candidateToken = "" + curChar;
                                    curState = State.RP;
                                }
                                else if(curChar == '[')
                                {
                                    // The lexical analyser found a '['.
                                    // Transition to the LB state.
                                    candidateToken = "" + curChar;
                                    curState = State.LB;
                                }
                                else if(curChar == ']')
                                {
                                    // The lexical analyser found a ']'.
                                    // Transition to the RB state.
                                    candidateToken = "" + curChar;
                                    curState = State.RB;
                                }
                                else if(curChar == '{')
                                {
                                    // The lexical analyser found a '{'.
                                    // Transition to the LBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.LBR;
                                }
                                else if(curChar == '}')
                                {
                                    // The lexical analyser found a '}'.
                                    // Transition to the RBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.RBR;
                                }
                                else
                                {
                                    // The lexical analyser found an undefined
                                    // character. Transition to the ERROR state.
                                    candidateToken = "" + curChar;
                                    curState = State.ERROR;
                                }
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case ERROR:
                        encounteredError = true;
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            candidateToken += curChar;
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case ASSIGN:
                        // TODO Implement '=' recognition.
                        // The lexical analyser encountered a '=' character.
                        // It represents the assignment operation in C-.
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == '=')
                            {
                                // The lexical analyser found another '=' character.
                                // The token is the equality operator.
                                candidateToken += curChar;
                                curState = State.EQ;
                            }
                            else
                            {
                                // The token is the assignment operator.
                                curToken = new Token(candidateToken, "ASSIGN");
                                tokens.add(curToken);

                                if(Character.isLetter(curChar))
                                {
                                    // The lexical analyser found a letter character.
                                    // Transition to the CHARACTER state.
                                    candidateToken = "" + curChar;
                                    curState = State.CHARACTER;
                                }
                                else if(Character.isDigit(curChar))
                                {
                                    // The lexical analyser found a digit.
                                    // Transition to the INTEGER_NO_EXP state.
                                    candidateToken = "" + curChar;
                                    curState = State.INTEGER_NO_EXP;
                                }
                                else if(curChar == '+')
                                {
                                    // The lexical analyser found a '+'.
                                    // Transition to the PLUS state.
                                    candidateToken = "" + curChar;
                                    curState = State.PLUS;
                                }
                                else if(curChar == '-')
                                {
                                    // The lexical analyser found a '-'.
                                    // Transition to the MINUS state.
                                    candidateToken = "" + curChar;
                                    curState = State.MINUS;
                                }
                                else if(curChar == '*')
                                {
                                    // The lexical analyser found a '*'.
                                    // Transition to the TIMES state.
                                    candidateToken = "" + curChar;
                                    curState = State.TIMES;
                                }
                                else if(curChar == ',')
                                {
                                    // The lexical analyser found a ',' character.
                                    // Transition to the COMMA state.
                                    candidateToken = "" + curChar;
                                    curState = State.COMMA;
                                }
                                else if(curChar == '>')
                                {
                                    // The lexical analyser found a '>'.
                                    // Transition to the GT state.
                                    candidateToken = "" + curChar;
                                    curState = State.GT;
                                }
                                else if(curChar == '<')
                                {
                                    // The lexical analyser found a '<'.
                                    // Transition to the LT state.
                                    candidateToken = "" + curChar;
                                    curState = State.LT;
                                }
                                else if(curChar == '!')
                                {
                                    // The lexical analyser found a '!'.
                                    // Transition to the BANG state.
                                    candidateToken = "" + curChar;
                                    curState = State.BANG;
                                }
                                else if(curChar == ';')
                                {
                                    // The lexical analyser found a ';'.
                                    // Transition to the SEMI state.
                                    candidateToken = "" + curChar;
                                    curState = State.SEMI;
                                }
                                else if(curChar == '(')
                                {
                                    // The lexical analyser found a '('.
                                    // Transition to the LP state.
                                    candidateToken = "" + curChar;
                                    curState = State.LP;
                                }
                                else if(curChar == ')')
                                {
                                    // The lexical analyser found a ')'.
                                    // Transition to the RP state.
                                    candidateToken = "" + curChar;
                                    curState = State.RP;
                                }
                                else if(curChar == '[')
                                {
                                    // The lexical analyser found a '['.
                                    // Transition to the LB state.
                                    candidateToken = "" + curChar;
                                    curState = State.LB;
                                }
                                else if(curChar == ']')
                                {
                                    // The lexical analyser found a ']'.
                                    // Transition to the RB state.
                                    candidateToken = "" + curChar;
                                    curState = State.RB;
                                }
                                else if(curChar == '{')
                                {
                                    // The lexical analyser found a '{'.
                                    // Transition to the LBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.LBR;
                                }
                                else if(curChar == '}')
                                {
                                    // The lexical analyser found a '}'.
                                    // Transition to the RBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.RBR;
                                }
                                else
                                {
                                    // The lexical analyser found an undefined
                                    // character. Transition to the ERROR state.
                                    candidateToken = "" + curChar;
                                    curState = State.ERROR;
                                }
                                candidateToken = "" + curChar;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case BANG:
                        // TODO Implement '!' recognition
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == '=')
                            {
                                // The lexical analyser found another '=' character.
                                // The token is the inequality operator.
                                candidateToken += curChar;
                                curState = State.NE;
                            }
                            else
                            {
                                // The C- grammar prohibits the '!' character from
                                // being alone. Extract any valid tokens and report
                                // an error.
                                if(candidateToken.matches(CHARACTER_REGEX))
                                {
                                    curToken = extractCharacterToken(candidateToken);
                                }
                                else if(candidateToken.matches(INTEGER_REGEX))
                                {
                                    curToken = new Token(candidateToken, "INT");
                                    tokens.add(curToken);
                                }
                                else if(candidateToken.matches(FLOATING_POINT_REGEX))
                                {
                                    curToken = new Token(candidateToken, "FLOAT");
                                    tokens.add(curToken);
                                }
                                curState = State.ERROR;
                                candidateToken = "" + curChar;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case BLOCK_COMMENT:
                        // TODO C-style comments /* ... */
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == '*')
                            {
                                if(candidateToken.equals("/"))
                                {
                                    // The lexical analyser encountered another
                                    // one-half of a C-style comment.
                                    depth++;
                                    candidateToken = "";
                                }
                                else
                                {
                                    // Remain in the current state.
                                    candidateToken = "" + curChar;
                                }
                            }
                            else if(curChar == '/')
                            {
                                if(candidateToken.equals("*"))
                                {
                                    // One complete C-style comment has been found.
                                    depth--;
                                    candidateToken = "";
                                    if(depth > 0)
                                    {
                                        // Still in a block comment.
                                        // Transition to the BLOCK_COMMENT state.
                                        curState = State.BLOCK_COMMENT;
                                    }
                                    else if(depth == 0)
                                    {
                                        // A complete block comment has been found.
                                        // Transition to the FINAL state.
                                        inBlockComment = false;
                                        curState = State.FINAL;
                                    }
                                }
                                else
                                {
                                    // Remain in the current state.
                                    candidateToken = "" + curChar;
                                }
                            }
                            else
                            {
                                // Remain in the current state.
                                candidateToken = "";
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                            candidateToken = "";        // No tokens are present in a block comment.
                        }
                        break;
                    case COMMA:
                        // TODO Commas
                        if(curIndex < s.length() - 1)
                        {
                            curToken = new Token(candidateToken, "COMMA");
                            tokens.add(curToken);
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == ',')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case EQ:
                        // TODO Equality operator
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            curToken = new Token(candidateToken, "EQ");
                            tokens.add(curToken);

                            if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // The lexical analyser has encountered
                                // a '>=' operator. 
                                // Transition to the GE state.
                                candidateToken += curChar;
                                curState = State.GE;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case GE:
                        // TODO Greater-than-or-equal operator
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            curToken = new Token(candidateToken, "GE");
                            tokens.add(curToken);

                            if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // The lexical analyser has encountered
                                // a '>=' operator. 
                                // Transition to the GE state.
                                candidateToken += curChar;
                                curState = State.GE;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                            
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case GT:
                        // TODO Greater-than operator
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            
                            if(curChar == '>')
                            {
                                // Remain in the current state.
                                curToken = new Token(candidateToken, "GT");
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // The lexical analyser has encountered
                                // a '>=' operator. 
                                // Transition to the GE state.
                                candidateToken += curChar;
                                curState = State.GE;
                            }
                            else
                            {
                                curToken = new Token(candidateToken, "GT");
                                tokens.add(curToken);
                                if(Character.isLetter(curChar))
                                {
                                    // The lexical analyser found a letter character.
                                    // Transition to the CHARACTER state.
                                    candidateToken = "" + curChar;
                                    curState = State.CHARACTER;
                                }
                                else if(Character.isDigit(curChar))
                                {
                                    // The lexical analyser found a digit.
                                    // Transition to the INTEGER_NO_EXP state.
                                    candidateToken = "" + curChar;
                                    curState = State.INTEGER_NO_EXP;
                                }
                                else if(curChar == '+')
                                {
                                    // The lexical analyser found a '+'.
                                    // Transition to the PLUS state.
                                    candidateToken = "" + curChar;
                                    curState = State.PLUS;
                                }
                                else if(curChar == '-')
                                {
                                    // The lexical analyser found a '-'.
                                    // Transition to the MINUS state.
                                    candidateToken = "" + curChar;
                                    curState = State.MINUS;
                                }
                                else if(curChar == '*')
                                {
                                    // The lexical analyser found a '*'.
                                    // Transition to the TIMES state.
                                    candidateToken = "" + curChar;
                                    curState = State.TIMES;
                                }
                                else if(curChar == ',')
                                {
                                    // The lexical analyser found a ','.
                                    // Transition to the COMMA state.
                                    candidateToken = "" + curChar;
                                    curState = State.COMMA;
                                }
                                else if(curChar == '<')
                                {
                                    // The lexical analyser found a '<'.
                                    // Transition to the LT state.
                                    candidateToken = "" + curChar;
                                    curState = State.LT;
                                }
                                else if(curChar == '!')
                                {
                                    // The lexical analyser found a '!'.
                                    // Transition to the BANG state.
                                    candidateToken = "" + curChar;
                                    curState = State.BANG;
                                }
                                else if(curChar == ';')
                                {
                                    // The lexical analyser found a ';'.
                                    // Transition to the SEMI state.
                                    candidateToken = "" + curChar;
                                    curState = State.SEMI;
                                }
                                else if(curChar == '(')
                                {
                                    // The lexical analyser found a '('.
                                    // Transition to the LP state.
                                    candidateToken = "" + curChar;
                                    curState = State.LP;
                                }
                                else if(curChar == ')')
                                {
                                    // The lexical analyser found a ')'.
                                    // Transition to the RP state.
                                    candidateToken = "" + curChar;
                                    curState = State.RP;
                                }
                                else if(curChar == '[')
                                {
                                    // The lexical analyser found a '['.
                                    // Transition to the LB state.
                                    candidateToken = "" + curChar;
                                    curState = State.LB;
                                }
                                else if(curChar == ']')
                                {
                                    // The lexical analyser found a ']'.
                                    // Transition to the RB state.
                                    candidateToken = "" + curChar;
                                    curState = State.RB;
                                }
                                else if(curChar == '{')
                                {
                                    // The lexical analyser found a '{'.
                                    // Transition to the LBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.LBR;
                                }
                                else if(curChar == '}')
                                {
                                    // The lexical analyser found a '}'.
                                    // Transition to the RBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.RBR;
                                }
                                else
                                {
                                    // The lexical analyser found an undefined
                                    // character. Transition to the ERROR state.
                                    candidateToken = "" + curChar;
                                    curState = State.ERROR;
                                }
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case LB:
                        // TODO Left bracket
                        if(curIndex < s.length() - 1)
                        {
                            curToken = new Token(candidateToken, "LB");
                            tokens.add(curToken);
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == '[')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case LBR:
                        // TODO Left brace
                        if(curIndex < s.length() - 1)
                        {
                            curToken = new Token(candidateToken, "LBR");
                            tokens.add(curToken);
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == '{')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case LE:
                        // TODO Less-than-or-equal operator
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            curToken = new Token(candidateToken, "LE");
                            tokens.add(curToken);

                            if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // The lexical analyser has encountered
                                // a '>=' operator. 
                                // Transition to the GE state.
                                candidateToken += curChar;
                                curState = State.GE;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case LINE_COMMENT:
                        // TODO Single-line comments
                        // The lexical analyser has found a line comment.
                        // Skip to the next line of source code.
                        curState = State.FINAL;
                        break;
                    case LP:
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            curToken = new Token(candidateToken, "LP");
                            tokens.add(curToken);
                            
                            if(curChar == '(')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '/')
                            {
                                // The lexical analyser found a '/'.
                                // Transition to the DIV state.
                                candidateToken = "" + curChar;
                                curState = State.DIV;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        // TODO Left parenthesis
                        break;
                    case LT:
                        // TODO Less-than operator
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == '<')
                            {
                                // Remain in the current state.
                                curToken = new Token(candidateToken, "LT");
                                tokens.add(curToken);
                                candidateToken = "" + curChar;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser has encountered
                                // a '<=' operator. 
                                // Transition to the LE state.
                                candidateToken += curChar;
                                curState = State.LE;
                            }
                            else
                            {
                                curToken = new Token(candidateToken, "LT");
                                tokens.add(curToken);
                                if(Character.isLetter(curChar))
                                {
                                    // The lexical analyser found a letter character.
                                    // Transition to the CHARACTER state.
                                    candidateToken = "" + curChar;
                                    curState = State.CHARACTER;
                                }
                                else if(Character.isDigit(curChar))
                                {
                                    // The lexical analyser found a digit.
                                    // Transition to the INTEGER_NO_EXP state.
                                    candidateToken = "" + curChar;
                                    curState = State.INTEGER_NO_EXP;
                                }
                                else if(curChar == '+')
                                {
                                    // The lexical analyser found a '+'.
                                    // Transition to the PLUS state.
                                    candidateToken = "" + curChar;
                                    curState = State.PLUS;
                                }
                                else if(curChar == '-')
                                {
                                    // The lexical analyser found a '-'.
                                    // Transition to the MINUS state.
                                    candidateToken = "" + curChar;
                                    curState = State.MINUS;
                                }
                                else if(curChar == '*')
                                {
                                    // The lexical analyser found a '*'.
                                    // Transition to the TIMES state.
                                    candidateToken = "" + curChar;
                                    curState = State.TIMES;
                                }
                                else if(curChar == '>')
                                {
                                    // The lexical analyser found a '>'.
                                    // Transition to the GT state.
                                    candidateToken = "" + curChar;
                                    curState = State.GT;
                                }
                                else if(curChar == ',')
                                {
                                    // The lexical analyser found a ','.
                                    // Transition to the COMMA state.
                                    candidateToken = "" + curChar;
                                    curState = State.COMMA;
                                }
                                else if(curChar == '!')
                                {
                                    // The lexical analyser found a '!'.
                                    // Transition to the BANG state.
                                    candidateToken = "" + curChar;
                                    curState = State.BANG;
                                }
                                else if(curChar == ';')
                                {
                                    // The lexical analyser found a ';'.
                                    // Transition to the SEMI state.
                                    candidateToken = "" + curChar;
                                    curState = State.SEMI;
                                }
                                else if(curChar == '(')
                                {
                                    // The lexical analyser found a '('.
                                    // Transition to the LP state.
                                    candidateToken = "" + curChar;
                                    curState = State.LP;
                                }
                                else if(curChar == ')')
                                {
                                    // The lexical analyser found a ')'.
                                    // Transition to the RP state.
                                    candidateToken = "" + curChar;
                                    curState = State.RP;
                                }
                                else if(curChar == '[')
                                {
                                    // The lexical analyser found a '['.
                                    // Transition to the LB state.
                                    candidateToken = "" + curChar;
                                    curState = State.LB;
                                }
                                else if(curChar == ']')
                                {
                                    // The lexical analyser found a ']'.
                                    // Transition to the RB state.
                                    candidateToken = "" + curChar;
                                    curState = State.RB;
                                }
                                else if(curChar == '{')
                                {
                                    // The lexical analyser found a '{'.
                                    // Transition to the LBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.LBR;
                                }
                                else if(curChar == '}')
                                {
                                    // The lexical analyser found a '}'.
                                    // Transition to the RBR state.
                                    candidateToken = "" + curChar;
                                    curState = State.RBR;
                                }
                                else
                                {
                                    // The lexical analyser found an undefined
                                    // character. Transition to the ERROR state.
                                    candidateToken = "" + curChar;
                                    curState = State.ERROR;
                                }
                            }
                            
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case NE:
                        // TODO Inequality operator
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            curToken = new Token(candidateToken, "NE");

                            if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // The lexical analyser has encountered
                                // a '>=' operator. 
                                // Transition to the GE state.
                                candidateToken += curChar;
                                curState = State.GE;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case RB:
                        // TODO Right bracket
                        if(curIndex < s.length() - 1)
                        {
                            curToken = new Token(candidateToken, "RB");
                            tokens.add(curToken);
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == ']')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case RBR:
                        // TODO Right brace
                        if(curIndex < s.length() - 1)
                        {
                            curToken = new Token(candidateToken, "RBR");
                            tokens.add(curToken);
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            if(curChar == '}')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case RP:
                        // TODO Right parenthesis
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            curToken = new Token(candidateToken, "RP");
                            tokens.add(curToken);
                            
                            if(curChar == ')')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '/')
                            {
                                // The lexical analyser found a '/'.
                                // Transition to the DIV state.
                                candidateToken = "" + curChar;
                                curState = State.DIV;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ';')
                            {
                                // The lexical analyser found a ';'.
                                // Transition to the SEMI state.
                                candidateToken = "" + curChar;
                                curState = State.SEMI;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    case SEMI:
                        // TODO Semicolon
                        if(curIndex < s.length() - 1)
                        {
                            curIndex++;
                            curChar = s.charAt(curIndex);
                            curToken = new Token(candidateToken, "SEMI");
                            tokens.add(curToken);
                            if(curChar == ';')
                            {
                                // Remain in the current state.
                                candidateToken = "" + curChar;
                            }
                            else if(Character.isLetter(curChar))
                            {
                                // The lexical analyser found a letter character.
                                // Transition to the CHARACTER state.
                                candidateToken = "" + curChar;
                                curState = State.CHARACTER;
                            }
                            else if(Character.isDigit(curChar))
                            {
                                // The lexical analyser found a digit.
                                // Transition to the INTEGER_NO_EXP state.
                                candidateToken = "" + curChar;
                                curState = State.INTEGER_NO_EXP;
                            }
                            else if(curChar == '+')
                            {
                                // The lexical analyser found a '+'.
                                // Transition to the PLUS state.
                                candidateToken = "" + curChar;
                                curState = State.PLUS;
                            }
                            else if(curChar == '-')
                            {
                                // The lexical analyser found a '-'.
                                // Transition to the MINUS state.
                                candidateToken = "" + curChar;
                                curState = State.MINUS;
                            }
                            else if(curChar == '*')
                            {
                                // The lexical analyser found a '*'.
                                // Transition to the TIMES state.
                                candidateToken = "" + curChar;
                                curState = State.TIMES;
                            }
                            else if(curChar == '=')
                            {
                                // The lexical analyser found a '='.
                                // Transition to the ASSIGN state.
                                candidateToken = "" + curChar;
                                curState = State.ASSIGN;
                            }
                            else if(curChar == '>')
                            {
                                // The lexical analyser found a '>'.
                                // Transition to the GT state.
                                candidateToken = "" + curChar;
                                curState = State.GT;
                            }
                            else if(curChar == '<')
                            {
                                // The lexical analyser found a '<'.
                                // Transition to the LT state.
                                candidateToken = "" + curChar;
                                curState = State.LT;
                            }
                            else if(curChar == '!')
                            {
                                // The lexical analyser found a '!'.
                                // Transition to the BANG state.
                                candidateToken = "" + curChar;
                                curState = State.BANG;
                            }
                            else if(curChar == ',')
                            {
                                // The lexical analyser found a ','.
                                // Transition to the COMMA state.
                                candidateToken = "" + curChar;
                                curState = State.COMMA;
                            }
                            else if(curChar == '(')
                            {
                                // The lexical analyser found a '('.
                                // Transition to the LP state.
                                candidateToken = "" + curChar;
                                curState = State.LP;
                            }
                            else if(curChar == ')')
                            {
                                // The lexical analyser found a ')'.
                                // Transition to the RP state.
                                candidateToken = "" + curChar;
                                curState = State.RP;
                            }
                            else if(curChar == '[')
                            {
                                // The lexical analyser found a '['.
                                // Transition to the LB state.
                                candidateToken = "" + curChar;
                                curState = State.LB;
                            }
                            else if(curChar == ']')
                            {
                                // The lexical analyser found a ']'.
                                // Transition to the RB state.
                                candidateToken = "" + curChar;
                                curState = State.RB;
                            }
                            else if(curChar == '{')
                            {
                                // The lexical analyser found a '{'.
                                // Transition to the LBR state.
                                candidateToken = "" + curChar;
                                curState = State.LBR;
                            }
                            else if(curChar == '}')
                            {
                                // The lexical analyser found a '}'.
                                // Transition to the RBR state.
                                candidateToken = "" + curChar;
                                curState = State.RBR;
                            }
                            else
                            {
                                // The lexical analyser found an undefined
                                // character. Transition to the ERROR state.
                                candidateToken = "" + curChar;
                                curState = State.ERROR;
                            }
                        }
                        else
                        {
                            curState = State.FINAL;
                        }
                        break;
                    default:
                        break;
                }
            }

            if(encounteredError || candidateToken.equals("!"))
            {
                curToken = new Token(candidateToken, "ERROR");
                tokens.add(curToken);
                encounteredError = false;
            }
            else if(candidateToken.matches(CHARACTER_REGEX))
            {
                curToken = extractCharacterToken(candidateToken);
                tokens.add(curToken);
            }
            else if(candidateToken.matches(INTEGER_REGEX))
            {
                curToken = new Token(candidateToken, "INT");
                tokens.add(curToken);
            }
            else if(candidateToken.matches(FLOATING_POINT_REGEX))
            {
                curToken = new Token(candidateToken, "FLOAT");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("+"))
            {
                curToken = new Token(candidateToken, "PLUS");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("-"))
            {
                curToken = new Token(candidateToken, "MINUS");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("*"))
            {
                curToken = new Token(candidateToken, "TIMES");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("/"))
            {
                curToken = new Token(candidateToken, "DIV");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("="))
            {
                curToken = new Token(candidateToken, "ASSIGN");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("=="))
            {
                curToken = new Token(candidateToken, "EQ");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("!="))
            {
                curToken = new Token(candidateToken, "NE");
                tokens.add(curToken);
            }
            else if(candidateToken.equals(">"))
            {
                curToken = new Token(candidateToken, "GT");
                tokens.add(curToken);
            }
            else if(candidateToken.equals(">="))
            {
                curToken = new Token(candidateToken, "GE");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("<"))
            {
                curToken = new Token(candidateToken, "LT");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("<="))
            {
                curToken = new Token(candidateToken, "LE");
                tokens.add(curToken);
            }
            else if(candidateToken.equals(";"))
            {
                curToken = new Token(candidateToken, "SEMI");
                tokens.add(curToken);
            }
            else if(candidateToken.equals(","))
            {
                curToken = new Token(candidateToken, "COMMA");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("("))
            {
                curToken = new Token(candidateToken, "LP");
                tokens.add(curToken);
            }
            else if(candidateToken.equals(")"))
            {
                curToken = new Token(candidateToken, "RP");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("["))
            {
                curToken = new Token(candidateToken, "LB");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("]"))
            {
                curToken = new Token(candidateToken, "RB");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("{"))
            {
                curToken = new Token(candidateToken, "LBR");
                tokens.add(curToken);
            }
            else if(candidateToken.equals("}"))
            {
                curToken = new Token(candidateToken, "RBR");
                tokens.add(curToken);
            }

            candidateToken = "";
            curState = State.START;
            curIndex = 0;
        }
        return tokens;
    }

    private Token extractCharacterToken(String candidateToken)
    {
        Token t = null;

        if(candidateToken.equals("else"))
        {
            // Found the "else" keyword.
            t = new Token(candidateToken, "KEYWORD");
        }
        else if(candidateToken.equals("float"))
        {
            // Found the "float" keyword.
            t = new Token(candidateToken, "KEYWORD");
        }
        else if(candidateToken.equals("if"))
        {
            // Found the "if" keyword.
            t = new Token(candidateToken, "KEYWORD");
        }
        else if(candidateToken.equals("int"))
        {
            // Found the "int" keyword.
            t = new Token(candidateToken, "KEYWORD");
        }
        else if(candidateToken.equals("return"))
        {
            // Found the "return" keyword.
            t = new Token(candidateToken, "KEYWORD");
        }
        else if(candidateToken.equals("void"))
        {
            // Found the "void" keyword.
            t = new Token(candidateToken, "KEYWORD");
        }
        else if(candidateToken.equals("while"))
        {
            // Found the "while" keyword.
            t = new Token(candidateToken, "KEYWORD");
        }
        else
        {
            // Found an identifier.
            t = new Token(candidateToken, "ID");
        }

        return t;
    }
}