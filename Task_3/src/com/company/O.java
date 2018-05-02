package com.company;

public class O {
    static void Init() {
        Text.Reset();
        if (!Text.Ok)
            Error.Message(Text.Message);
        Scan.init();
    }
    static void Done() {
        Text.Close();
    }
}
