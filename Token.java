public class Token
{
    protected String token;
    protected String tokenType;

    /**
     * Instantiates a <code>Token</code> object.
     * @param token - A string representation of the token.
     * @param tokenType - The name of the token, as defined in the C- grammar.
     */
    public Token(String token, String tokenType)
    {
        this.token = token;
        this.tokenType = tokenType;
    }

    /**
     * Gets the actual token stored in this <code>Token</code> object.
     * 
     * For instance, if a <code>Token</code> object represents
     * the identifier <code>cutiemark</code>, invoking
     * this method returns the string <code>"cutiemark"</code>.
     * @return - A string representation of the token.
     */
    public String getToken()
    {
        return token;
    }

    /**
     * Gets the type of token stored in this <code>Token</code> object.
     * 
     * For instance, if a <code>Token</code> object represents
     * the identifier <code>cutiemark</code>, invoking
     * this method returns the string <code>"ID"</code>.
     * @return - The classification of the token.
     */
    public String getTokenType()
    {
        return tokenType;
    }
    
    public String toString()
    {
        return "{Token: " + token + " , "
               + "Token type: " + tokenType + "}";
    }
}