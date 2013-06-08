/* Generated By:JavaCC: Do not edit this line. ESLLParserByJavaCC.java */
package com.github.kmizu.esll.parser;
import com.github.kmizu.esll.*;
import java.util.*;
import java.io.*;
import static com.github.kmizu.esll.Ast.*;
public class ESLLParserByJavaCC implements ESLLParserByJavaCCConstants {
  public static void main(String[] args)throws Exception {
    ESLLParserByJavaCC parser = new ESLLParserByJavaCC(
      new BackupCharStream(new InputStreamReader(System.in))
    );
    while(true) {
      parser.statement();
    }
  }
  private static Pos pos(Token t){
        return new Pos(t.beginLine, t.beginColumn);
  }
  private static Pos pos(int line, int column) {
        return new Pos(line, column);
  }
  private static String unescape(String s){
    StringBuffer sb = new StringBuffer();
    for(int i = 0; i < s.length(); i++){
      char ch = s.charAt(i);
      if(ch != '\\'){
        sb.append(ch);
        continue;
      }
      i++;
      ch = s.charAt(i);
      switch(ch){
      case 'n':
        sb.append('\n');
        break;
      case 't':
        sb.append('\t');
        break;
      case 'b':
        sb.append('\b');
        break;
      case 'r':
        sb.append('\r');
        break;
      case 'f':
        sb.append('\f');
        break;
      case '\'':
        sb.append('\'');
      case '\\':
        sb.append('\\');
        break;
      default:
        sb.append(ch);
        break;
      }
    }
    return new String(sb);
  }

  // Copy from http://www.engr.mun.ca/~theo/JavaCC-FAQ/SetState.txt
  // JavaCC hack for doing lexical state transitions syntactically
  private void SetState(int state) {
    if (state != token_source.curLexState) {
      Token root = new Token(), last=root;
      root.next = null;

      // First, we build a list of tokens to push back, in backwards order
      while (token.next != null) {
        Token t = token;
        // Find the token whose token.next is the last in the chain
        while (t.next != null && t.next.next != null)
          t = t.next;

        // put it at the end of the new chain
        last.next = t.next;
        last = t.next;

        // If there are special tokens, these go before the regular tokens,
        // so we want to push them back onto the input stream in the order
        // we find them along the specialToken chain.

        if (t.next.specialToken != null) {
          Token tt=t.next.specialToken;
          while (tt != null) {
            last.next = tt;
            last = tt;
            tt.next = null;
            tt = tt.specialToken;
          }
        }
        t.next = null;
      };

      while (root.next != null) {
        token_source.backup(root.next.image.length());
        root.next = root.next.next;
      }
      jj_ntk = -1;
      token_source.SwitchTo(state);
    }
  }

  final public Program program() throws ParseException {
  Declaration d;
  List<Declaration> declarations = new ArrayList<Declaration>();
    label_1:
    while (true) {
      d = declaration();
                    declarations.add(d);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case FUN:
      case VAR:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
    }
    jj_consume_token(0);
    {if (true) return new Program(declarations.get(0).pos, declarations);}
    throw new Error("Missing return statement in function");
  }

  final public Declaration declaration() throws ParseException {
                             Declaration d;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case FUN:
      d = functionDeclaration();
      break;
    case VAR:
      d = globalVariableDeclaration();
      break;
    default:
      jj_la1[1] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
                                                            {if (true) return d;}
    throw new Error("Missing return statement in function");
  }

  final public GlobalVariableDeclaration globalVariableDeclaration() throws ParseException {
                                                         Token t, n;
    t = jj_consume_token(VAR);
    n = jj_consume_token(IDENT);
    jj_consume_token(SEMI);
                            {if (true) return new GlobalVariableDeclaration(pos(t), t.image);}
    throw new Error("Missing return statement in function");
  }

  final public FunctionDeclaration functionDeclaration() throws ParseException {
  Token t, n, p; Statement s;
  List<String> params = new ArrayList<String>();
    t = jj_consume_token(FUN);
    n = jj_consume_token(IDENT);
    jj_consume_token(LPAREN);
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENT:
      n = jj_consume_token(IDENT);
              params.add(n.image);
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case COMMA:
          ;
          break;
        default:
          jj_la1[2] = jj_gen;
          break label_2;
        }
        jj_consume_token(COMMA);
        jj_consume_token(IDENT);
                                                      params.add(n.image);
      }
      break;
    default:
      jj_la1[3] = jj_gen;
      ;
    }
    jj_consume_token(RPAREN);
    s = block();
                      {if (true) return new FunctionDeclaration(pos(t), n.image, params, s);}
    throw new Error("Missing return statement in function");
  }

  final public Statement statement() throws ParseException {
  Token t; Expression e;
  List<Statement> statements = new ArrayList<Statement>();
  Statement s;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case PRINT:
      t = jj_consume_token(PRINT);
      jj_consume_token(LPAREN);
      e = expression();
      jj_consume_token(RPAREN);
      jj_consume_token(SEMI);
                                                     {if (true) return new PrintStatement(pos(t), e);}
      break;
    case LB:
      s = block();
             {if (true) return s;}
      break;
    case IF:
      s = ifStatement();
                   {if (true) return s;}
      break;
    case NUMBER:
    case LPAREN:
    case DQUOTE1:
    case IDENT:
      e = expression();
      jj_consume_token(SEMI);
                         {if (true) return new ExpressionStatement(e.pos, e);}
      break;
    default:
      jj_la1[4] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
  }

  final public Statement block() throws ParseException {
  Token t; List<Statement> statements = new ArrayList<Statement>();
  Statement s;
    t = jj_consume_token(LB);
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case NUMBER:
      case LPAREN:
      case LB:
      case DQUOTE1:
      case PRINT:
      case IF:
      case IDENT:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_3;
      }
      s = statement();
                         statements.add(s);
    }
    jj_consume_token(RB);
                                                     {if (true) return new BlockStatement(pos(t), statements);}
    throw new Error("Missing return statement in function");
  }

  final public Statement ifStatement() throws ParseException {
                           Token t; Expression condition; Statement thenBody, elseBody = null;
    t = jj_consume_token(IF);
    jj_consume_token(LPAREN);
    condition = expression();
    jj_consume_token(RPAREN);
    thenBody = statement();
    if (jj_2_1(2)) {
      jj_consume_token(ELSE);
      elseBody = statement();
    } else {
      ;
    }
   {if (true) return new IfStatement(pos(t), condition, thenBody, elseBody);}
    throw new Error("Missing return statement in function");
  }

  final public Expression expression() throws ParseException {
                          Token t = null; Expression l,r;
    l = additive();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case EQ:
      jj_consume_token(EQ);
      r = expression();
                                     l = new BinaryExpression(pos(t), Operator.ASSIGN, l, r);
      break;
    default:
      jj_la1[6] = jj_gen;
      ;
    }
   {if (true) return l;}
    throw new Error("Missing return statement in function");
  }

  final public Expression additive() throws ParseException {
                        Token t; Expression l,r;
    l = multitive();
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
      case MINUS:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_4;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
        t = jj_consume_token(PLUS);
        r = multitive();
                            l = new BinaryExpression(pos(t), Operator.PLUS, l, r);
        break;
      case MINUS:
        t = jj_consume_token(MINUS);
        r = multitive();
                             l = new BinaryExpression(pos(t), Operator.MINUS, l, r);
        break;
      default:
        jj_la1[8] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
      {if (true) return l;}
    throw new Error("Missing return statement in function");
  }

  final public Expression multitive() throws ParseException {
                         Token t; Expression l,r;
    l = primary();
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ASTER:
      case SLASH:
      case PERC:
        ;
        break;
      default:
        jj_la1[9] = jj_gen;
        break label_5;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ASTER:
        t = jj_consume_token(ASTER);
        r = primary();
                           l = new BinaryExpression(pos(t), Operator.MULT, l, r);
        break;
      case SLASH:
        t = jj_consume_token(SLASH);
        r = primary();
                           l = new BinaryExpression(pos(t), Operator.DIV, l, r);
        break;
      case PERC:
        t = jj_consume_token(PERC);
        r = primary();
                          l = new BinaryExpression(pos(t), Operator.MOD, l, r);
        break;
      default:
        jj_la1[10] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
      {if (true) return l;}
    throw new Error("Missing return statement in function");
  }

  final public Expression primary() throws ParseException {
  Token t; Expression e;
  List<Expression> es = new ArrayList<Expression>();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case LPAREN:
      jj_consume_token(LPAREN);
      e = expression();
      jj_consume_token(RPAREN);
                                    {if (true) return e;}
      break;
    case DQUOTE1:
      e = string();
              {if (true) return e;}
      break;
    default:
      jj_la1[13] = jj_gen;
      if (jj_2_2(2)) {
        t = jj_consume_token(IDENT);
        jj_consume_token(LPAREN);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case NUMBER:
        case LPAREN:
        case DQUOTE1:
        case IDENT:
          e = expression();
                   es.add(e);
          jj_consume_token(COMMA);
          label_6:
          while (true) {
            switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
            case NUMBER:
            case LPAREN:
            case DQUOTE1:
            case IDENT:
              ;
              break;
            default:
              jj_la1[11] = jj_gen;
              break label_6;
            }
            e = expression();
                                                        es.add(e);
          }
          break;
        default:
          jj_la1[12] = jj_gen;
          ;
        }
        jj_consume_token(RPAREN);
   {if (true) return new FunctionCall(pos(t), t.image, es);}
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case IDENT:
          t = jj_consume_token(IDENT);
             {if (true) return new Identifier(pos(t), t.image);}
          break;
        case NUMBER:
          t = jj_consume_token(NUMBER);
              {if (true) return new NumberLiteral(pos(t), Integer.parseInt(t.image));}
          break;
        default:
          jj_la1[14] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
    throw new Error("Missing return statement in function");
  }

  final public Expression string() throws ParseException {
                      Token t, s; Expression e; List<Expression> es = new ArrayList<Expression>();
    t = jj_consume_token(DQUOTE1);
               SetState(1);/* -> <IN_STRING> */
    label_7:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BEGIN_EXPR:
      case CONTENT:
        ;
        break;
      default:
        jj_la1[15] = jj_gen;
        break label_7;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BEGIN_EXPR:
        jj_consume_token(BEGIN_EXPR);
                 SetState(0);/* -> <DEFAULT> */
        e = expression();
                                                                 es.add(e);
        jj_consume_token(RB);
                                                                                   SetState(1); /* -> <IN_STRING> */
        break;
      case CONTENT:
        s = jj_consume_token(CONTENT);
                es.add(new StringLiteral(pos(s), s.image));
        break;
      default:
        jj_la1[16] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
    jj_consume_token(DQUOTE2);
             SetState(0);/* -> <DEFAULT> */
    if(es.size() == 0) {{if (true) return new StringLiteral(pos(t), "");}}
    else if(es.size() == 1) {{if (true) return es.get(0);}}
    else {
      Expression l = es.get(0);
      for(Expression r:es.subList(1, es.size())) {
        l = new BinaryExpression(l.pos, Operator.PLUS, l, r);
      }
      {if (true) return l;}
    }
    throw new Error("Missing return statement in function");
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  final private boolean jj_3_2() {
    if (jj_scan_token(IDENT)) return true;
    if (jj_scan_token(LPAREN)) return true;
    return false;
  }

  final private boolean jj_3R_20() {
    if (jj_3R_23()) return true;
    return false;
  }

  final private boolean jj_3R_18() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_19()) {
    jj_scanpos = xsp;
    if (jj_3R_20()) {
    jj_scanpos = xsp;
    if (jj_3_2()) {
    jj_scanpos = xsp;
    if (jj_3R_21()) {
    jj_scanpos = xsp;
    if (jj_3R_22()) return true;
    }
    }
    }
    }
    return false;
  }

  final private boolean jj_3R_19() {
    if (jj_scan_token(LPAREN)) return true;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_scan_token(ELSE)) return true;
    if (jj_3R_8()) return true;
    return false;
  }

  final private boolean jj_3R_17() {
    if (jj_3R_18()) return true;
    return false;
  }

  final private boolean jj_3R_16() {
    if (jj_3R_17()) return true;
    return false;
  }

  final private boolean jj_3R_15() {
    if (jj_3R_16()) return true;
    return false;
  }

  final private boolean jj_3R_14() {
    if (jj_scan_token(IF)) return true;
    return false;
  }

  final private boolean jj_3R_13() {
    if (jj_scan_token(LB)) return true;
    return false;
  }

  final private boolean jj_3R_23() {
    if (jj_scan_token(DQUOTE1)) return true;
    return false;
  }

  final private boolean jj_3R_12() {
    if (jj_3R_15()) return true;
    return false;
  }

  final private boolean jj_3R_11() {
    if (jj_3R_14()) return true;
    return false;
  }

  final private boolean jj_3R_22() {
    if (jj_scan_token(NUMBER)) return true;
    return false;
  }

  final private boolean jj_3R_10() {
    if (jj_3R_13()) return true;
    return false;
  }

  final private boolean jj_3R_21() {
    if (jj_scan_token(IDENT)) return true;
    return false;
  }

  final private boolean jj_3R_9() {
    if (jj_scan_token(PRINT)) return true;
    return false;
  }

  final private boolean jj_3R_8() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_9()) {
    jj_scanpos = xsp;
    if (jj_3R_10()) {
    jj_scanpos = xsp;
    if (jj_3R_11()) {
    jj_scanpos = xsp;
    if (jj_3R_12()) return true;
    }
    }
    }
    return false;
  }

  public ESLLParserByJavaCCTokenManager token_source;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[17];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0xc00000,0xc00000,0x20000,0x1000000,0x11c2820,0x11c2820,0x8000,0xc0,0xc0,0x700,0x700,0x1040820,0x1040820,0x40800,0x1000020,0xa000000,0xa000000,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[2];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public ESLLParserByJavaCC(CharStream stream) {
    token_source = new ESLLParserByJavaCCTokenManager(stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(CharStream stream) {
    token_source.ReInit(stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public ESLLParserByJavaCC(ESLLParserByJavaCCTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(ESLLParserByJavaCCTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 17; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[28];
    for (int i = 0; i < 28; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 17; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 28; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 2; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}