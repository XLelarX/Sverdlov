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
            else if (ch == '\n') {
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
                do
                    System.out.println(' ');
                while (++Location.Pos % TAB_SIZE != 0);
        } catch (IOException ignored) {
        }
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