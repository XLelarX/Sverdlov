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

        System.out.println(new BigDecimal(Double.MAX_VALUE));
        System.out.println(new BigDecimal(Float.MAX_VALUE));
        System.out.println(1e308);
        System.out.println(Integer.MAX_VALUE + "     " + Long.MAX_VALUE);
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
