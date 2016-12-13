package com.github.kmizu.yapp.tr;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Ast.CharClass;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.StringLiteral;
import com.github.kmizu.yapp.Ast.Wildcard;
import com.github.kmizu.yapp.util.CollectionUtil;

import com.github.kmizu.yapp.Position;

public class ExpressionUtil {

  public static Set<Expression> compact(Set<Expression> expSet) {
    List<Expression> expList = CollectionUtil.list();
    expList.addAll(expSet);
    Set<Expression> result = CollectionUtil.set();
    OUTER:
    for(int i = 0; i < expList.size();) {
      for(int j = i + 1; j < expList.size();) {
        if(AutoCutInserterUsingFirstSet.isPrefixOf(expList.get(i), expList.get(j))){
          expList.remove(j);
        }else if(AutoCutInserterUsingFirstSet.isPrefixOf(expList.get(j), expList.get(i))){
          expList.remove(i);
          continue OUTER;
        }else{
          j++;
        }        
      }
      i++;
    }
    result.addAll(expList);
    return result;
  }

  public static Expression newAlternation(Position pos, Set<Expression> elements) {
    List<Expression> newElements = CollectionUtil.list();
    newElements.addAll(elements);
    Collections.sort(newElements, new Comparator<Expression>() {
      public int compare(Expression o1, Expression o2) {
        if(o1 instanceof StringLiteral) {
          if(o2 instanceof CharClass) return 1;
          if(o2 instanceof Wildcard) return 1;
          int length1 = ((StringLiteral)o1).value().length();
          int length2 = ((StringLiteral)o2).value().length();
          return length1 > length2 ? -1 :
                  length1 < length2 ? 1 :
                  ((StringLiteral)o1).value().compareTo(((StringLiteral)o2).value());
        } 
        if(o1 instanceof CharClass){
          if(o2 instanceof StringLiteral) return -1;
          if(o2 instanceof Wildcard) return 1;
          return o1.toString().compareTo(o2.toString());          
        } 
        if(o1 instanceof Wildcard) {
          if(o2 instanceof StringLiteral) return -1;
          if(o2 instanceof CharClass) return -1;
          return 0;
        } 
        throw new RuntimeException("cannot reach here");
      }
    });
    return new Ast.N_Alternation(pos, newElements);
  }
  
  public static boolean maybePrefixOf(Expression e1, Expression e2) {
    if(e1 instanceof CharClass && e2 instanceof CharClass) {
      CharClass c1 = (CharClass)e1;
      CharClass c2 = (CharClass)e2;
      for(CharClass.Element ce:c1.elements){
        if(ce instanceof CharClass.Range) {
          char start = ((CharClass.Range)ce).start;
          char end = ((CharClass.Range)ce).end;
          for(int i = start; i<= end; i++) {
            if(c1.positive == AutoCutInserterUsingFirstSet.isContained((char)i, c2)) return true;
          }
        }else {
          if(c1.positive == AutoCutInserterUsingFirstSet.isContained(((CharClass.Char)ce).value, c2)) return true;
        }
      }
      return false;      
    } else if(e1 instanceof CharClass && e2 instanceof Wildcard) {
      return true;
    } else {
      return AutoCutInserterUsingFirstSet.isPrefixOf(e1, e2);
    }
  }

  public static boolean disjoint(Set<Expression> f1, Set<Expression> f2) {
    for(Expression e1:f1) {
      for(Expression e2:f2) {
        if(maybePrefixOf(e1, e2) || maybePrefixOf(e2, e1)) return false;
      }
    }
    return true;
  }

}
