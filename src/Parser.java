import node.*;
import node.Number;
import node.expression.*;
import node.ForStmt;
import node.stmt.*;
import token.Token;
import token.TokenType;

import java.util.ArrayList;
import java.util.Objects;

public class Parser {
    private final ArrayList<Token> tokens;
    private int index;

    public Parser(Lexer lexer) {
        this.tokens = lexer.getTokens();
        this.index = 0;
    }

    public CompUnit compUnit() {
        ArrayList<Decl> decls = new ArrayList<>();
        ArrayList<FuncDef> funcDefs = new ArrayList<>();
        MainFuncDef mainFuncDef;
        while (!Objects.equals(tokens.get(index + 2).getSymbol(), TokenType.LPARENT)) {
            Decl decl = decl();
            decls.add(decl);
        }
        while (!Objects.equals(tokens.get(index + 1).getSymbol(), TokenType.MAINTK)) {
            FuncDef funcDef = funcDef();
            funcDefs.add(funcDef);
        }
        mainFuncDef = mainFuncDef();
        return new CompUnit(decls, funcDefs, mainFuncDef);
    }

    private Decl decl() {
        if (match(TokenType.CONSTTK)) {
            return constDecl();
        } else {
            return varDecl();
        }//else error
    }

    private ConstDecl constDecl() {
        BType bType = bType();
        ArrayList<ConstDef> constDefs = new ArrayList<>();
        {
            ConstDef constDef = constDef();
            constDefs.add(constDef);
        }
        while (!match(TokenType.SEMICN)) {
            if (!match(TokenType.COMMA)) {
                error();
            }
            ConstDef constDef = constDef();
            constDefs.add(constDef);
        }
        return new ConstDecl(bType, constDefs);
    }

    private BType bType() {
        if (!match(TokenType.INTTK)) {
            error();
        }
        return new BType(tokens.get(index - 1));
    }

    private ConstDef constDef() {
        if (!match(TokenType.IDENFR)) {
            error();
        }
        Token Ident = tokens.get(index - 1);
        ArrayList<ConstExp> constExps = new ArrayList<>();
        while (match(TokenType.LBRACK)) {
            ConstExp constExp = constExp();
            constExps.add(constExp);
            if (!match(TokenType.RBRACK)) {
                error();
            }
        }
        if (!match(TokenType.ASSIGN)) {
            error();
        }
        ConstInitVal constInitVal = constInitVal();
        return new ConstDef(Ident, constExps, constInitVal);
    }

    private ConstExp constExp() {
        AddExp addExp = addExp();
        return new ConstExp(addExp);
    }

    private ConstInitVal constInitVal() {
        if (match(TokenType.LBRACE)) {
            ArrayList<ConstInitVal> constInitVals = new ArrayList<>();
            if (match(TokenType.RBRACE)) {
                return new ConstInitVal(constInitVals);
            }
            {
                ConstInitVal constInitVal = constInitVal();
                constInitVals.add(constInitVal);
            }
            while (!match(TokenType.RBRACE)) {
                if (!match(TokenType.COMMA)) {
                    error();
                }
                ConstInitVal constInitVal = constInitVal();
                constInitVals.add(constInitVal);
            }
            return new ConstInitVal(constInitVals);
        } else {
            ConstExp constExp = constExp();
            return new ConstInitVal(constExp);
        }
    }

    private VarDecl varDecl() {
        BType bType = bType();
        ArrayList<VarDef> varDefs = new ArrayList<>();
        varDefs.add(varDef());
        while (!match(TokenType.SEMICN)) {
            if (!match(TokenType.COMMA)) {
                error();
            }
            varDefs.add(varDef());
        }
        return new VarDecl(bType, varDefs);
    }

    private VarDef varDef() {
        Token Ident = tokens.get(index);
        ArrayList<ConstExp> constExps = new ArrayList<>();
        InitVal InitVal = null;
        if (!match(TokenType.IDENFR)) {
            error();
        }
        while (match(TokenType.LBRACK)) {
            constExps.add(constExp());
            if (!match(TokenType.RBRACK)) {
                error();
            }
        }
        if (match(TokenType.ASSIGN)) {
            InitVal = initVal();
        }
        return new VarDef(Ident, constExps, InitVal);
    }

    private Exp exp() {
        return new Exp(addExp());
    }

    private InitVal initVal() {
        if (match(TokenType.LBRACE)) {
            ArrayList<InitVal> initVals = new ArrayList<>();
            if (match(TokenType.RBRACE)) {
                return new InitVal(initVals);
            }
            initVals.add(initVal());
            while (match(TokenType.COMMA)) {
                initVals.add(initVal());
            }
            if (!match(TokenType.RBRACE)) {
                error();
            }
            return new InitVal(initVals);
        } else {
            return new InitVal(exp());
        }
    }

    private FuncDef funcDef() {
        FuncType funcType = funcType();
        Token Ident = tokens.get(index);
        FuncFParams funcFParams = null;
        if (!match(TokenType.IDENFR)) {
            error();
        }
        if (!match(TokenType.LPARENT)) {
            error();
        }
        if (!match(TokenType.RPARENT)) {
            funcFParams = funcFParams();
            if (!match(TokenType.RPARENT)) {
                error();
            }
        }
        Block block = block();
        return new FuncDef(funcType, Ident, funcFParams, block);
    }

    private FuncType funcType() {
        Token funcType = tokens.get(index);
        if (!match(TokenType.VOIDTK) && !match(TokenType.INTTK)) {
            error();
        }
        return new FuncType(funcType);
    }

    private FuncFParams funcFParams() {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        {
            FuncFParam funcFParam = funcFParam();
            funcFParams.add(funcFParam);
        }
        while (match(TokenType.COMMA)) {
            FuncFParam funcFParam = funcFParam();
            funcFParams.add(funcFParam);
        }
        return new FuncFParams(funcFParams);
    }

    private FuncFParam funcFParam() {
        BType bType = bType();
        Token Ident = tokens.get(index);
        boolean isArray = false;
        ArrayList<ConstExp> constExps = new ArrayList<>();
        if (!match(TokenType.IDENFR)) {
            error();
        }
        if (match(TokenType.LBRACK)) {
            isArray = true;
            if (!match(TokenType.RBRACK)) {
                error();
            }
            while (match(TokenType.LBRACK)) {
                ConstExp constExp = constExp();
                constExps.add(constExp);
                if (!match(TokenType.RBRACK)) {
                    error();
                }
            }
        }
        return new FuncFParam(bType, Ident, constExps, isArray);
    }

    private Block block() {
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        if (!match(TokenType.LBRACE)) {
            error();
        }
        while (!match(TokenType.RBRACE)) {
            BlockItem blockItem = blockItem();
            blockItems.add(blockItem);
        }
        return new Block(blockItems);
    }

    private BlockItem blockItem() {
        if (Objects.equals(tokens.get(index).getSymbol(), TokenType.CONSTTK)
                || Objects.equals(tokens.get(index).getSymbol(), TokenType.INTTK)) {
            Decl decl = decl();
            return new BlockItem(decl);
        } else {
            Stmt stmt = stmt();
            return new BlockItem(stmt);
        }
    }

    private Stmt stmt() {
        if (match(TokenType.IFTK)) {
            return ifStmt();
        }
        if (match(TokenType.FORTK)) {
            return stmtFor();
        }
        if (match(TokenType.BREAKTK)) {
            if (!match(TokenType.SEMICN)) {
                error();
            }
            return new BreakStmt();
        }
        if (match(TokenType.CONTINUETK)) {
            if (!match(TokenType.SEMICN)) {
                error();
            }
            return new ContinueStmt();
        }
        if (match(TokenType.RETURNTK)) {
            return returnStmt();
        }
        if (match(TokenType.PRINTFTK)) {
            return printfStmt();
        }
        if (Objects.equals(tokens.get(index).getSymbol(), TokenType.LBRACE)) {
            return new BlockStmt(block());
        }
        if (Objects.equals(tokens.get(index).getSymbol(), TokenType.IDENFR)) {
            int tmpIndex = index;
            LVal lVal = lVal();
            if (match(TokenType.ASSIGN)) {
                if (match(TokenType.GETINTTK)) {
                    if (!match(TokenType.LPARENT)) {
                        error();
                    }
                    if (!match(TokenType.RPARENT)) {
                        error();
                    }
                    if (!match(TokenType.SEMICN)) {
                        error();
                    }
                    return new AssignGetintStmt(lVal);
                }
                Exp exp = exp();
                if (!match(TokenType.SEMICN)) {
                    error();
                }
                return new AssignExpStmt(lVal, exp);
            }
            index = tmpIndex;
        }
        Exp exp = null;
        if (!match(TokenType.SEMICN)) {
            exp = exp();
            if (!match(TokenType.SEMICN)) {
                error();
            }
        }
        return new ExpStmt(exp);
        //else error();
    }

    private Cond cond() {
        return new Cond(lOrExp());
    }

    private IfStmt ifStmt() {
        //这里if已经被匹配过了
        Cond cond;
        Stmt stmt1;
        Stmt stmt2 = null;
        if (!match(TokenType.LPARENT)) {
            error();
        }
        cond = cond();
        if (!match(TokenType.RPARENT)) {
            error();
        }
        stmt1 = stmt();
        if (match(TokenType.ELSETK)) {
            stmt2 = stmt();
        }
        return new IfStmt(cond, stmt1, stmt2);
    }

    private For stmtFor() {
        //for已经被匹配过了
        ForStmt forStmt1 = null;
        Cond cond = null;
        ForStmt forStmt2 = null;
        Stmt stmt;
        if (!match(TokenType.LPARENT)) {
            error();
        }
        if (!match(TokenType.SEMICN)) {
            forStmt1 = forStmt();
            if (!match(TokenType.SEMICN)) {
                error();
            }
        }
        if (!match(TokenType.SEMICN)) {
            cond = cond();
            if (!match(TokenType.SEMICN)) {
                error();
            }
        }
        if (!match(TokenType.RPARENT)) {
            forStmt2 = forStmt();
            if (!match(TokenType.RPARENT)) {
                error();
            }
        }
        stmt = stmt();
        return new For(forStmt1, cond, forStmt2, stmt);
    }

    private ForStmt forStmt() {
        LVal lVal = lVal();
        if (!match(TokenType.ASSIGN)) {
            error();
        }
        return new ForStmt(lVal, exp());
    }

    private ReturnStmt returnStmt() {
        //return 已经被匹配过了
        Exp exp = null;
        if (!match(TokenType.SEMICN)) {
            exp = exp();
            if (!match(TokenType.SEMICN)) {
                error();
            }
        }
        return new ReturnStmt(exp);
    }

    private PrintfStmt printfStmt() {
        //print已经被匹配过了
        if (!match(TokenType.LPARENT)) {
            error();
        }
        Token token = tokens.get(index);
        ArrayList<Exp> exps = new ArrayList<>();
        if (!match(TokenType.STRCON)) {
            error();
        }
        while (!match(TokenType.RPARENT)) {
            if (!match(TokenType.COMMA)) {
                error();
            }
            exps.add(exp());
        }
        if (!match(TokenType.SEMICN)) {
            error();
        }
        return new PrintfStmt(token, exps);
    }

    private LVal lVal() {
        Token token = tokens.get(index);
        ArrayList<Exp> exps = new ArrayList<>();
        if (!match(TokenType.IDENFR)) {
            error();
        }
        while (match(TokenType.LBRACK)) {
            exps.add(exp());
            if (!match(TokenType.RBRACK)) {
                error();
            }
        }
        return new LVal(token, exps);
    }

    private AddExp addExp() {
        ArrayList<MulExp> mulExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        MulExp mulExp = mulExp();
        mulExps.add(mulExp);
        while (match(TokenType.PLUS) || match(TokenType.MINU)) {
            Token op = tokens.get(index - 1);
            ops.add(op);
            mulExps.add(mulExp());
        }
        return new AddExp(mulExps, ops);
    }

    private MulExp mulExp() {
        ArrayList<UnaryExp> unaryExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        {
            UnaryExp unaryExp = unaryExp();
            unaryExps.add(unaryExp);
        }
        while (match(TokenType.MULT) || match(TokenType.DIV) || match(TokenType.MOD)) {
            Token op = tokens.get(index - 1);
            ops.add(op);
            UnaryExp unaryExp = unaryExp();
            unaryExps.add(unaryExp);
        }
        return new MulExp(unaryExps, ops);
    }

    private LOrExp lOrExp() {
        ArrayList<LAndExp> lAndExps = new ArrayList<>();
        lAndExps.add(lAndExp());
        while (match(TokenType.OR)) {
            lAndExps.add(lAndExp());
        }
        return new LOrExp(lAndExps);
    }

    private LAndExp lAndExp() {
        ArrayList<EqExp> eqExps = new ArrayList<>();
        eqExps.add(eqExp());
        while (match(TokenType.AND)) {
            eqExps.add(eqExp());
        }
        return new LAndExp(eqExps);
    }

    private EqExp eqExp() {
        ArrayList<RelExp> relExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        relExps.add(relExp());
        while (match(TokenType.EQL) || match(TokenType.NEQ)) {
            ops.add(tokens.get(index - 1));
            relExps.add(relExp());
        }
        return new EqExp(relExps, ops);
    }

    private RelExp relExp() {
        ArrayList<AddExp> addExps = new ArrayList<>();
        ArrayList<Token> ops = new ArrayList<>();
        addExps.add(addExp());
        while (match(TokenType.LSS) || match(TokenType.LEQ)
                || match(TokenType.GRE) || match(TokenType.GEQ)) {
            ops.add(tokens.get(index - 1));
            addExps.add(addExp());
        }
        return new RelExp(addExps, ops);
    }

    private UnaryExp unaryExp() {
        if (Objects.equals(tokens.get(index).getSymbol(), TokenType.IDENFR)
                && Objects.equals(tokens.get(index + 1).getSymbol(), TokenType.LPARENT)) {
            Token ident = tokens.get(index);
            FuncRParams funcRParams = null;
            index += 2;
            if (!match(TokenType.RPARENT)) {
                funcRParams = funcRParams();
                if (!match(TokenType.RPARENT)) {
                    error();
                }
            }
            return new UnaryExp(ident, funcRParams);
        }
        Token op = tokens.get(index);
        if (match(TokenType.PLUS) || match(TokenType.MINU) || match(TokenType.NOT)) {
            UnaryOp unaryOp = new UnaryOp(op);
            UnaryExp unaryExp = unaryExp();
            return new UnaryExp(unaryOp, unaryExp);
        }
        return new UnaryExp(primaryExp());
    }

    private PrimaryExp primaryExp() {
        if (match(TokenType.LPARENT)) {
            Exp exp = exp();
            if (!match(TokenType.RPARENT)) {
                error();
            }
            return new PrimaryExp(exp);
        }
        if (match(TokenType.INTCON)) {
            return new PrimaryExp(new Number(tokens.get(index - 1)));
        }
        LVal lVal = lVal();
        return new PrimaryExp(lVal);
    }

    private FuncRParams funcRParams() {
        ArrayList<Exp> exps = new ArrayList<>();
        exps.add(exp());
        while (match(TokenType.COMMA)) {
            exps.add(exp());
        }
        return new FuncRParams(exps);
    }

    private MainFuncDef mainFuncDef() {
        if (!match(TokenType.INTTK)) {
            error();
        }
        if (!match(TokenType.MAINTK)) {
            error();
        }
        if (!match(TokenType.LPARENT)) {
            error();
        }
        if (!match(TokenType.RPARENT)) {
            error();
        }
        Block block = block();
        return new MainFuncDef(block);
    }

    private boolean match(TokenType tokenType) {
        if (tokens.get(index).getSymbol() == tokenType) {
            index++;
            return true;
        } else {
            return false;
        }
    }

    private void error() {
        System.out.println("error at index:" + index);
        System.exit(1);
    }
}
