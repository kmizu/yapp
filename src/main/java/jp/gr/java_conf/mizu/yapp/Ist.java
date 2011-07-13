/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp;

import static jp.gr.java_conf.mizu.yapp.util.SystemProperties.LINE_SEPARATOR;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Represents Intermediate Syntax Tree(Ist).
 * Basically, this class has no member except class or interface.
 * @author Kota Mizushima
 *
 */
public class Ist {
  public abstract static class Node {
    private final Position pos;
    
    public Node(Position pos) {
      this.pos = pos;
    }
    
    public Position pos() {
      return pos;
    }
    
    public abstract <R, C> R accept(Visitor<R, C> visitor, C context);
  }
  
  public static class ParserUnit extends Node implements Iterable<Function> {
    private final Symbol name;
    private final Symbol startName;
    private final Symbol startType;
    private final List<Function> rules;
    private final Map<Symbol, Set<Character>> nameToCharSet;

    public ParserUnit(Position pos, Symbol name, Map<Symbol, Set<Character>> nameToCharSet, Symbol startName, Symbol startType, List<Function> rules) {
      super(pos);
      this.name = name;
      this.nameToCharSet = nameToCharSet;
      this.startName = startName;
      this.startType = startType;
      this.rules = rules;
    }

    public Symbol getName() {
      return name;
    }

    public Map<Symbol, Set<Character>> getNameToCharSet() {
      return nameToCharSet;
    }
    
    public Symbol getStartName() {
      return startName;
    }
    
    public Symbol getStartType() {
      return startType;
    }

    public List<Function> getRules() {
      return rules;
    }

    public Iterator<Function> iterator() {
      return rules.iterator();
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("parser ");
      builder.append(name);
      builder.append(LINE_SEPARATOR);
      builder.append(LINE_SEPARATOR);
      for(Function rule : rules){
        builder.append(rule);
        builder.append(LINE_SEPARATOR);
        builder.append(LINE_SEPARATOR);
      }
      return new String(builder);
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  
  public static class Function extends Node {
    private final Symbol name;
    private final Symbol type;
    private final String code;
    private final boolean memoized;
    private final Statement statement;
    
    public Function(
      Position pos, Symbol name, Symbol type, String code, boolean memoized, Statement statement
    ) {
      super(pos);
      this.name = name;
      this.type = type;
      this.code = code;
      this.memoized = memoized;
      this.statement = statement;
    }

    public Symbol getName() {
      return name;
    }

    public Symbol getType() {
      return type;
    }
    
    public String getCode() {
      return code;
    }
    
    public boolean isMemoized() {
      return memoized;
    }
    
    public Statement getStatement() {
      return statement;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public abstract static class Statement extends Node {
    public Statement(Position pos) {
      super(pos);
    }
  }
  
  public static class ActionStatement extends Statement {
    private String  code;

    public ActionStatement(Position pos, String code) {
      super(pos);
      this.code = code;
    }

    public String getCode() {
      return code;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class SetSemanticValue extends Statement {
    private String  code;

    public SetSemanticValue(Position pos, String code) {
      super(pos);
      this.code = code;
    }

    public String getCode() {
      return code;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class Block extends Statement {
    private final Symbol label;
    private final Statement[] statements;
    
    public Block(Position pos, Symbol label, Statement... statements) {
      super(pos);
      this.label = label;
      this.statements = statements;
    }
    
    public Block(Position pos, Symbol label, List<Statement> statements) {
      super(pos);
      this.label = label;
      this.statements = statements.toArray(new Statement[0]);
    }
    
    public Symbol getLabel() {
      return label;
    }
    
    public Statement[] getStatements() {
      return statements;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class EscapeFrom extends Statement {
    private final Symbol label;
    
    public EscapeFrom(Position pos, Symbol label) {
      super(pos);
      this.label = label;
    }
    
    public Symbol getLabel() {
      return label;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class Fail extends Statement {    
    public Fail(Position pos) {
      super(pos);
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }    
  }
  
  public static class MatchString extends Statement {
    private final Var var;
    private final String value;
    private final Symbol label;
    
    public MatchString(Position pos, Var var, String value, Symbol label) {
      super(pos);
      this.var = var;
      this.value = value;
      this.label = label;
    }
    
    public Var getVar() {
      return var;
    }
    
    public String getValue() {
      return value;
    }

    public Symbol getLabel() {
      return label;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class Accept extends Statement {
    public Accept(Position pos) {
      super(pos);
    }

    @Override
    public String toString() {
      return "accept;";
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class GenerateSuccess extends Statement {
    public GenerateSuccess(Position pos) {
      super(pos);
    }

    @Override
    public String toString() {
      return "generate_success;";
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class GenerateFailure extends Statement {
    private String expected;
    
    public GenerateFailure(Position pos, String message) {
      super(pos);
      this.expected = message;
    }
    
    public String expected() { return expected; }

    @Override
    public String toString() {
      return "generate_failure;";
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class MatchAny extends Statement {
    private final Var var;
    private final Symbol label;
    
    public MatchAny(Position pos, Var var, Symbol label) {
      super(pos);
      this.var = var;
      this.label = label;
    }
    
    public Var getVar() {
      return var;
    }
    
    public Symbol getLabel() {
      return label;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class MatchCharClass extends Statement {
    private final Symbol name;
    private final Var var;
    private final boolean positive;
    private final Symbol label;
    
    public MatchCharClass(Position pos, Symbol name, Var var, boolean positive, Symbol label) {
      super(pos);
      this.name = name;
      this.var = var;
      this.positive = positive;
      this.label = label;
    }
    
    public Symbol getName() {
      return name;
    }
    
    public Var getVar() {
      return var;
    }
    
    public boolean isPositive() {
      return positive;
    }
    
    public Symbol getLabel() {
      return label;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class NewCursorVar extends Statement {
    private final Symbol name;
    
    public NewCursorVar(Position pos, Symbol name) {
      super(pos);
      this.name = name;
    }
    
    public Symbol getName() {
      return name;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class Nop extends Statement {
    public Nop(Position pos) {
      super(pos);
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class Var {
    private final Symbol name;
    private final Symbol type;
    
    public Var(Symbol name, Symbol type) {
      this.name = name;
      this.type = type;
    }
    
    public Symbol getName() {
      return name;
    }
    
    public Symbol getType() {
      return type;
    }    
  }
  
  public static class MatchRule extends Statement {
    private final Var var;
    private final Symbol rule;
    private final Symbol label;
    
    public MatchRule(Position pos, Var var, Symbol rule, Symbol label) {
      super(pos);
      this.var = var;
      this.rule = rule;
      this.label = label;
    }
    
    public Var getVar() {
      return var;
    }
    
    public Symbol getRule() {
      return rule;
    }

    public Symbol getLabel() {
      return label;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class BackupCursor extends Statement {
    private final Symbol var;

    public BackupCursor(Position pos, Symbol var) {
      super(pos);
      this.var = var;
    }
    
    public Symbol getVar() {
      return var;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class IncrDepth extends Statement {
    public IncrDepth(Position pos) {
      super(pos);
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class DecrDepth extends Statement {
    public DecrDepth(Position pos) {
      super(pos);
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public static class RewindCursor extends Statement {
    private final Symbol var;

    public RewindCursor(Position pos, Symbol var) {
      super(pos);
      this.var = var;
    }
    
    public Symbol getVar() {
      return var;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }

  public static class Loop extends Statement {
    private final Symbol label;
    private final Statement[] statements;
    
    public Loop(Position pos, Symbol label, Statement... statements) {
      super(pos);
      this.label = label;
      this.statements = statements;
    }
    
    public Symbol getLabel() {
      return label;
    }
    
    public Statement[] getStatements() {
      return statements;
    }
    
    public <R, C> R accept(Visitor<R, C> visitor, C context) {
      return visitor.visit(this, context);
    }
  }
  
  public abstract static class Visitor<R, C> {
    public R visit(Accept node, C context) { return null; }
    public R visit(ActionStatement node, C context) { return null; }
    public R visit(BackupCursor node, C context) { return null; }
    public R visit(Block node, C context) { return null; }
    public R visit(DecrDepth node, C context) { return null; }
    public R visit(EscapeFrom node, C context) { return null; }
    public R visit(Fail node, C context) { return null; }
    public R visit(Function node, C context) { return null; }
    public R visit(GenerateSuccess node, C context) { return null; }
    public R visit(GenerateFailure node, C context) { return null; }
    public R visit(IncrDepth node, C context) { return null; }
    public R visit(Loop node, C context) { return null; }
    public R visit(MatchAny node, C context) { return null; }
    public R visit(MatchCharClass node, C context) { return null; }
    public R visit(MatchRule node, C context) { return null; }
    public R visit(MatchString node, C context) { return null; }
    public R visit(NewCursorVar node, C context) { return null; }
    public R visit(Nop node, C context) { return null; }
    public R visit(ParserUnit node, C context) { return null; }
    public R visit(RewindCursor node, C context) { return null; }
    public R visit(SetSemanticValue node, C context) { return null; }
  }
}
