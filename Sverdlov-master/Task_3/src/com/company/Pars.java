package com.company;

 class Pars {
    static void Compile() {
        System.out.println(Scan.Lex);
        while (Scan.Lex != 33) {
            Scan.NextLex();
            System.out.println(Scan.Lex);
        }
    }
}
