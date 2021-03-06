/* Generated By:JavaCC: Do not edit this line. TurtleParserConstants.java */
/*
 * (c) Copyright 2006 Hewlett-Packard Development Company, LP
 * All rights reserved.
 */

package com.hp.hpl.jena.n3.turtle.parser ;

public interface TurtleParserConstants {

  int EOF = 0;
  int PLING = 1;
  int VBAR = 2;
  int CARROT = 3;
  int FPATH = 4;
  int RPATH = 5;
  int WS = 11;
  int SINGLE_LINE_COMMENT = 12;
  int KW_A = 13;
  int PREFIX = 14;
  int BASE = 15;
  int INTEGER = 16;
  int DECIMAL = 17;
  int DOUBLE = 18;
  int EXPONENT = 19;
  int QUOTE_3D = 20;
  int QUOTE_3S = 21;
  int ECHAR = 22;
  int STRING_LITERAL1 = 23;
  int STRING_LITERAL2 = 24;
  int STRING_LITERAL_LONG1 = 25;
  int STRING_LITERAL_LONG2 = 26;
  int DIGITS = 27;
  int HEX = 28;
  int Q_IRIref = 29;
  int QNAME_NS = 30;
  int QNAME = 31;
  int BLANK_NODE_LABEL = 32;
  int VAR1 = 33;
  int VAR2 = 34;
  int LANGTAG = 35;
  int A2Z = 36;
  int A2ZN = 37;
  int LPAREN = 38;
  int RPAREN = 39;
  int NIL = 40;
  int LBRACE = 41;
  int RBRACE = 42;
  int LBRACKET = 43;
  int RBRACKET = 44;
  int ANON = 45;
  int SEMICOLON = 46;
  int COMMA = 47;
  int DOT = 48;
  int EQ = 49;
  int ARROW = 50;
  int DOLLAR = 51;
  int QMARK = 52;
  int TILDE = 53;
  int COLON = 54;
  int STAR = 55;
  int SLASH = 56;
  int RSLASH = 57;
  int DATATYPE = 58;
  int AT = 59;
  int NCCHAR1p = 60;
  int NCCHAR1 = 61;
  int NCCHAR = 62;
  int NCNAME_PREFIX = 63;
  int NCNAME = 64;
  int VARNAME = 65;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\"!\"",
    "\"|\"",
    "\"^\"",
    "\"->\"",
    "\"<-\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<WS>",
    "<SINGLE_LINE_COMMENT>",
    "\"a\"",
    "\"@prefix\"",
    "\"@base\"",
    "<INTEGER>",
    "<DECIMAL>",
    "<DOUBLE>",
    "<EXPONENT>",
    "\"\\\"\\\"\\\"\"",
    "\"\\\'\\\'\\\'\"",
    "<ECHAR>",
    "<STRING_LITERAL1>",
    "<STRING_LITERAL2>",
    "<STRING_LITERAL_LONG1>",
    "<STRING_LITERAL_LONG2>",
    "<DIGITS>",
    "<HEX>",
    "<Q_IRIref>",
    "<QNAME_NS>",
    "<QNAME>",
    "<BLANK_NODE_LABEL>",
    "<VAR1>",
    "<VAR2>",
    "<LANGTAG>",
    "<A2Z>",
    "<A2ZN>",
    "\"(\"",
    "\")\"",
    "<NIL>",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "<ANON>",
    "\";\"",
    "\",\"",
    "\".\"",
    "\"=\"",
    "\"=>\"",
    "\"$\"",
    "\"?\"",
    "\"~\"",
    "\":\"",
    "\"*\"",
    "\"/\"",
    "\"\\\\\"",
    "\"^^\"",
    "\"@\"",
    "<NCCHAR1p>",
    "<NCCHAR1>",
    "<NCCHAR>",
    "<NCNAME_PREFIX>",
    "<NCNAME>",
    "<VARNAME>",
  };

}
