/* Generated By:JavaCC: Do not edit this line. YappParserTokenManager.java */
package jp.gr.java_conf.mizu.yapp.parser;
import java.util.*;
import jp.gr.java_conf.mizu.yapp.*;
import jp.gr.java_conf.mizu.yapp.parser.*;
import static jp.gr.java_conf.mizu.yapp.Ast.*;

public class YappParserTokenManager implements YappParserConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x4000L) != 0L)
            return 34;
         if ((active0 & 0x780L) != 0L)
         {
            jjmatchedKind = 32;
            return 8;
         }
         if ((active0 & 0x1000L) != 0L)
            return 14;
         if ((active0 & 0x80000L) != 0L)
            return 59;
         return -1;
      case 1:
         if ((active0 & 0x780L) != 0L)
         {
            jjmatchedKind = 32;
            jjmatchedPos = 1;
            return 8;
         }
         return -1;
      case 2:
         if ((active0 & 0x780L) != 0L)
         {
            jjmatchedKind = 32;
            jjmatchedPos = 2;
            return 8;
         }
         return -1;
      case 3:
         if ((active0 & 0x100L) != 0L)
            return 8;
         if ((active0 & 0x680L) != 0L)
         {
            jjmatchedKind = 32;
            jjmatchedPos = 3;
            return 8;
         }
         return -1;
      case 4:
         if ((active0 & 0x400L) != 0L)
            return 8;
         if ((active0 & 0x280L) != 0L)
         {
            jjmatchedKind = 32;
            jjmatchedPos = 4;
            return 8;
         }
         return -1;
      case 5:
         if ((active0 & 0x280L) != 0L)
         {
            jjmatchedKind = 32;
            jjmatchedPos = 5;
            return 8;
         }
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 33:
         return jjStopAtPos(0, 15);
      case 38:
         return jjStartNfaWithStates_0(0, 14, 34);
      case 40:
         jjmatchedKind = 23;
         return jjMoveStringLiteralDfa1_0(0x400000L);
      case 41:
         return jjStopAtPos(0, 24);
      case 42:
         return jjStopAtPos(0, 20);
      case 43:
         return jjStopAtPos(0, 21);
      case 44:
         return jjStopAtPos(0, 28);
      case 47:
         return jjStartNfaWithStates_0(0, 19, 59);
      case 58:
         return jjStopAtPos(0, 17);
      case 59:
         return jjStopAtPos(0, 29);
      case 60:
         return jjMoveStringLiteralDfa1_0(0x1000L);
      case 61:
         return jjStopAtPos(0, 13);
      case 63:
         return jjStopAtPos(0, 16);
      case 94:
         return jjStopAtPos(0, 27);
      case 98:
         return jjMoveStringLiteralDfa1_0(0x200L);
      case 102:
         return jjMoveStringLiteralDfa1_0(0x100L);
      case 103:
         return jjMoveStringLiteralDfa1_0(0x80L);
      case 109:
         return jjMoveStringLiteralDfa1_0(0x400L);
      case 123:
         return jjStopAtPos(0, 25);
      case 124:
         return jjStopAtPos(0, 18);
      case 125:
         return jjStopAtPos(0, 26);
      default :
         return jjMoveNfa_0(0, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 41:
         if ((active0 & 0x400000L) != 0L)
            return jjStopAtPos(1, 22);
         break;
      case 45:
         if ((active0 & 0x1000L) != 0L)
            return jjStopAtPos(1, 12);
         break;
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x500L);
      case 111:
         return jjMoveStringLiteralDfa2_0(active0, 0x200L);
      case 114:
         return jjMoveStringLiteralDfa2_0(active0, 0x80L);
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x80L);
      case 99:
         return jjMoveStringLiteralDfa3_0(active0, 0x400L);
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x100L);
      case 117:
         return jjMoveStringLiteralDfa3_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 108:
         if ((active0 & 0x100L) != 0L)
            return jjStartNfaWithStates_0(3, 8, 8);
         break;
      case 109:
         return jjMoveStringLiteralDfa4_0(active0, 0x80L);
      case 110:
         return jjMoveStringLiteralDfa4_0(active0, 0x200L);
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x400L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 100:
         return jjMoveStringLiteralDfa5_0(active0, 0x200L);
      case 109:
         return jjMoveStringLiteralDfa5_0(active0, 0x80L);
      case 111:
         if ((active0 & 0x400L) != 0L)
            return jjStartNfaWithStates_0(4, 10, 8);
         break;
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 97:
         return jjMoveStringLiteralDfa6_0(active0, 0x80L);
      case 101:
         return jjMoveStringLiteralDfa6_0(active0, 0x200L);
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 100:
         if ((active0 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(6, 9, 8);
         break;
      case 114:
         if ((active0 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(6, 7, 8);
         break;
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0xfffffffffffffffeL, 0xffffffffffffffffL, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static final long[] jjbitVec2 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 67;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if (curChar == 47)
                     jjAddStates(0, 1);
                  else if (curChar == 39)
                     jjCheckNAddStates(2, 4);
                  else if (curChar == 34)
                     jjCheckNAddStates(5, 7);
                  else if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 34;
                  else if (curChar == 36)
                     jjstateSet[jjnewStateCnt++] = 29;
                  else if (curChar == 37)
                     jjstateSet[jjnewStateCnt++] = 23;
                  else if (curChar == 60)
                     jjstateSet[jjnewStateCnt++] = 14;
                  else if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 2;
                  else if (curChar == 46)
                  {
                     if (kind > 11)
                        kind = 11;
                  }
                  if (curChar == 36)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 59:
                  if (curChar == 47)
                  {
                     if (kind > 6)
                        kind = 6;
                     jjCheckNAdd(66);
                  }
                  else if (curChar == 42)
                     jjCheckNAddTwoStates(60, 61);
                  break;
               case 1:
                  if (curChar == 35)
                     jjstateSet[jjnewStateCnt++] = 2;
                  break;
               case 3:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 30)
                     kind = 30;
                  jjstateSet[jjnewStateCnt++] = 3;
                  break;
               case 4:
                  if (curChar == 36)
                     jjstateSet[jjnewStateCnt++] = 5;
                  break;
               case 6:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 31)
                     kind = 31;
                  jjstateSet[jjnewStateCnt++] = 6;
                  break;
               case 8:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 32)
                     kind = 32;
                  jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 10:
                  if ((0xffffffffffffdbffL & l) != 0L)
                     jjCheckNAddStates(8, 10);
                  break;
               case 12:
                  if (curChar == 45)
                     jjCheckNAddStates(8, 10);
                  break;
               case 16:
                  if ((0xbfffffffffffffffL & l) != 0L)
                     jjCheckNAddStates(11, 14);
                  break;
               case 17:
                  jjCheckNAddStates(11, 14);
                  break;
               case 19:
                  if (curChar == 62)
                     jjCheckNAddStates(11, 14);
                  break;
               case 20:
                  if (curChar == 62 && kind > 34)
                     kind = 34;
                  break;
               case 22:
                  if (curChar == 60)
                     jjstateSet[jjnewStateCnt++] = 14;
                  break;
               case 24:
                  jjCheckNAddStates(15, 17);
                  break;
               case 28:
                  if (curChar == 37)
                     jjstateSet[jjnewStateCnt++] = 23;
                  break;
               case 30:
                  jjCheckNAddStates(18, 20);
                  break;
               case 33:
                  if (curChar == 36)
                     jjstateSet[jjnewStateCnt++] = 29;
                  break;
               case 35:
                  if ((0xfffffff7ffffffffL & l) != 0L)
                     jjAddStates(21, 23);
                  break;
               case 39:
                  if (curChar == 38)
                     jjstateSet[jjnewStateCnt++] = 34;
                  break;
               case 40:
                  if (curChar == 34)
                     jjCheckNAddStates(5, 7);
                  break;
               case 41:
                  if ((0xfffffffbffffdbffL & l) != 0L)
                     jjCheckNAddStates(5, 7);
                  break;
               case 43:
                  if ((0x8400000000L & l) != 0L)
                     jjCheckNAddStates(5, 7);
                  break;
               case 44:
                  if (curChar == 34 && kind > 37)
                     kind = 37;
                  break;
               case 45:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(24, 27);
                  break;
               case 46:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(5, 7);
                  break;
               case 47:
                  if ((0xf000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 48;
                  break;
               case 48:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(46);
                  break;
               case 49:
                  if (curChar == 39)
                     jjCheckNAddStates(2, 4);
                  break;
               case 50:
                  if ((0xffffff7fffffdbffL & l) != 0L)
                     jjCheckNAddStates(2, 4);
                  break;
               case 52:
                  if ((0x8400000000L & l) != 0L)
                     jjCheckNAddStates(2, 4);
                  break;
               case 53:
                  if (curChar == 39 && kind > 37)
                     kind = 37;
                  break;
               case 54:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(28, 31);
                  break;
               case 55:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAddStates(2, 4);
                  break;
               case 56:
                  if ((0xf000000000000L & l) != 0L)
                     jjstateSet[jjnewStateCnt++] = 57;
                  break;
               case 57:
                  if ((0xff000000000000L & l) != 0L)
                     jjCheckNAdd(55);
                  break;
               case 58:
                  if (curChar == 47)
                     jjAddStates(0, 1);
                  break;
               case 60:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(60, 61);
                  break;
               case 61:
                  if (curChar == 42)
                     jjCheckNAddStates(32, 34);
                  break;
               case 62:
                  if ((0xffff7bffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(63, 61);
                  break;
               case 63:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(63, 61);
                  break;
               case 64:
                  if (curChar == 47 && kind > 5)
                     kind = 5;
                  break;
               case 65:
                  if (curChar != 47)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAdd(66);
                  break;
               case 66:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjCheckNAdd(66);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 0:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 32)
                        kind = 32;
                     jjCheckNAdd(8);
                  }
                  else if (curChar == 91)
                     jjCheckNAddStates(8, 10);
                  if (curChar == 95)
                  {
                     if (kind > 11)
                        kind = 11;
                  }
                  break;
               case 2:
               case 3:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 30)
                     kind = 30;
                  jjCheckNAdd(3);
                  break;
               case 5:
               case 6:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 31)
                     kind = 31;
                  jjCheckNAdd(6);
                  break;
               case 7:
               case 8:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 32)
                     kind = 32;
                  jjCheckNAdd(8);
                  break;
               case 9:
                  if (curChar == 91)
                     jjCheckNAddStates(8, 10);
                  break;
               case 10:
                  if ((0xffffffffcfffffffL & l) != 0L)
                     jjCheckNAddStates(8, 10);
                  break;
               case 11:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 12;
                  break;
               case 12:
                  if ((0x14404438000000L & l) != 0L)
                     jjCheckNAddStates(8, 10);
                  break;
               case 13:
                  if (curChar == 93 && kind > 33)
                     kind = 33;
                  break;
               case 14:
                  if (curChar == 91)
                     jjCheckNAddStates(11, 14);
                  break;
               case 15:
                  if (curChar == 93)
                     jjstateSet[jjnewStateCnt++] = 16;
                  break;
               case 16:
                  jjCheckNAddStates(11, 14);
                  break;
               case 17:
                  if ((0xffffffffcfffffffL & l) != 0L)
                     jjCheckNAddStates(11, 14);
                  break;
               case 18:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 19;
                  break;
               case 19:
                  if ((0x30000000L & l) != 0L)
                     jjCheckNAddStates(11, 14);
                  break;
               case 21:
                  if (curChar == 93)
                     jjstateSet[jjnewStateCnt++] = 20;
                  break;
               case 23:
                  if (curChar == 123)
                     jjCheckNAddStates(15, 17);
                  break;
               case 24:
                  if ((0xdfffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(15, 17);
                  break;
               case 25:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 26;
                  break;
               case 26:
                  if ((0x2000000010000000L & l) != 0L)
                     jjCheckNAddStates(15, 17);
                  break;
               case 27:
                  if (curChar == 125 && kind > 35)
                     kind = 35;
                  break;
               case 29:
                  if (curChar == 123)
                     jjCheckNAddStates(18, 20);
                  break;
               case 30:
                  if ((0xdfffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(18, 20);
                  break;
               case 31:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 32;
                  break;
               case 32:
                  if ((0x2000000010000000L & l) != 0L)
                     jjCheckNAddStates(18, 20);
                  break;
               case 34:
                  if (curChar == 123)
                     jjCheckNAddStates(21, 23);
                  break;
               case 35:
                  if ((0xdfffffffffffffffL & l) != 0L)
                     jjCheckNAddStates(21, 23);
                  break;
               case 36:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 37;
                  break;
               case 37:
                  if ((0x2000000010000000L & l) != 0L)
                     jjCheckNAddStates(21, 23);
                  break;
               case 38:
                  if (curChar == 125 && kind > 36)
                     kind = 36;
                  break;
               case 41:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(5, 7);
                  break;
               case 42:
                  if (curChar == 92)
                     jjAddStates(35, 37);
                  break;
               case 43:
                  if ((0x14404410000000L & l) != 0L)
                     jjCheckNAddStates(5, 7);
                  break;
               case 50:
                  if ((0xffffffffefffffffL & l) != 0L)
                     jjCheckNAddStates(2, 4);
                  break;
               case 51:
                  if (curChar == 92)
                     jjAddStates(38, 40);
                  break;
               case 52:
                  if ((0x14404410000000L & l) != 0L)
                     jjCheckNAddStates(2, 4);
                  break;
               case 60:
                  jjCheckNAddTwoStates(60, 61);
                  break;
               case 62:
               case 63:
                  jjCheckNAddTwoStates(63, 61);
                  break;
               case 66:
                  if (kind > 6)
                     kind = 6;
                  jjstateSet[jjnewStateCnt++] = 66;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int hiByte = (int)(curChar >> 8);
         int i1 = hiByte >> 6;
         long l1 = 1L << (hiByte & 077);
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 10:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjAddStates(8, 10);
                  break;
               case 16:
               case 17:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(11, 14);
                  break;
               case 24:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(15, 17);
                  break;
               case 30:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddStates(18, 20);
                  break;
               case 35:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjAddStates(21, 23);
                  break;
               case 41:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjAddStates(5, 7);
                  break;
               case 50:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjAddStates(2, 4);
                  break;
               case 60:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(60, 61);
                  break;
               case 62:
               case 63:
                  if (jjCanMove_0(hiByte, i1, i2, l1, l2))
                     jjCheckNAddTwoStates(63, 61);
                  break;
               case 66:
                  if (!jjCanMove_0(hiByte, i1, i2, l1, l2))
                     break;
                  if (kind > 6)
                     kind = 6;
                  jjstateSet[jjnewStateCnt++] = 66;
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 67 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   59, 65, 50, 51, 53, 41, 42, 44, 10, 11, 13, 15, 17, 18, 21, 24, 
   25, 27, 30, 31, 27, 35, 36, 38, 41, 42, 46, 44, 50, 51, 55, 53, 
   61, 62, 64, 43, 45, 47, 52, 54, 56, 
};
private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2)
{
   switch(hiByte)
   {
      case 0:
         return ((jjbitVec2[i2] & l2) != 0L);
      default : 
         if ((jjbitVec0[i1] & l1) != 0L)
            return true;
         return false;
   }
}
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, "\147\162\141\155\155\141\162", 
"\146\141\151\154", "\142\157\165\156\144\145\144", "\155\141\143\162\157", null, "\74\55", "\75", 
"\46", "\41", "\77", "\72", "\174", "\57", "\52", "\53", "\50\51", "\50", "\51", 
"\173", "\175", "\136", "\54", "\73", null, null, null, null, null, null, null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x3fffffff81L, 
};
static final long[] jjtoSkip = {
   0x7eL, 
};
static final long[] jjtoSpecial = {
   0x60L, 
};
protected JavaCharStream input_stream;
private final int[] jjrounds = new int[67];
private final int[] jjstateSet = new int[134];
protected char curChar;
public YappParserTokenManager(JavaCharStream stream){
   if (JavaCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public YappParserTokenManager(JavaCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(JavaCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 67; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(JavaCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      matchedToken.specialToken = specialToken;
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         matchedToken.specialToken = specialToken;
         return matchedToken;
      }
      else
      {
         if ((jjtoSpecial[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
         {
            matchedToken = jjFillToken();
            if (specialToken == null)
               specialToken = matchedToken;
            else
            {
               matchedToken.specialToken = specialToken;
               specialToken = (specialToken.next = matchedToken);
            }
         }
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}