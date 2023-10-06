public class Lexer {
    private final String in;
    private int pos;
    private StringBuilder curToken;
    private String symbol;
    private int line;

    public Lexer(String in) {
        this.in = in;
        pos = 0;
        line = 1;
        curToken = null;
        symbol = null;
    }

    public boolean hasNext() {
        curToken = new StringBuilder();
        symbol = null;
        while (pos < in.length() && isBlank(in.charAt(pos))) {
            pos++;
        }
        if (pos >= in.length()) {
            return false;
        }
        if (isIdHead(in.charAt(pos))) {
            while (isIdHead(in.charAt(pos)) || Character.isDigit(in.charAt(pos))) {
                catToken();
            }
            reserve();
        } else if (Character.isDigit(in.charAt(pos))) {
            symbol = "INTCON";
            while (Character.isDigit(in.charAt(pos))) {
                catToken();
            }
        } else if (in.charAt(pos) == '"') {
            symbol = "STRCON";
            catToken();
            while (in.charAt(pos) != '"') {
                catToken();
            }
            catToken();
        } else if (in.charAt(pos) == '!') {
            catToken();
            if (in.charAt(pos) == '=') {
                catToken();
                symbol = "NEQ";
            } else {
                symbol = "NOT";
            }
        } else if (in.charAt(pos) == '&') {
            catToken();
            if (in.charAt(pos) == '&') {
                catToken();
                symbol = "AND";
            }
            //else error();
        } else if (in.charAt(pos) == '|') {
            catToken();
            if (in.charAt(pos) == '|') {
                catToken();
                symbol = "OR";
            }
            //else error();
        } else if (in.charAt(pos) == '+') {
            catToken();
            symbol = "PLUS";
        } else if (in.charAt(pos) == '-') {
            catToken();
            symbol = "MINU";
        } else if (in.charAt(pos) == '*') {
            catToken();
            symbol = "MULT";
        } else if (in.charAt(pos) == '/') {
            if (in.charAt(pos + 1) == '/') {
                while (in.charAt(pos) != '\n') {
                    pos++;
                }
                return hasNext();
            } else if (in.charAt(pos + 1) == '*') {
                while (!(in.charAt(pos) == '*' && in.charAt(pos + 1) == '/')) {
                    pos++;
                }
                pos += 2;
                return hasNext();
            } else {
                catToken();
                symbol = "DIV";
            }
        } else if (in.charAt(pos) == '%') {
            catToken();
            symbol = "MOD";
        } else if (in.charAt(pos) == '<') {
            catToken();
            if (in.charAt(pos) == '=') {
                catToken();
                symbol = "LEQ";
            } else {
                symbol = "LSS";
            }
        } else if (in.charAt(pos) == '>') {
            catToken();
            if (in.charAt(pos) == '=') {
                catToken();
                symbol = "GEQ";
            } else {
                symbol = "GRE";
            }
        } else if (in.charAt(pos) == '=') {
            catToken();
            if (in.charAt(pos) == '=') {
                catToken();
                symbol = "EQL";
            } else {
                symbol = "ASSIGN";
            }
        } else if (in.charAt(pos) == ';') {
            catToken();
            symbol = "SEMICN";
        } else if (in.charAt(pos) == ',') {
            catToken();
            symbol = "COMMA";
        } else if (in.charAt(pos) == '(') {
            catToken();
            symbol = "LPARENT";
        } else if (in.charAt(pos) == ')') {
            catToken();
            symbol = "RPARENT";
        } else if (in.charAt(pos) == '[') {
            catToken();
            symbol = "LBRACK";
        } else if (in.charAt(pos) == ']') {
            catToken();
            symbol = "RBRACK";
        } else if (in.charAt(pos) == '{') {
            catToken();
            symbol = "LBRACE";
        } else if (in.charAt(pos) == '}') {
            catToken();
            symbol = "RBRACE";
        }
        return true;
    }

    public String getToken() {
        return curToken.toString();
    }

    public String getSymbol() {
        return symbol;
    }

    private void catToken() {
        curToken.append(in.charAt(pos));
        pos++;
    }

    private boolean isBlank(char c) {
        if (c == '\n') {
            line++;
            return true;
        }
        return c == ' ' || c == '\t' || c == '\r';
    }

    private boolean isIdHead(char c) {
        return c == '_' || Character.isLetter(c);
    }

    private void reserve() {
        String tmp = curToken.toString();
        switch (tmp) {
            case "main" -> symbol = "MAINTK";
            case "const" -> symbol = "CONSTTK";
            case "int" -> symbol = "INTTK";
            case "break" -> symbol = "BREAKTK";
            case "continue" -> symbol = "CONTINUETK";
            case "if" -> symbol = "IFTK";
            case "else" -> symbol = "ELSETK";
            case "for" -> symbol = "FORTK";
            case "getint" -> symbol = "GETINTTK";
            case "printf" -> symbol = "PRINTFTK";
            case "return" -> symbol = "RETURNTK";
            case "void" -> symbol = "VOIDTK";
            default -> symbol = "IDENFR";
        }
    }

}
