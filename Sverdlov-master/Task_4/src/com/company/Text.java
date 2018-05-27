package com.company;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Text {
    private static final String FILE_NOT_OPEN = "Файл не открыт";
    private static final String FILE_IS_NOT_FOUND = "Входной файл не найден";
    public static final int tabSize = 3;
    public static final char charSpace = ' ';
    public static final char charTab = '\t';
    public static final char charEndOfLine = '\n';

    private static File file = new File("C://For_Sverdlov//t.txt");
    private static Scanner fileReader;

//    public static void nextChar() {
//        try {
//            if ((ch = file.read()) == -1)
//                ch = charEndOfFile;
//            else if (ch == '\n') {
//                System.out.println();
//                Location.line++;
//                Location.pos = 0;
//                ch = charEndOfLine;
//            } else if (ch == '\r')
//                nextChar();
//            else if (ch == '\t') {
//                System.out.write(ch);
//                Location.pos++;
//            } else
//                do {
//                    System.out.println(' ');
//                } while (++Location.pos % tabSize != 0);
//        } catch (IOException e) {
//        }
//    }


    static void reset() {
        try {
            fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                char arrayFromLine[] = line.toCharArray();
                ArrayList<Character> array = new ArrayList<>();

                for (char element : arrayFromLine) {
                   array.add(element);

                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void close() {
        fileReader.close();
    }
}
