package com.company;

public class Main {

    public static void main(String[] args) {
        System.out.println("\nКомпилятор языка");
        System.out.println(args.length);
        if (args.length == 0)
            Location.Path = null;
        else
            Location.Path = args[0];
        O.Init();
        Pars.Compile();
        O.Done();
    }
}
