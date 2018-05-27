package com.company;

import java.math.BigDecimal;

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
        System.out.println(0x7fffffff + ":" + 0x7fffffffffffffffL);
        System.out.println("Компилятор языка Java");
        if (args.length == 0)
            Location.path = null;
        else
            Location.path = args[0];
        O.Init();
        Pars.Compile();
        O.Done();
    }
}
