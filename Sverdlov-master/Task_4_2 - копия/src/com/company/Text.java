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
                nextUnicode();
//                System.out.write(ch);
            } else if (ch == '\n') {
//                System.out.println();
                Location.Line++;
                Location.Pos = 0;
                ch = chEOL;
            } else if (ch == '\r')
                NextCh();
            else if (ch != '\t') {
//                System.out.write(ch);
                Location.Pos++;
            } else
                do {
                    System.out.println(' ');
                }while (++Location.Pos % TAB_SIZE != 0);
        } catch (IOException ignored) {
        }
    }

    private static boolean nextUnicode() {
        if (ch == '\\') {
            Text.NextCh();
            if (ch == 'u') {
                ch = numberOfUnicodeForLex();
                return true;
            } else {
//                System.out.println(Location.LexPos + ":" + Location.Pos);
                Location.Pos--;
//                System.out.println(Location.Pos);
                //Error.Message("Недопустимый символ");
                return false;
            }
        }
        return false;
    }

    private static char numberOfUnicodeForLex() {
        while (Text.ch == 'u')
            Text.NextCh();

        String checkString = "";

        for (int i = 0; i <= 3; i++) {
            checkString += (char) Text.ch;
            if (isHexNumber() && i != 3) {
                Text.NextCh();
            } else if (i != 3) {
                Location.LexPos = Location.Pos + 1;
                Error.Message("Недопустимый символ");
            }
        }

        return (char) Integer.parseInt(checkString, 16);
    }

    private static boolean isHexNumber() {
        return Character.isDigit((char) Text.ch) || (ch >= 'A' && ch <= 'F') || (ch >= 'a' && ch <= 'f');
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