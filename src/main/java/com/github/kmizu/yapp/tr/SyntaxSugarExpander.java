/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package com.github.kmizu.yapp.tr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.kmizu.yapp.Ast;
import com.github.kmizu.yapp.Ast.N_Alternation;
import com.github.kmizu.yapp.Ast.AndPredicate;
import com.github.kmizu.yapp.Ast.Cut;
import com.github.kmizu.yapp.Ast.Empty;
import com.github.kmizu.yapp.Ast.Expression;
import com.github.kmizu.yapp.Ast.Fail;
import com.github.kmizu.yapp.Ast.NotPredicate;
import com.github.kmizu.yapp.Ast.Optional;
import com.github.kmizu.yapp.Ast.Repetition;
import com.github.kmizu.yapp.Ast.RepetitionPlus;
import com.github.kmizu.yapp.Ast.N_Sequence;

public class SyntaxSugarExpander extends AbstractGrammarExpander<Void> {  
  @Override
  protected Expression visit(AndPredicate node, Void context) {
    return new NotPredicate(
      node.pos(),
      new NotPredicate(
        node.pos(),
        node.body().accept(this, context)
      )
    ).accept(this, context);
  }

  @Override
  protected Expression visit(Optional node, Void context) {
    return new N_Alternation(
      node.pos(), list(
        node.body().accept(this, context),
        new Empty(node.pos())
      )
    );
  }

  @Override
  protected Expression visit(RepetitionPlus node, Void context) {
    Expression expanded = node.body().accept(this, context);
    return new N_Sequence(
      node.pos(), list(
        expanded,
        new Repetition(node.pos(), expanded)
      )
    );
  }

  private static <T> List<T> list(T... args) {
    return new ArrayList<T>(Arrays.asList(args));
  }
}
