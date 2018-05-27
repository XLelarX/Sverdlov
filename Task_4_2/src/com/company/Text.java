package com.company;

import java.io.*;

class Text {
    private static final int TAB_SIZE = 3;
    static final char chSpace = ' ';
    static final char chTab = '\t';
    static final char chEOL = '\n';
    static final char chEOT = '\0';

    static boolean ok = false;
    static String message = "Файл не открыт";
    static int ch = chEOT;

    private static InputStream f;

    static void NextCh() {
        try {
            if ((ch = f.read()) == -1)
                ch = chEOT;
            else if (ch == '\\') {
                ch = nextUnicode();
                System.out.print((char) ch);
                Location.Pos++;
            } else if (ch == '\n') {
                System.out.println();
                Location.Line++;
                Location.Pos = 0;
                ch = chEOL;
            } else if (ch == '\r')
                NextCh();
            else if (ch != '\t') {
                System.out.write(ch);
                Location.Pos++;
            } else
                do {
                    System.out.println(' ');
                } while (++Location.Pos % TAB_SIZE != 0);
        } catch (IOException ignored) {
        }
    }

    static void NextChForChar() {
        try {
            if ((ch = f.read()) == -1)
                ch = chEOT;
            else if (ch == '\r')
                NextChForChar();
            else if (ch != '\t') {
                System.out.write(ch);
                Location.Pos++;
            } else
                do {
                    System.out.println(' ');
                } while (++Location.Pos % TAB_SIZE != 0);
        } catch (IOException ignored) {
        }
    }

    private static char nextUnicode() throws IOException {

        char chbuf = (char) f.read();

        if (chbuf == 'u') {
            chbuf = numberOfUnicodeForLex();
        } else {
            Location.LexPos = Location.Pos + 1;
            Error.Message("Нет такого символа : \\ ");
        }

        return chbuf;
    }

    private static char numberOfUnicodeForLex() throws IOException {
        char chbuf = (char) f.read();

        while (ch == 'u')
            chbuf = (char) f.read();

        String checkString = "";

        for (int i = 0; i <= 3; i++) {
            checkString += (char) chbuf;
            if (isHexNumber(chbuf) && i != 3) {
                chbuf = (char) f.read();
            } else if (!isHexNumber(chbuf)) {
                Location.LexPos = Location.Pos;
                Error.Message("Недопустимый символ : " + chbuf);
            }
        }
        return (char) Integer.parseInt(checkString, 16);
    }

    private static boolean isHexNumber(char charbuf) {
        return Character.isDigit(charbuf) || (charbuf >= 'A' && charbuf <= 'F') || (charbuf >= 'a' && charbuf <= 'f');
    }

    static void Reset() {
        if (Location.path == null) {
            System.out.println("Формат вызова : ");
            System.exit(1);
        } else
            try {
                f = new FileInputStream(Location.path);
                ok = true;
                message = "ok";
                Location.Pos = 0;
                Location.Line = 0;
                NextCh();
            } catch (IOException e) {
                ok = false;
                message = "Входной файл не найден";
            }
    }

    static void Close() {
        try {
            f.close();
        } catch (IOException ignored) {
        }
    }
}