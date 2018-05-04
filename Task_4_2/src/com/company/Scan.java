package com.company;


class Scan {
    private static int NAME_LENGTH = 31;

    private final static int lexName = 0, lexAbstract = 1, lexNum = 2, lexAssert = 3,
            lexBoolean = 4, lexBreak = 5, lexByte = 6, lexCase = 7,
            lexCatch = 8, lexChar = 9, lexClass = 10, lexConst = 11, lexContinue = 12,
            lexDefault = 13, lexDo = 14, lexMult = 15, lexBegin = 16,
            lexEnd = 17, lexPlus = 18, lexMinus = 19, lexEQ = 20, lexNE = 21,
            lexLT = 22, lexLE = 23, lexGT = 24, lexGE = 25, lexDot = 26,
            lexComma = 27, lexColon = 28, lexSemi = 29, lexAss = 30, lexLPar = 31,
            lexRPar = 32, lexEOT = 33, lexDouble = 34, lexElse = 35, lexEnum = 36,
            lexExtends = 37, lexFinal = 38, lexFinally = 39, lexFloat = 40, lexFor = 41,
            lexGoTo = 42, lexIf = 43, lexImplements = 44, lexImport = 45, lexInstanceOf = 46,
            lexInt = 47, lexInterface = 48, lexLong = 49, lexNative = 50, lexNew = 51,
            lexPackage = 52, lexPrivate = 53, lexProtected = 54, lexPublic = 55, lexReturn = 56,
            lexShort = 57, lexStatic = 58, lexStrictFP = 59, lexSuper = 60, lexSwitch = 61,
            lexSynchronized = 62, lexThis = 63, lexThrow = 64, lexThrows = 65, lexTransient = 66,
            lexTry = 67, lexVoid = 68, lexVolatile = 69, lexWhile = 70, lexLBracket = 71,
            lexRBracket = 72, lexDiv = 73, lexMod = 74, lexDecrement = 75, lexIncrement = 76,
            lexAnd = 77, lexOr = 78, lexBitwiseExclusiveOr = 79, lexLShift = 80, lexRShift = 81,
            lexRShiftWithFillZeros = 82, lexPlusWithAss = 83, lexMinusWithAss = 84,
            lexMultWithAss = 85, lexDivWithAss = 86, lexModWithAss = 87, lexAndWithAss = 88,
            lexOrWithAss = 89, lexBitwiseExclusiveOrWithAss = 90, lexRShiftWithFillZerosWithAss = 91,
            lexLShiftWithAss = 92, lexRShiftWithAss = 93, lexNot = 94, lexBitwiseNot = 95,
            lexDoubleAnd = 96, lexDoubleOr = 97, lexShortIf = 98, lexLambda = 99,
            lexDog = 100, lexLinkToMethod = 101, lexTripleDot = 102, lexString = 103,
            lexCharacter = 104, lexUnicode = 105, lexNull = 106, lexTrue = 107, lexFalse = 108,
            lexBackSlash = 109, lexBS = 110, lexHT = 111, lexLF = 112, lexFF = 113, lexCR = 114,
            lexDoubleQuote = 115, lexQuote = 116;


    static int Lex;

    private static StringBuffer Buf = new StringBuffer(NAME_LENGTH);

    private static String Name;

    private static int KWNUM = 53;
    private static int nkw = 0;

    private static class Item {
        private String Word;
        private int Lex;
    }

    private static Item[] KWTable = new Item[KWNUM];

    private static void EnterKW(String Name, int Lex) {
        (KWTable[nkw] = new Item()).Word = Name;
        KWTable[nkw++].Lex = Lex;
    }

    private static int TestKW() {
        for (int i = nkw - 1; i >= 0; i--)
            if (KWTable[i].Word.compareTo(Name) == 0)
                return KWTable[i].Lex;
        return lexName;
    }

    private static void Ident() {
        int i = 0;
        Buf.setLength(0);
        do {
            if (i++ < NAME_LENGTH)
                Buf.append((char) Text.Ch);
            else
                Error.Message("Слишком длинное имя");
            Text.NextCh();
        } while (Character.isLetterOrDigit((char) Text.Ch));
        Name = Buf.toString();
        Lex = TestKW();
    }

    private static void Number() {
        Lex = lexNum;
        int numInt = 0;
        double numDouble = 0;

        if (Text.Ch == '0') {
            Text.NextCh();
            if (Text.Ch == 'x' || Text.Ch == 'X') {
                Text.NextCh();
                if (!Character.isDigit((char) Text.Ch) && (Text.Ch > 'F' || Text.Ch < 'A') && (Text.Ch > 'f' || Text.Ch < 'a'))
                    Error.Expected("Недопустимый символ");
                String str = "";
                do {
                    str += (char) Text.Ch;
                    int d = Integer.parseInt(str, 16);

                    if ((Integer.MAX_VALUE - d) < 0)
                        Error.Message("Слишком большое число");

                    Text.NextCh();
                }
                while (Character.isDigit((char) Text.Ch) || (Text.Ch <= 'F' && Text.Ch >= 'A') || (Text.Ch <= 'f' && Text.Ch >= 'a'));
            } else if (Text.Ch == 'b' || Text.Ch == 'B') {
                Text.NextCh();
                if (Text.Ch != '1' && Text.Ch != '0')
                    Error.Expected("Недопустимый символ");
                String str = "";
                do {
                    str += (char) Text.Ch;
                    int d = Integer.parseInt(str, 2);

                    if ((Integer.MAX_VALUE - d) < 0)
                        Error.Message("Слишком большое число");

                    Text.NextCh();
                } while (Text.Ch == '1' || Text.Ch == '0');
            } else if (Text.Ch <= '7' && Text.Ch >= '0') {
                String str = "";
                do {
                    str += (char) Text.Ch;
                    int d = Integer.parseInt(str, 8);

                    if ((Integer.MAX_VALUE - d) < 0)
                        Error.Message("Слишком большое число");

                    Text.NextCh();
                } while (Text.Ch <= '7' && Text.Ch >= '0');
            } else if (Text.Ch == '.') {
                Text.NextCh();
                numberDouble(0);
            } else elevateE();
        } else
            do {
                int d = Text.Ch - '0';
                if ((Integer.MAX_VALUE - d) / 10 >= numInt)
                    numInt = 10 * numInt + d;
                else
                    Error.Message("Слишком большое число");
                Text.NextCh();

                if (Text.Ch == 'L' || Text.Ch == 'l')
                    Text.NextCh();
                else if (Text.Ch == '.') {
                    Text.NextCh();
                    numberDouble(numInt);
                } else elevateE();
            } while (Character.isDigit((char) Text.Ch) || Text.Ch == '_');
    }

    private static void numberDouble(int numInt) {
        double numDouble;
        if (Character.isDigit(Text.Ch)) {
            numDouble = numInt;
            int i = 1;
            do {
                double d2 = Text.Ch - '0';
                numDouble = numDouble + d2 / Math.pow(10, i);
                i++;
                Text.NextCh();
                if (Text.Ch == 'F' || Text.Ch == 'f')
                    Text.NextCh();
                else elevateE();
            } while (Character.isDigit((char) Text.Ch) || Text.Ch == '_');
        }
    }

    private static void elevateE() {
        if (Text.Ch == 'E' || Text.Ch == 'e') {
            Text.NextCh();
            if (Text.Ch == '-')
                Text.NextCh();
            if (Character.isDigit(Text.Ch)) {
                int num = 0;
                do {
                    int d3 = Text.Ch - '0';
                    num = 10 * num + d3;
                    Text.NextCh();
                } while (Character.isDigit((char) Text.Ch) || Text.Ch == '_');
            } else Error.Message("Недопустимый символ");
        }
    }

    private static void Comment() {
        Text.NextCh();
        do {
            while (Text.Ch != '*' && Text.Ch != Text.chEOT)
                if (Text.Ch == '/') {
                    Text.NextCh();
                    if (Text.Ch == '*')
                        Comment();
                } else
                    Text.NextCh();
            if (Text.Ch == '*')
                Text.NextCh();
        } while (Text.Ch != '/' && Text.Ch != Text.chEOT);
        if (Text.Ch == '/')
            Text.NextCh();
        else {
            Location.LexPos = Location.Pos;
            Error.Message("Комментарий не закончен");
        }
    }

    private static void lineComment() {
        Text.NextCh();
        do
            Text.NextCh();
        while (Text.Ch != Text.chEOL);
    }

    static void NextLex() {
        while (Text.Ch == Text.chSpace || Text.Ch == Text.chEOL || Text.Ch == Text.chTab)
            Text.NextCh();
        Location.LexPos = Location.Pos;
        if (Character.isLetter((char) Text.Ch))
            Ident();
        else if (Character.isDigit((char) Text.Ch))
            Number();
        else
            switch (Text.Ch) {
                case '[':
                    Text.NextCh();
                    Lex = lexLBracket;
                    break;
                case ']':
                    Text.NextCh();
                    Lex = lexRBracket;
                    break;
                case '{':
                    Text.NextCh();
                    Lex = lexBegin;
                    break;
                case '}':
                    Text.NextCh();
                    Lex = lexEnd;
                    break;
                case ';':
                    Text.NextCh();
                    Lex = lexSemi;
                    break;
                case ':':
                    Text.NextCh();
                    if (Text.Ch == ':') {
                        Text.NextCh();
                        Lex = lexLinkToMethod;
                    } else
                        Lex = lexColon;
                    break;
                case '.':
                    Text.NextCh();
                    if (Text.Ch == '.') {
                        Text.NextCh();
                        if (Text.Ch == '.') {
                            Text.NextCh();
                            Lex = lexTripleDot;
                        } else Error.Expected("Недопустимый символ");
                    } else if (Character.isDigit(Text.Ch))
                        numberDouble(0);
                    else
                        Lex = lexDot;
                    break;
                case ',':
                    Text.NextCh();
                    Lex = lexComma;
                    break;
                case '=':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexEQ;
                    } else
                        Lex = lexAss;
                    break;
                case '!':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexNE;
                    } else
                        Lex = lexNot;
                    break;
                case '<':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexLE;
                    } else if (Text.Ch == '<') {
                        Text.NextCh();
                        if (Text.Ch == '=') {
                            Text.NextCh();
                            Lex = lexLShiftWithAss;
                        } else
                            Lex = lexLShift;
                    } else
                        Lex = lexLT;
                    break;
                case '>':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexGE;
                    } else if (Text.Ch == '>') {
                        Text.NextCh();
                        if (Text.Ch == '>') {
                            Text.NextCh();
                            if (Text.Ch == '=') {
                                Text.NextCh();
                                Lex = lexRShiftWithFillZerosWithAss;
                            } else
                                Lex = lexRShiftWithFillZeros;
                        } else if (Text.Ch == '=') {
                            Text.NextCh();
                            Lex = lexRShiftWithAss;
                        } else
                            Lex = lexRShift;
                    } else
                        Lex = lexGT;
                    break;
                case '(':
                    Text.NextCh();
                    Lex = lexLPar;
                    break;
                case ')':
                    Text.NextCh();
                    Lex = lexRPar;
                    break;
                case '+':
                    Text.NextCh();
                    if (Text.Ch == '+') {
                        Text.NextCh();
                        Lex = lexIncrement;
                    } else if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexPlusWithAss;
                    } else
                        Lex = lexPlus;
                    break;
                case '-':
                    Text.NextCh();
                    if (Text.Ch == '-') {
                        Text.NextCh();
                        Lex = lexDecrement;
                    } else if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexMinusWithAss;
                    } else if (Text.Ch == '>') {
                        Text.NextCh();
                        Lex = lexLambda;
                    } else
                        Lex = lexMinus;
                    break;
                case '*':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexMultWithAss;
                    } else
                        Lex = lexMult;
                    break;
                case '/':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexDivWithAss;
                    } else if (Text.Ch == '*') {
                        Comment();
                        NextLex();
                    } else if (Text.Ch == '/') {
                        lineComment();
                        NextLex();
                    } else
                        Lex = lexDiv;
                    break;
                case '%':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexModWithAss;
                    } else
                        Lex = lexMod;
                    break;
                case '&':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexAndWithAss;
                    } else if (Text.Ch == '&') {
                        Text.NextCh();
                        Lex = lexDoubleAnd;
                    } else
                        Lex = lexAnd;
                    break;
                case '|':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexOrWithAss;
                    } else if (Text.Ch == '|') {
                        Text.NextCh();
                        Lex = lexDoubleOr;
                    } else
                        Lex = lexOr;
                    break;
                case '^':
                    Text.NextCh();
                    if (Text.Ch == '=') {
                        Text.NextCh();
                        Lex = lexBitwiseExclusiveOrWithAss;
                    } else
                        Lex = lexBitwiseExclusiveOr;
                    break;
                case '~':
                    Text.NextCh();
                    Lex = lexBitwiseNot;
                    break;
                case '?':
                    Text.NextCh();
                    Lex = lexShortIf;
                    break;
                case '@':
                    Text.NextCh();
                    Lex = lexDog;
                    break;
                case '"':
                    Text.NextCh();
                    Lex = lexString;
                    break;
                case '\'':
                    Text.NextCh();
                    Lex = lexCharacter;
                    break;
                case '\\':
                    Text.NextCh();
                    if (Text.Ch == 'u')
                        numberOfUnicode();
                    else if (isaNumber())
                        numberOfLatin();
                    else if (Text.Ch == 'b') {
                        Text.NextCh();
                        Lex = lexBS;
                    } else if (Text.Ch == 't') {
                        Text.NextCh();
                        Lex = lexHT;
                    } else if (Text.Ch == 'n') {
                        Text.NextCh();
                        Lex = lexLF;
                    } else if (Text.Ch == 'f') {
                        Text.NextCh();
                        Lex = lexFF;
                    } else if (Text.Ch == 'r') {
                        Text.NextCh();
                        Lex = lexCR;
                    } else if (Text.Ch == '\"') {
                        Text.NextCh();
                        Lex = lexDoubleQuote;
                    } else if (Text.Ch == '\'') {
                        Text.NextCh();
                        Lex = lexQuote;
                    } else if (Text.Ch == '\\') {
                        Text.NextCh();
                        Lex = lexBackSlash;
                    } else
                        Error.Message("Недопустимый символ");
                    break;
                case Text.chEOT:
                    Lex = lexEOT;
                    break;
                default:
                    Error.Message("Недопустимый символ");
            }
    }

    private static boolean isaNumber() {
        return Text.Ch <= '9' && Text.Ch >= '0';
    }

    private static void numberOfLatin() {
        Text.NextCh();
        for (int i = 0; i < 2; i++) {
            if (isaNumber())
                Text.NextCh();
            else
                return;
        }

        Lex = lexUnicode;
    }

    private static void numberOfUnicode() {
        Text.NextCh();
        for (int i = 0; i < 4; i++) {
            if (isaNumber())
                Text.NextCh();
            else
                Error.Message("Недопустимый символ");
        }

        Lex = lexUnicode;
    }


    static void init() {
        EnterKW("abstract", lexAbstract);
        EnterKW("assert", lexAssert);
        EnterKW("boolean", lexBoolean);
        EnterKW("break", lexBreak);
        EnterKW("byte", lexByte);
        EnterKW("case", lexCase);
        EnterKW("catch", lexCatch);
        EnterKW("char", lexChar);
        EnterKW("class", lexClass);
        EnterKW("const", lexConst);
        EnterKW("continue", lexContinue);
        EnterKW("default", lexDefault);
        EnterKW("do", lexDo);
        EnterKW("double", lexDouble);
        EnterKW("else", lexElse);
        EnterKW("enum", lexEnum);
        EnterKW("extends", lexExtends);
        EnterKW("final", lexFinal);
        EnterKW("finally", lexFinally);
        EnterKW("float", lexFloat);
        EnterKW("for", lexFor);
        EnterKW("goto", lexGoTo);
        EnterKW("if", lexIf);
        EnterKW("implements", lexImplements);
        EnterKW("import", lexImport);
        EnterKW("instanceof", lexInstanceOf);
        EnterKW("int", lexInt);
        EnterKW("interface", lexInterface);
        EnterKW("long", lexLong);
        EnterKW("native", lexNative);
        EnterKW("new", lexNew);
        EnterKW("package", lexPackage);
        EnterKW("private", lexPrivate);
        EnterKW("protected", lexProtected);
        EnterKW("public", lexPublic);
        EnterKW("return", lexReturn);
        EnterKW("short", lexShort);
        EnterKW("static", lexStatic);
        EnterKW("strictfp", lexStrictFP);
        EnterKW("super", lexSuper);
        EnterKW("switch", lexSwitch);
        EnterKW("synchronized", lexSynchronized);
        EnterKW("this", lexThis);
        EnterKW("throw", lexThrow);
        EnterKW("throws", lexThrows);
        EnterKW("transient", lexTransient);
        EnterKW("try", lexTry);
        EnterKW("void", lexVoid);
        EnterKW("volatile", lexVolatile);
        EnterKW("while", lexWhile);
        EnterKW("null", lexNull);
        EnterKW("true", lexTrue);
        EnterKW("false", lexFalse);

        NextLex();
    }
}