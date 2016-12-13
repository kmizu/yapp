package com.github.kmizu.yapp.benchmark;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.github.kmizu.esll.Ast.BinaryExpression;
import com.github.kmizu.esll.Ast.BlockStatement;
import com.github.kmizu.esll.Ast.ExpressionStatement;
import com.github.kmizu.esll.Ast.FunctionCall;
import com.github.kmizu.esll.Ast.FunctionDeclaration;
import com.github.kmizu.esll.Ast.Identifier;
import com.github.kmizu.esll.Ast.IfStatement;
import com.github.kmizu.esll.Ast.NumberLiteral;
import com.github.kmizu.esll.Ast.Operator;
import com.github.kmizu.esll.Ast.PrintStatement;
import com.github.kmizu.esll.Ast.Statement;
import com.github.kmizu.esll.Ast.StringLiteral;
import com.github.kmizu.esll.parser.BackupCharStream;
import com.github.kmizu.esll.parser.ESLLParserByJavaCC;
import com.github.kmizu.esll.parser.ParseException;

public class ESLLInterpreter {
  public static final class UndefinedVariableException extends RuntimeException {
    public final String variableName;
    public UndefinedVariableException(String variableName) {
      super("undefined variable `" + variableName + "`");
      this.variableName = variableName;      
    }
  }
  public static final class InvalidTypeException extends RuntimeException {
    public InvalidTypeException(String expected) {
      super("expected: " + expected);
    }
  }
  private final class GlobalEnvironment {
    public final Map<String, Ast.FunctionDeclaration> functions;
    public final Map<String, Object> globals;

    public GlobalEnvironment(Map<String, Ast.FunctionDeclaration> functions, Map<String, Object> globals) {
      super();
      this.functions = functions;
      this.globals = globals;
    }        
  }
  private final class Environment {
    public final Environment parent;
    public final Map<String, Object> bindings;
    
    public Environment(Environment parent, Map<String, Object> bindings) {
      this.parent = parent;
      this.bindings = bindings;
    }
    
    public Environment(Map<String, Object> bindings) {
      this(null, bindings);
    }    
  }
  private final class InterpreterCore extends Ast.Visitor<Object, Environment> {
    private final GlobalEnvironment global;
    InterpreterCore(GlobalEnvironment global) {
      this.global = global;
    }
    @Override
    public Object visit(BinaryExpression exp, Environment context) {
      if(exp.op == Operator.ASSIGN) {
        Identifier lhs = (Identifier) exp.lhs;
        Object value = exp.rhs.accept(this, context);
        if(global.globals.containsKey(lhs.name)){
          global.globals.put(lhs.name, value);
        }else {
          context.bindings.put(lhs.name, value);          
        }
        return value;
      }else {
        Object lvalue = exp.lhs.accept(this, context);
        Object rvalue = exp.rhs.accept(this, context);
        switch(exp.op) {
        case PLUS:
          if(lvalue instanceof Integer && rvalue instanceof Integer){
            return ((Integer)lvalue).intValue() + ((Integer)rvalue).intValue();
          }else {
            return lvalue.toString() + rvalue.toString();
          }
        case MINUS:
          if(lvalue instanceof Integer && rvalue instanceof Integer){
            return ((Integer)lvalue).intValue() - ((Integer)rvalue).intValue();
          }else {
            throw new InvalidTypeException("Number");
          }
        case MULT:
          if(lvalue instanceof Integer && rvalue instanceof Integer){
            return ((Integer)lvalue).intValue() * ((Integer)rvalue).intValue();
          }else {
            throw new InvalidTypeException("Number");
          }
        case DIV:
          if(lvalue instanceof Integer && rvalue instanceof Integer){
            return ((Integer)lvalue).intValue() / ((Integer)rvalue).intValue();
          }else {
            throw new InvalidTypeException("Number");
          }
        default:
          throw new RuntimeException("must not reach here");
        }
      }
    }
    
    @Override
    public Object visit(FunctionCall exp, Environment context) {
      FunctionDeclaration target = global.functions.get(exp.name);
      Map<String, Object> bindings = new HashMap<String, Object>();
      for(int i = 0; i < exp.params.size(); i++) {
        bindings.put(target.params.get(i), exp.params.get(i));
      }
      return target.body.accept(this, new Environment(bindings));
    }

    @Override
    public Object visit(BlockStatement stmt, Environment context) {
      for(Statement elements:stmt.elements) {
        elements.accept(this, context);
      }
      return null;
    }

    @Override
    public Object visit(ExpressionStatement stmt, Environment context) {
      stmt.accept(this, context);
      return null;
    }

    @Override
    public Object visit(Identifier exp, Environment context) {
      Object value = context.bindings.get(exp.name);
      if(value == null) {
        value = global.globals.get(exp.name);
        if(value == null) {
          throw new UndefinedVariableException(exp.name);
        }
      }
      return value;
    }

    @Override
    public Object visit(IfStatement stmt, Environment context) {
      Object cvalue = stmt.condition.accept(this, context);
      if(!cvalue.equals(0)) {
        stmt.thenBody.accept(this, context);
      }else if(stmt.elseBody != null) {
        stmt.elseBody.accept(this, context);          
      }
      return null;
    }

    @Override
    public Object visit(NumberLiteral exp, Environment context) {
      return exp.value;
    }

    @Override
    public Object visit(PrintStatement stmt, Environment context) {
      Object arg = stmt.arg.accept(this, context);
      System.out.println(arg);
      return null;
    }

    @Override
    public Object visit(StringLiteral exp, Environment context) {
      return exp.value;
    }    
  }
  public void run(Ast.Program program) {
    Map<String, Object> globals = new HashMap<String, Object>();
    Map<String, Ast.FunctionDeclaration> functions = new HashMap<String, Ast.FunctionDeclaration>();
    for(Ast.Declaration declaration:program.declarations) {
      if(declaration instanceof Ast.FunctionDeclaration) {
        Ast.FunctionDeclaration fun = (Ast.FunctionDeclaration)declaration;
        functions.put(fun.name, fun);
      }else if(declaration instanceof Ast.GlobalVariableDeclaration) {
        Ast.GlobalVariableDeclaration global = (Ast.GlobalVariableDeclaration)declaration;
        globals.put(global.name, 0);
      }
    }
    InterpreterCore interpreter = new InterpreterCore(
      new GlobalEnvironment(functions, globals)
    );
    FunctionDeclaration mainFunction = functions.get("main");
    Environment env = new Environment(new HashMap<String, Object>());
    mainFunction.body.accept(interpreter, env);
  }
  public void eval(Statement program) {
    eval(program, new Environment(new HashMap<String, Object>()));
  }
  public void eval(Statement program, Environment env) {
    InterpreterCore core = new InterpreterCore(null);    
    program.accept(core, env);
  }
  public void eval(String program) throws ParseException {
    ESLLParserByJavaCC parser = new ESLLParserByJavaCC(
      new BackupCharStream(new StringReader(program))
    );
    eval(parser.statement());
  }
  public static void main(String[] args) throws Exception {
    new ESLLInterpreter().eval("{ print(\"1 + 2 = #{\"#{1 + 2}\"}\"); }");
  }
}
