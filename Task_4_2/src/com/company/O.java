package com.company;

public class O {
    private static void Init() {
        Text.Reset();
        if (!Text.Ok)
            Error.Message(Text.Message);
        Scan.init();
    }

    private static void Done() {
        Text.Close();
    }

    public static void main(String[] args) {
        System.out.println(0e1);
        System.out.println("\nКомпилятор языка");
        if (args.length == 0)
            Location.Path = null;
        else
            Location.Path = args[0];
        O.Init();
        Pars.Compile();
        O.Done();
    }
}
