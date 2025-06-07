package lox.token;

import lox.token.Tokens.Lexemes;

public abstract class AbstractToken implements Token {

  protected final Lexemes lexeme;

  protected AbstractToken(Lexemes lexeme) {
    this.lexeme = lexeme;
  }

  public Lexemes lexeme() {
    return lexeme;
  }

  @Override
  public String toString() {
    return lexeme.toString() + " null";
  }

  // Generated ...
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((lexeme == null) ? 0 : lexeme.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AbstractToken other = (AbstractToken) obj;
    if (lexeme == null) {
      if (other.lexeme != null)
        return false;
    } else if (!lexeme.equals(other.lexeme))
      return false;
    return true;
  }
}
