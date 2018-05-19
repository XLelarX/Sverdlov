package com.company;

import java.io.File;

public class O {
    private static void Init() {
        Text.Reset();
        if (!Text.ok)
            Error.Message(Text.message);
        Scan.init();
    }

    private static void Done() {
        Text.Close();
    }

    public static void main(String[] args) {
        System.out.println('\u002c');
        System.out.println(Integer.MAX_VALUE + "     " + Long.MAX_VALUE);
        System.out.println("Компилятор языка Java");
        showoutFiles(convertHumanReadableFormatToRegex(args[0]));

    }

    private static String convertHumanReadableFormatToRegex(String arg) {
        return arg.replaceAll("\\.", "\\*")
                .replaceAll("^\\*", "\\.");
    }

    private static void showoutFiles(String fileMask) {
        File dir = new File(".");
        File[] files = dir.listFiles((dir1, name) -> name.matches(fileMask));
        assert files != null;
        for (File file : files) {
            System.out.println(file.getName());
            Location.path = file.getName();
            O.Init();
            Pars.Compile();
            O.Done();
        }
    }
}
