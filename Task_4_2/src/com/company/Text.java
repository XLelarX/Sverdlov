package com.company;

import java.io.*;

class Text {
    private static final int TAB_SIZE = 3;
    static final char chSpace = ' ';
    static final char chTab = '\t';
    static final char chEOL = '\n';
    static final char chEOT = '\0';

    static boolean Ok = false;
    static String Message = "Файл не открыт";
    static int Ch = chEOT;

    private static InputStream f;

    static void NextCh() {
        try {
            if ((Ch = f.read()) == -1)
                Ch = chEOT;
            else if (Ch == '\n') {
                System.out.println();
                Location.Line++;
                Location.Pos = 0;
                Ch = chEOL;
            } else if (Ch == '\r')
                NextCh();
            else if (Ch != '\t') {
                System.out.write(Ch);
                Location.Pos++;
            } else
                do
                    System.out.println(' ');
                while (++Location.Pos % TAB_SIZE != 0);
        } catch (IOException ignored) {
        }
    }

    static void Reset() {
        if (Location.Path == null) {
            System.out.println("Формат вызова : ");
            System.exit(1);
        } else
            try {
                f = new FileInputStream(Location.Path);
                Ok = true;
                Message = "Ok";
                Location.Pos = 0;
                Location.Line = 0;
                NextCh();
            } catch (IOException e) {
                Ok = false;
                Message = "Входной файл не найден";
            }
    }

    static void Close() {
        try {
            f.close();
        } catch (IOException ignored) {
        }
    }
}