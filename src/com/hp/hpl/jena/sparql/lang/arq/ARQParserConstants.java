/* Generated By:JavaCC: Do not edit this line. ARQParserConstants.java */
/*
 * (c) Copyright 2004, 2005, 2006, 2007 Hewlett-Packard Development Company, LP
 * All rights reserved.
 */

package com.hp.hpl.jena.sparql.lang.arq ;

public interface ARQParserConstants {

  int EOF = 0;
  int WS = 8;
  int SINGLE_LINE_COMMENT = 9;
  int Q_IRIref = 10;
  int QNAME_NS = 11;
  int QNAME_LN = 12;
  int BLANK_NODE_LABEL = 13;
  int VAR1 = 14;
  int VAR2 = 15;
  int LANGTAG = 16;
  int A2Z = 17;
  int A2ZN = 18;
  int KW_A = 19;
  int BASE = 20;
  int PREFIX = 21;
  int SELECT = 22;
  int DISTINCT = 23;
  int REDUCED = 24;
  int DESCRIBE = 25;
  int CONSTRUCT = 26;
  int ASK = 27;
  int LIMIT = 28;
  int OFFSET = 29;
  int ORDER = 30;
  int BY = 31;
  int AS = 32;
  int ASC = 33;
  int DESC = 34;
  int NAMED = 35;
  int FROM = 36;
  int WHERE = 37;
  int AND = 38;
  int GRAPH = 39;
  int OPTIONAL = 40;
  int UNION = 41;
  int UNSAID = 42;
  int EXT = 43;
  int FILTER = 44;
  int BOUND = 45;
  int STR = 46;
  int DTYPE = 47;
  int LANG = 48;
  int LANGMATCHES = 49;
  int IS_URI = 50;
  int IS_IRI = 51;
  int IS_BLANK = 52;
  int IS_LITERAL = 53;
  int REGEX = 54;
  int SAME_TERM = 55;
  int TRUE = 56;
  int FALSE = 57;
  int DIGITS = 58;
  int INTEGER = 59;
  int DECIMAL = 60;
  int DOUBLE = 61;
  int INTEGER_POSITIVE = 62;
  int DECIMAL_POSITIVE = 63;
  int DOUBLE_POSITIVE = 64;
  int INTEGER_NEGATIVE = 65;
  int DECIMAL_NEGATIVE = 66;
  int DOUBLE_NEGATIVE = 67;
  int EXPONENT = 68;
  int QUOTE_3D = 69;
  int QUOTE_3S = 70;
  int ECHAR = 71;
  int STRING_LITERAL1 = 72;
  int STRING_LITERAL2 = 73;
  int STRING_LITERAL_LONG1 = 74;
  int STRING_LITERAL_LONG2 = 75;
  int LPAREN = 76;
  int RPAREN = 77;
  int NIL = 78;
  int LBRACE = 79;
  int RBRACE = 80;
  int LBRACKET = 81;
  int RBRACKET = 82;
  int ANON = 83;
  int SEMICOLON = 84;
  int COMMA = 85;
  int DOT = 86;
  int EQ = 87;
  int NE = 88;
  int GT = 89;
  int LT = 90;
  int LE = 91;
  int GE = 92;
  int BANG = 93;
  int TILDE = 94;
  int COLON = 95;
  int SC_OR = 96;
  int SC_AND = 97;
  int PLUS = 98;
  int MINUS = 99;
  int STAR = 100;
  int SLASH = 101;
  int DATATYPE = 102;
  int AT = 103;
  int NCCHAR1P = 104;
  int NCCHAR1 = 105;
  int NCCHAR = 106;
  int NCNAME_PREFIX = 107;
  int NCNAME = 108;
  int VARNAME = 109;
  int UNKNOWN = 110;

  int DEFAULT = 0;

  String[] tokenImage = {
    "<EOF>",
    "\"<<\"",
    "\">>\"",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<WS>",
    "<SINGLE_LINE_COMMENT>",
    "<Q_IRIref>",
    "<QNAME_NS>",
    "<QNAME_LN>",
    "<BLANK_NODE_LABEL>",
    "<VAR1>",
    "<VAR2>",
    "<LANGTAG>",
    "<A2Z>",
    "<A2ZN>",
    "\"a\"",
    "\"base\"",
    "\"prefix\"",
    "\"select\"",
    "\"distinct\"",
    "\"reduced\"",
    "\"describe\"",
    "\"construct\"",
    "\"ask\"",
    "\"limit\"",
    "\"offset\"",
    "\"order\"",
    "\"by\"",
    "\"as\"",
    "\"asc\"",
    "\"desc\"",
    "\"named\"",
    "\"from\"",
    "\"where\"",
    "\"and\"",
    "\"graph\"",
    "\"optional\"",
    "\"union\"",
    "\"unsaid\"",
    "\"ext\"",
    "\"filter\"",
    "\"bound\"",
    "\"str\"",
    "\"datatype\"",
    "\"lang\"",
    "\"langmatches\"",
    "\"isURI\"",
    "\"isIRI\"",
    "\"isBlank\"",
    "\"isLiteral\"",
    "\"regex\"",
    "\"sameTerm\"",
    "\"true\"",
    "\"false\"",
    "<DIGITS>",
    "<INTEGER>",
    "<DECIMAL>",
    "<DOUBLE>",
    "<INTEGER_POSITIVE>",
    "<DECIMAL_POSITIVE>",
    "<DOUBLE_POSITIVE>",
    "<INTEGER_NEGATIVE>",
    "<DECIMAL_NEGATIVE>",
    "<DOUBLE_NEGATIVE>",
    "<EXPONENT>",
    "\"\\\"\\\"\\\"\"",
    "\"\\\'\\\'\\\'\"",
    "<ECHAR>",
    "<STRING_LITERAL1>",
    "<STRING_LITERAL2>",
    "<STRING_LITERAL_LONG1>",
    "<STRING_LITERAL_LONG2>",
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
    "\"!=\"",
    "\">\"",
    "\"<\"",
    "\"<=\"",
    "\">=\"",
    "\"!\"",
    "\"~\"",
    "\":\"",
    "\"||\"",
    "\"&&\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"^^\"",
    "\"@\"",
    "<NCCHAR1P>",
    "<NCCHAR1>",
    "<NCCHAR>",
    "<NCNAME_PREFIX>",
    "<NCNAME>",
    "<VARNAME>",
    "<UNKNOWN>",
  };

}
