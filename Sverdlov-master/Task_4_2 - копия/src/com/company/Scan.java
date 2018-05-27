package com.company;


import java.math.BigDecimal;
import java.util.Objects;

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
            lexDoubleQuote = 115, lexQuote = 116, lexLatin = 117;


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
        //TODO Не повторяющиеся имена?
        return lexName;
    }

    private static void Ident() {
        int i = 0;
        Buf.setLength(0);

        do {
            if (i++ < NAME_LENGTH)
                Buf.append((char) Text.ch);
            else
                Error.Message("Слишком длинное имя");
            Text.NextCh();
        } while (Character.isLetterOrDigit((char) Text.ch) || Text.ch == '_' || Text.ch == '$');

        Name = Buf.toString();
        Lex = TestKW();
    }

    private static void Number() {
        Lex = lexNum;
        if (Text.ch == '0') {
            Text.NextCh();

            if (Text.ch == 'x' || Text.ch == 'X')
                hexNumbers();
            else if (isOctNumber() || Text.ch == '_')
                octNumbers();
            else if (Text.ch == 'b' || Text.ch == 'B')
                binNumbers();
            else if (isLong())
                Text.NextCh();
            else if (Text.ch == '.') {
                numberDouble(new BigDecimal(0));
                if (isFloat()) {
                    Text.NextCh();
                }
            } else if (isFloat() || isLong() || isDouble()) {
                Text.NextCh();
            } else if (isE()) {
                E(new BigDecimal(0), new BigDecimal(0), 309, 0);
                if (isFloat() || isDouble())
                    Text.NextCh();
            }
        } else decNumbers();

    }

    private static void decNumbers() {
        BigDecimal bigDec = BigDecimal.valueOf(0);
        BigDecimal bigMax = new BigDecimal(Double.MAX_VALUE);

        do {

            int d = Text.ch - '0';

            bigDec = bigDec.movePointRight(1);
            bigDec = bigDec.add(BigDecimal.valueOf(d));

            if (bigDec.compareTo(bigMax) > 0) {
                Location.LexPos = Location.Pos;
                Error.Message("Слишком большое число");
            }

            Text.NextCh();
            while (Text.ch == '_') {
                Text.NextCh();
                if (isLong() || Text.ch == '.') {
                    Location.LexPos = Location.Pos;
                    Error.Message("Недопустимый символ");
                }
            }

            if (!Character.isDigit(Text.ch)) {
                if (Text.ch == '.') {
                    numberDouble(bigDec);
                } else if (isE()) {
                    E(bigDec, bigMax, 309, bigDec.toString().length());
                    if (isFloat() || isDouble())
                        Text.NextCh();
                }
            } else if (!isLong()) {
                downToInteger(bigDec);
            } else if (isLong()) {
                downToLong(bigDec);
            }
        } while (Character.isDigit((char) Text.ch));

        if (isFloat() || isLong()) {
            Text.NextCh();
        }

    }

    private static void numberDouble(BigDecimal nom) {
        BigDecimal bigMax = new BigDecimal(Double.MAX_VALUE);
        int indexMax = bigMax.toString().length();
        int indexE = nom.toString().length();
        int endOfFloat = 0;

        Text.NextCh();
        if (Character.isDigit((char) Text.ch) || isFloat() || isDouble() || isE()) {

            do {
                if (Character.isDigit((char) Text.ch))
                    endOfFloat += Text.ch - '0';


                if (nom.equals(bigMax) && 0 != Text.ch - '0' && !isDouble() && !isLong()) {
                    Location.LexPos = Location.Pos;
                    Error.Message("Слишком большое число");
                }

                if (!isFloat() && !isDouble() && !isE()) {
                    Text.NextCh();

                }

                while (Text.ch == '_') {
                    Text.NextCh();
                    if (isFloat() || isDouble()) {
                        Location.LexPos = Location.Pos;
                        Error.Message("Недопустимый символ");
                    }
                }


                if (isE()) {
                    E(nom, bigMax, indexMax, indexE);
                }


                if (isFloat()) {
                    downToFloat(endOfFloat, nom);
                } else if (isDouble())
                    Text.NextCh();


            } while (Character.isDigit((char) Text.ch));

        }
        //System.out.println(indexE);


    }

    private static void E(BigDecimal nom, BigDecimal bigMax, int indexMax, int indexE) {
        char symbol = '+';

        if (nom.equals(new BigDecimal(0)))
            indexE = 0;

        Text.NextCh();

        if (Text.ch == '-' || Text.ch == '+') {
            if (Text.ch == '-')
                symbol = '-';
            Text.NextCh();
        }


        if (!Character.isDigit((char) Text.ch)) {
            Location.LexPos = Location.Pos + 1;
            Error.Message("Недопустимый символ");
        }

        int index = 0;

        while (Character.isDigit((char) Text.ch)) {
            int d = Text.ch - '0';
            if (symbol == '+')
                if ((indexMax - indexE - d) / 10 >= index)
                    index = 10 * index + d;
                else {
                    Location.LexPos = Location.Pos;
                    Error.Message("Слишком большое число");
                }

            else {
                indexMax = 324;
                if ((indexMax - indexE - d) / 10 >= index) {
                    index = 10 * index + d;
                    //System.out.println((char) Text.ch);
                } else {
                    Location.LexPos = Location.Pos;
                    Error.Message("Слишком большое число");
                }

                if (nom.compareTo(bigMax) > 0) {
                    Location.LexPos = Location.Pos;
                    Error.Message("Слишком большое число");
                }
            }

            Text.NextCh();
        }
    }

    private static boolean isE() {
        return Text.ch == 'e' || Text.ch == 'E';
    }

    private static boolean isFloat() {
        return Text.ch == 'F' || Text.ch == 'f';
    }

    private static boolean isDouble() {
        return Text.ch == 'D' || Text.ch == 'd';
    }

    private static void downToFloat(int endOfFloat, BigDecimal sum) {
        BigDecimal max = new BigDecimal(Float.MAX_VALUE);

        if (sum.compareTo(max) > 0) {
            Location.LexPos = Location.Pos - 1;
            Error.Message("Слишком большое число");
        }

        if (sum.compareTo(max) == 0 && endOfFloat != 0) {
            Location.LexPos = Location.Pos - 1;
            Error.Message("Слишком большое число");
        }
    }

    private static void downToLong(BigDecimal sum) {
        BigDecimal max = new BigDecimal(Long.MAX_VALUE);

        if (sum.compareTo(max) > 0) {
            Location.LexPos = Location.Pos - 1;
            Error.Message("Слишком большое число");
        }
    }

    private static void downToInteger(BigDecimal sum) {
        BigDecimal max = new BigDecimal(Integer.MAX_VALUE);

        if (sum.compareTo(max) > 0) {
            Location.LexPos = Location.Pos - 1;
            Error.Message("Слишком большое число");
        }
    }

    private static void binNumbers() {
        Text.NextCh();

        if (!isBinNumber()) {
            Location.LexPos = Location.Pos;
            Error.Expected("Недопустимый символ");
        }

        StringBuilder str = new StringBuilder();
        int length = 0;
        long max = Long.MAX_VALUE;

        do {


            str.append((char) Text.ch);
            length++;

            long sum = 0;
            int j = 0;
            for (int i = length - 1; i >= 0; i--) {
                long d = (long) (Integer.parseInt("" + str.charAt(j), 2) * Math.pow(2, i));
                if ((max - sum) >= d) {
                    sum += d;
                } else {
                    Location.LexPos = Location.Pos;
                    Error.Message("Слишком большое число");
                }
                j++;
            }

            Text.NextCh();

            while (Text.ch == '_') {
                Text.NextCh();
                if (isLong()) {
                    Location.LexPos = Location.Pos;
                    Error.Message("Недопустимый символ");
                }
            }

            if (!isLong() && !isBinNumber()) {
                max = upToLong(sum);
            }
        } while (isBinNumber());

        if (isLong())
            Text.NextCh();
    }

    private static void octNumbers() {
        while (Text.ch == '_')
            Text.NextCh();

        if (!isOctNumber()) {
            Location.LexPos = Location.Pos;
            Error.Expected("Недопустимый символ");
        }

        StringBuilder str = new StringBuilder();
        int length = 0;
        long max = Long.MAX_VALUE;
        do {
            str.append((char) Text.ch);
            length++;

            long sum = 0;
            int j = 0;
            for (int i = length - 1; i >= 0; i--) {
                long d = (long) (Integer.parseInt("" + str.charAt(j), 8) * Math.pow(8, i));
                if ((max - sum) >= d) {
                    sum += d;
                } else {
                    Location.LexPos = Location.Pos;
                    Error.Message("Слишком большое число");
                }
                j++;
            }
            Text.NextCh();

            while (Text.ch == '_') {
                Text.NextCh();
                if (isLong()) {
                    Location.LexPos = Location.Pos;
                    Error.Message("Недопустимый символ");
                }
            }

            if (!isLong() && !isOctNumber()) {
                max = upToLong(sum);
            }

        } while (isOctNumber());
        if (isLong())
            Text.NextCh();
    }

    private static void hexNumbers() {
        Text.NextCh();

        if (!isHexNumber()) {
            Location.LexPos = Location.Pos;
            Error.Expected("Недопустимый символ");
        }

        StringBuilder str = new StringBuilder();
        int length = 0;
        long max = Long.MAX_VALUE;

        do {


            str.append((char) Text.ch);
            length++;

            long sum = 0;
            int j = 0;
            for (int i = length - 1; i >= 0; i--) {
                long d = (long) (Integer.parseInt("" + str.charAt(j), 16) * Math.pow(16, i));
                if ((max - sum) >= d) {
                    sum += d;
                } else {
                    Location.LexPos = Location.Pos;
                    Error.Message("Слишком большое число");
                }
                j++;
            }

            Text.NextCh();

            while (Text.ch == '_') {
                Text.NextCh();
                if (isLong()) {
                    Location.LexPos = Location.Pos;
                    Error.Message("Недопустимый символ");
                }
            }

            if (!isLong() && !isHexNumber()) {
                max = upToLong(sum);
            }
        } while (isHexNumber());
        if (isLong())
            Text.NextCh();
    }

    private static long upToLong(long sum) {
        long max;
        max = Integer.MAX_VALUE;

        if (sum > max) {
            Location.LexPos = Location.Pos;
            Error.Message("Слишком большое число");
        }
        return max;
    }

    private static boolean isLong() {
        return Text.ch == 'l' || Text.ch == 'L';
    }

    private static void Comment() {
        Text.NextCh();
        do {
            while (Text.ch != '*' && Text.ch != Text.chEOT)
                Text.NextCh();

            if (Text.ch == '*')
                Text.NextCh();
        } while (Text.ch != '/' && Text.ch != Text.chEOT);

        if (Text.ch == '/')
            Text.NextCh();
        else {
            Location.LexPos = Location.Pos;
            Error.Message("Комментарий не закончен");
        }
    }

    private static void lineComment() {
        Text.NextCh();

        while (Text.ch != Text.chEOT && Text.ch != Text.chEOL)
            Text.NextCh();
    }

    private static void string() {//TODO String lex + Slash lex?
        while (Text.ch != '"' && Text.ch != Text.chEOL && Text.ch != Text.chEOT) {
            boolean ok = true;

            if (Text.ch == '\\') {
                Text.NextCh();
                ok = false;
                slashCombinations();
            }

            if (ok)
                Text.NextCh();

        }

        if (Text.ch == '"')
            Text.NextCh();
        else
            Error.Message("String не закончен");
    }

    private static void character() {
        if (Text.ch != '\'') {
            boolean ok = true;
            if (Text.ch == '\\') {
                Text.NextCh();
                ok = false;
                slashCombinations();
            }
            if (ok)
                Text.NextCh();

            if (Text.ch == '\'') {
                Text.NextCh();
            } else {
                Location.LexPos = Location.Pos;
                Error.Message("char не закончен");
            }
        } else {
            Location.LexPos = Location.Pos;
            Error.Message("Недопустимый символ");
        }
    }

    private static char numberOfUnicodeForLex() {
        while (Text.ch == 'u')
            Text.NextCh();

        String checkString = "";

        for (int i = 0; i <= 3; i++) {
            checkString += (char) Text.ch;
//            if (Objects.equals(checkString, "\\u000a") || Objects.equals(checkString, "\\u000d") || Objects.equals(checkString, "\\u000A") || Objects.equals(checkString, "\\u000D")) {
//                Location.LexPos = Location.Pos;
//                Error.Message("LineTerminator");
//            } else if (Objects.equals(checkString, "\\u005c") || Objects.equals(checkString, "\\u005C")) {
//                Text.NextCh();
//                slashCombinationsWithoutUnicode();
//            } else
            if (isHexNumber() && i != 3) {
                Text.NextCh();
            } else if (i != 3) {
                Location.LexPos = Location.Pos + 1;
                Error.Message("Недопустимый символ");
            }
        }

        return (char) Integer.parseInt(checkString, 16);
    }

    static void nextLex() {
        if (Text.ch == '\\') {
            Text.NextCh();
            if (Text.ch == 'u') {
                Text.ch = numberOfUnicodeForLex();
                nextSymbol();
            } else Lex = lexBackSlash;
        } else nextSymbol();
    }

//    private static boolean nextUnicode() {
//        if (Text.ch == '\\') {
//            Text.NextCh();
//            if (Text.ch == 'u') {
//                Text.ch = numberOfUnicodeForLex();
//                return true;
//            } else {
//                System.out.println(Location.LexPos + ":" + Location.Pos);
//                Location.Pos = Location.Pos - 1;
//                System.out.println(Location.Pos);
//                //Error.Message("Недопустимый символ");
//                return false;
//            }
//        }
//        return false;
//    }

    private static void nextSymbol() {
        while (Text.ch == Text.chSpace || Text.ch == Text.chEOL || Text.ch == Text.chTab)
            Text.NextCh();

        Location.LexPos = Location.Pos;

        if (Character.isLetter((char) Text.ch) || Text.ch == '_' || Text.ch == '$')
            Ident();

        else if (Character.isDigit((char) Text.ch))
            Number();

        else {
            switch (Text.ch) {
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
                    if (Text.ch == ':') {
                        Text.NextCh();
                        Lex = lexLinkToMethod;
                    } else
                        Lex = lexColon;
                    break;

                case '.':
                    Text.NextCh();
                    if (Text.ch == '.') {
                        Text.NextCh();
                        if (Text.ch == '.') {
                            Text.NextCh();
                            Lex = lexTripleDot;
                        } else Error.Expected("Недопустимый символ");
                    } else if (Character.isDigit(Text.ch)) {
                        numberDouble(new BigDecimal(0));
                        if (isFloat()) {
                            Text.NextCh();
                        }
                    } else if (isFloat() || isDouble()) {
                        Text.NextCh();
                    } else if (isE()) {
                        E(new BigDecimal(0), new BigDecimal(0), 309, 0);
                        if (isFloat() || isDouble())
                            Text.NextCh();
                    } else
                        Lex = lexDot;
                    break;

                case ',':
                    Text.NextCh();
                    Lex = lexComma;
                    break;

                case '=':
                    Text.NextCh();
                    if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexEQ;
                    } else
                        Lex = lexAss;
                    break;

                case '!':
                    Text.NextCh();
                    if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexNE;
                    } else
                        Lex = lexNot;
                    break;

                case '<':
                    Text.NextCh();
                    if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexLE;
                    } else if (Text.ch == '<') {
                        Text.NextCh();
                        if (Text.ch == '=') {
                            Text.NextCh();
                            Lex = lexLShiftWithAss;
                        } else
                            Lex = lexLShift;
                    } else
                        Lex = lexLT;
                    break;

                case '>':
                    Text.NextCh();
                    if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexGE;
                    } else if (Text.ch == '>') {
                        Text.NextCh();
                        if (Text.ch == '>') {
                            Text.NextCh();
                            if (Text.ch == '=') {
                                Text.NextCh();
                                Lex = lexRShiftWithFillZerosWithAss;
                            } else
                                Lex = lexRShiftWithFillZeros;
                        } else if (Text.ch == '=') {
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
                    if (Text.ch == '+') {
                        Text.NextCh();
                        Lex = lexIncrement;
                    } else if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexPlusWithAss;
                    } else
                        Lex = lexPlus;
                    break;

                case '-':
                    Text.NextCh();
                    if (Text.ch == '-') {
                        Text.NextCh();
                        Lex = lexDecrement;
                    } else if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexMinusWithAss;
                    } else if (Text.ch == '>') {
                        Text.NextCh();
                        Lex = lexLambda;
                    } else
                        Lex = lexMinus;
                    break;

                case '*':
                    Text.NextCh();
                    if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexMultWithAss;
                    } else
                        Lex = lexMult;
                    break;

                case '/':
                    Text.NextCh();
//                    if (Text.ch == '\\')
//                        if (!nextUnicode()) {
//                            break;
//                        }


                    if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexDivWithAss;
                    } else if (Text.ch == '*') {
                        Comment();
                        nextLex();
                    } else if (Text.ch == '/') {
                        lineComment();
                        nextLex();
                    } else
                        Lex = lexDiv;
                    break;

                case '%':
                    Text.NextCh();
                    if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexModWithAss;
                    } else
                        Lex = lexMod;
                    break;

                case '&':
                    Text.NextCh();
                    if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexAndWithAss;
                    } else if (Text.ch == '&') {
                        Text.NextCh();
                        Lex = lexDoubleAnd;
                    } else
                        Lex = lexAnd;
                    break;

                case '|':
                    Text.NextCh();
                    if (Text.ch == '=') {
                        Text.NextCh();
                        Lex = lexOrWithAss;
                    } else if (Text.ch == '|') {
                        Text.NextCh();
                        Lex = lexDoubleOr;
                    } else
                        Lex = lexOr;
                    break;

                case '^':
                    Text.NextCh();
                    if (Text.ch == '=') {
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
                    string();
                    Lex = lexString;
                    break;

                case '\'':
                    Text.NextCh();
                    character();
                    Lex = lexCharacter;
                    break;

                case Text.chEOT:
                    Lex = lexEOT;
                    break;

                case '\\':
                    nextLex();
                    break;

                default:
                    Error.Message("Недопустимый символ");
            }
        }
    }

    private static void slashCombinations() {
        if (Text.ch == 'u')
            numberOfUnicode();
        else if (isOctNumber())
            numberOfLatin();
        else if (Text.ch == 'b') {
            Text.NextCh();
            Lex = lexBS;
        } else if (Text.ch == 't') {
            Text.NextCh();
            Lex = lexHT;
        } else if (Text.ch == 'n') {
            Text.NextCh();
            Lex = lexLF;
        } else if (Text.ch == 'f') {
            Text.NextCh();
            Lex = lexFF;
        } else if (Text.ch == 'r') {
            Text.NextCh();
            Lex = lexCR;
        } else if (Text.ch == '\"') {
            Text.NextCh();
            Lex = lexDoubleQuote;
        } else if (Text.ch == '\'') {
            Text.NextCh();
            Lex = lexQuote;
        } else if (Text.ch == '\\') {
            Text.NextCh();
            Lex = lexBackSlash;
        } else {
            Location.LexPos = Location.Pos;
            Error.Message("Недопустимый символ");
        }
    }

    private static void numberOfLatin() {
        String checkString = "" + (char) Text.ch;
        Text.NextCh();

        for (int i = 0; i < 2; i++) {
            if (isOctNumber()) {
                // System.out.println("     :" + (char) Text.ch);
                checkString += (char) Text.ch;
                if (Integer.parseInt(checkString) <= 377) {
                    Text.NextCh();
                } else {
                    Location.LexPos = Location.Pos - 2;
                    Error.Message("Слишком большое число");
                }
            } else
                return;
        }

        Lex = lexLatin;
    }

    private static boolean isBinNumber() {
        return Text.ch >= '0' && Text.ch <= '1';
    }

    private static boolean isOctNumber() {
        return Text.ch >= '0' && Text.ch <= '7';
    }

    private static boolean isHexNumber() {
        return Character.isDigit((char) Text.ch) || (Text.ch >= 'A' && Text.ch <= 'F') || (Text.ch >= 'a' && Text.ch <= 'f');
    }

    private static void numberOfUnicode() {
        while (Text.ch == 'u')
            Text.NextCh();

        String checkString = "\\u";

        for (int i = 0; i < 4; i++) {
            checkString += (char) Text.ch;
            if (Objects.equals(checkString, "\\u000a") || Objects.equals(checkString, "\\u000d") || Objects.equals(checkString, "\\u000A") || Objects.equals(checkString, "\\u000D")) {
                Location.LexPos = Location.Pos;
                Error.Message("LineTerminator");
            } else if (Objects.equals(checkString, "\\u005c") || Objects.equals(checkString, "\\u005C")) {
                Text.NextCh();
                slashCombinationsWithoutUnicode();
            } else if (isHexNumber())
                Text.NextCh();
            else {
                Location.LexPos = Location.Pos + 1;
                Error.Message("Недопустимый символ");
            }
        }
        Lex = lexUnicode;
    }

    private static void slashCombinationsWithoutUnicode() {
        if (isOctNumber())
            numberOfLatin();
        else if (Text.ch == 'b') {
            Text.NextCh();
            Lex = lexBS;
        } else if (Text.ch == 't') {
            Text.NextCh();
            Lex = lexHT;
        } else if (Text.ch == 'n') {
            Text.NextCh();
            Lex = lexLF;
        } else if (Text.ch == 'f') {
            Text.NextCh();
            Lex = lexFF;
        } else if (Text.ch == 'r') {
            Text.NextCh();
            Lex = lexCR;
        } else if (Text.ch == '\"') {
            Text.NextCh();
            Lex = lexDoubleQuote;
        } else if (Text.ch == '\'') {
            Text.NextCh();
            Lex = lexQuote;
        } else if (Text.ch == '\\') {
            Text.NextCh();
            Lex = lexBackSlash;
        } else {
            Location.LexPos = Location.Pos;
            Error.Message("Недопустимый символ");
        }
    }

    static void init() {
        nkw = 0;

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

        nextLex();
    }
}