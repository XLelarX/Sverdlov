package com.company;

import java.io.IOException;

class Error {

    static void Message(String Msg) {
        int ELine = Location.Line;
        while (Text.ch != Text.chEOL && Text.ch != Text.chEOT)
            Text.NextCh();
        if (Text.ch == Text.chEOT)
            System.out.println();
        for (int i = 1; i < Location.LexPos; i++)
            System.out.print(' ');
        System.out.println("^");
        System.out.println("Строка : " + ELine + " Ошибка : " + Msg);
        System.out.println();
        System.out.println("Нажмите ВВОД");
        try {
            while (System.in.read() != '\n');
        } catch (IOException ignored) {
        }
        System.exit(0);
    }

    static void Expected(String message) {
        Message("Ожидается " + message);
    }

    public static void Warning(String Msg) {
        System.out.println();
        System.out.println("Предупреждение : " + Msg);
    }
}