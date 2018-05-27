package com.company;

import java.util.Scanner;

public class Main {
    public static class Int{
        int n;
    }
    private static final int $n1 = 2;
    private static final int $n2 = 1000;
    private static final int $w = 10;
    private static Int $n = new Int();
    private static Int $d = new Int();
    private static Scanner in = new Scanner(System.in);

    public static void main(String[] args){
        $n.n = $n1;
        while ($n.n <= $n2) {
            $d.n = 2;
            while ($n.n % $d.n != 0) {
                $d.n = $d.n + 1;
            }      if ($d.n == $n.n) {
                OutInt($n.n, $w);
            }      $n.n = $n.n + 1;
        }   }static private void OutLn() {
        System.out.println();
    }
    static private int MAX(int x) {
        return Integer.MAX_VALUE;
    }
    static private int MIN(int x) {
        return Integer.MIN_VALUE;
    }
    static private void INC(Int n) {
        n.n += 1;
    }
    static private void INC(Int n, int v) {
        n.n += v;
    }
    static private boolean ODD(int x){
        return x % 2 == 1;
    }
    static private void DEC(Int n, int v) {
        n.n -=  v;
    }
    static private void DEC(Int n) {
        n.n -=  1;
    }
    static private int ABS(int x) {
        return Math.abs(x);
    }
    static private void HALT(int n){
        System.exit(n);
    }
    static private void OutInt(int v, int n) {
        for (int i = 1;i < n;i++) {
            System.out.print(" ");
        }
        System.out.print(v);
    }
    static private void InOpen(){
    }
    static private void InInt(Int v){
        v.n = in.nextInt();
    }
}