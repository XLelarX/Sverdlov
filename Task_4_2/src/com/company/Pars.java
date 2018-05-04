package com.company;

class Pars {
    static void Compile() {
        int n = 0;
        while (Scan.Lex != 33) {
            Scan.NextLex();
            n++;
        }
        System.out.println();
        System.out.println(n);

    }
}
