package com.github.kmizu.yapp.translator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Symbol;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Grammar;
import com.github.kmizu.yapp.Ast.MacroCall;
import com.github.kmizu.yapp.Ast.MacroDefinition;
import com.github.kmizu.yapp.Ast.MacroVariable;

public class MacroExpander extends 
  AbstractGrammarExpander<MacroExpander.MacroEnvironment> implements Translator<Ast.Grammar, Ast.Grammar> {
  private final Map<Symbol, MacroDefinition> macroBindings = new HashMap<Symbol, MacroDefinition>();
  public static class MacroEnvironment {
    public final Map<Symbol, Expression> bindings = new HashMap<Symbol, Expression>();
  }
  @Override
  public Grammar translate(Grammar from) {
    return expand(from);
  }
  @Override
  public Grammar expand(Grammar node, MacroEnvironment env) {
    for(MacroDefinition macro:node.macros()){
      macroBindings.put(macro.name(), macro);
    }
    return super.expand(node, env);
  }
  @Override
  public MacroEnvironment newContext() {
    return new MacroEnvironment();
  }
  @Override
  protected Expression visit(MacroCall node, MacroEnvironment env) {
    MacroDefinition target = macroBindings.get(node.name());
    MacroEnvironment newEnv = new MacroEnvironment();
    List<Symbol> formalParams = target.formalParams();
    for(int i = 0; i < formalParams.size(); i++){
      newEnv.bindings.put(formalParams.get(i), node.params().get(i));
    }
    return target.body().accept(this, newEnv);
  }
  @Override
  protected Expression visit(MacroVariable node, MacroEnvironment env) {
    return env.bindings.get(node.name()).accept(this, env);
  }  
}
