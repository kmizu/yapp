/* ************************************************************** *
 *                                                                *
 * Copyright (c) 2005, Kota Mizushima, All rights reserved.       *
 *                                                                *
 *                                                                *
 * This software is distributed under the modified BSD License.   *
 * ************************************************************** */
package jp.gr.java_conf.mizu.yapp.tr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.gr.java_conf.mizu.yapp.Ast;
import jp.gr.java_conf.mizu.yapp.Ast.N_Alternation;
import jp.gr.java_conf.mizu.yapp.Ast.AndPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.Cut;
import jp.gr.java_conf.mizu.yapp.Ast.Empty;
import jp.gr.java_conf.mizu.yapp.Ast.Expression;
import jp.gr.java_conf.mizu.yapp.Ast.Fail;
import jp.gr.java_conf.mizu.yapp.Ast.NotPredicate;
import jp.gr.java_conf.mizu.yapp.Ast.Optional;
import jp.gr.java_conf.mizu.yapp.Ast.Repetition;
import jp.gr.java_conf.mizu.yapp.Ast.RepetitionPlus;
import jp.gr.java_conf.mizu.yapp.Ast.N_Sequence;

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
