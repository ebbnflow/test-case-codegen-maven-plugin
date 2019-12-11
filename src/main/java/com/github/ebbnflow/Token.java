package com.github.ebbnflow;

import com.google.common.base.Objects;

import java.util.ArrayList;
import java.util.List;

public class Token {
    private String tokenStart;
    private List<Token> childTokens;
    private String tokenEnd;
    private boolean newLineAfterTokenEnd;

    public Token(){
        this.newLineAfterTokenEnd = true;
    }

    public String getTokenStart() {
        return tokenStart;
    }

    public void setTokenStart(String tokenStart) {
        this.tokenStart = tokenStart;
    }

    public List<Token> getChildTokens() {
        return childTokens;
    }

    public void setChildTokens(List<Token> childTokens) {
        this.childTokens = childTokens;
    }

    public String getTokenEnd() {
        return tokenEnd;
    }

    public void setTokenEnd(String tokenEnd) {
        this.tokenEnd = tokenEnd;
    }

    public Token createChildToken() {
        if (null == this.childTokens) {
            this.childTokens = new ArrayList<>();
        }
        Token newChildToken = new Token();
        this.childTokens.add(newChildToken);
        return newChildToken;
    }

    public Token createChildToken(String startToken, String tokenEnd) {
        if (null == this.childTokens) {
            this.childTokens = new ArrayList<>();
        }
        Token newChildToken = new Token();
        this.childTokens.add(newChildToken);
        newChildToken.tokenStart = startToken;
        newChildToken.tokenEnd = tokenEnd;
        return newChildToken;
    }

    public Token createChildToken(String startToken, String tokenEnd, boolean addNewLineAfterTokenEnd) {
        if (null == this.childTokens) {
            this.childTokens = new ArrayList<>();
        }
        Token newChildToken = new Token();
        this.childTokens.add(newChildToken);
        newChildToken.tokenStart = startToken;
        newChildToken.tokenEnd = tokenEnd;
        newChildToken.newLineAfterTokenEnd = addNewLineAfterTokenEnd;
        return newChildToken;
    }

    public boolean isNewLineAfterTokenEnd() {
        return newLineAfterTokenEnd;
    }

    public void setNewLineAfterTokenEnd(boolean newLineAfterTokenEnd) {
        this.newLineAfterTokenEnd = newLineAfterTokenEnd;
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenStart='" + tokenStart + '\'' +
                ", childTokens=" + childTokens +
                ", tokenEnd='" + tokenEnd + '\'' +
                ", addNewLineAfterTokenEnd=" + newLineAfterTokenEnd +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return newLineAfterTokenEnd == token.newLineAfterTokenEnd &&
                Objects.equal(tokenStart, token.tokenStart) &&
                Objects.equal(childTokens, token.childTokens) &&
                Objects.equal(tokenEnd, token.tokenEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tokenStart, childTokens, tokenEnd, newLineAfterTokenEnd);
    }

}
