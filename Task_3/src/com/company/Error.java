package com.company;

import java.io.IOException;

class Error {

    static void Message(String Msg) {
        int ELine = Location.Line;
        while (Text.Ch != Text.chEOL && Text.Ch != Text.chEOT)
            Text.NextCh();
        if (Text.Ch == Text.chEOT)
            System.out.println();
        for (int i = 1; i < Location.LexPos; i++)
            System.out.println(' ');
        System.out.println("^");
        System.out.println("Строка : " + ELine + " Ошибка : " + Msg);
        System.out.println();
        System.out.println("Нажмите ВВОД");
        try {
            while (System.in.read() != '\n') ;
        } catch (IOException e) {
        }
        System.exit(0);
    }

    public static void Expected(String Msg) {
        Message("Ожидается " + Msg);
    }

    public static void Warning(String Msg) {
        System.out.println();
        System.out.println("Предупреждение : " + Msg);
    }
}
