unit OPars;
{ Распознаватель }



interface



procedure Convert;

{======================================================}

implementation

uses
OScan, OText;



procedure Convert;
var
   indexTab: integer;
   b: boolean := true;
   c: boolean := true;

begin
   assign(res, 'Res.txt');
   rewrite(res);
   writeln(res, 'package com.company;', #10);
   writeln(res, 'import java.util.Scanner;', #10);
   writeln(res, 'public class Main {');
   writeln(res, 'public static class Int{', #10, '   int n;', #10, '}');
   
   while Lex <> lexEOT do 
   begin
      var prevLex := Lex;
      
      NextLex;
      
      if (((Lex = lexVAR) or (Lex = lexBEGIN) or (Lex = lexCONST)) and (b = true)) then
         b := false;
      
      if ((Lex = lexBEGIN) and (c = true)) then begin
         c := false; 
         
         writeln(res, 'private static Scanner in = new Scanner(System.in);', #10);         
         write(res, 'public static void main(String[] args)');
      end;
     
      if (procNames) then begin
         if ((Lex = lexDot) or (Lex = lexLPar) or (Lex = lexSemi)) then
            write(res, procName)
         else
            write(res, '$', procName);
         procNames := false;     
      end;  
      
//      if (((Lex = lexLPar) or (Lex = lexSemi) or (Lex = lexDot)) and (procNames)) then begin
//         write(res, procName);
//         
//         end
//      else begin
//         if (procNames) then
//         write(res, '$', procName);
//      end;   
       
      
      if (b = false) then
         case Lex of
            lexWHILE: write(res, 'while (');
            lexDO:
               begin
                  writeln(res, ') {');
                  indexTab += 1;
                  for i: integer := 1 to indexTab do
                     write(res, '   ');
               end;
            lexLPar: 
               begin
                  
                  write(res, '(');
               end;
            lexRPar: write(res, ')');
            lexNE: write(res, ' != ');
            lexMult: write(res, ' * ');
            lexMinus: write(res, ' - ');    
            lexPlus: write(res, ' + ');
            lexGT: write(res, ' > ');
            lexGE: write(res, ' >= ');
            lexLT: write(res, ' < ');
            lexLE: write(res, ' <= ');
            lexEQ: 
               begin
                  if (c = false) then
                     write(res, ' == ')
                  else
                  begin
                     write(res, ' = ');
                  end;
               end;   
            lexComma: 
               if (blockVAR = false) then      
                  write(res, ', ');
               //else write(';', #10);
            
            lexAss: write(res, ' = ');
            
            lexBEGIN: 
               begin
                  writeln(res, '{');
                  indexTab += 1;
                  for i: integer := 1 to indexTab do
                     write(res, '   ');
               end;
            
            lexEND:
               begin
                  indexTab -= 1;
                  
                  if ((prevLex = lexRPar) or (prevLex = lexName) or (prevLex = lexNum)) then
                     write(res, ';'); 
                  if (prevLex <> lexSemi) then begin
                     writeln(res, '');
                     for i: integer := 1 to indexTab do
                        write(res, '   ');
                  end;
                  
                  write(res, '}');
                  writeln;
                  
                  for i: integer := 1 to indexTab do
                     write(res, '   ');
               end;   
            lexELSE: 
               begin
                  if ((prevLex = lexRPar) or (prevLex = lexName) or (prevLex = lexNum)) then
                     write(res, ';');   
                  if (prevLex <> lexSemi) then
                     writeln(res, '');
                  writeln(res, '}else {');
                  for i: integer := 1 to indexTab do
                     write(res, '   ');
               end;
            lexELSIF: 
               begin
                  if ((prevLex = lexRPar) or (prevLex = lexName) or (prevLex = lexNum)) then
                     write(res, ';');   
                  if (prevLex <> lexSemi) then
                     writeln(res, '');
                  indexTab -= 1;
                  for i: integer := 1 to indexTab do
                     write(res, '   ');
                  write(res, '}else if (');                
               end;   
            lexIF: write(res, 'if (');
            lexTHEN:
               begin
                  writeln(res, ') {');
                  indexTab += 1;
                  for i: integer := 1 to indexTab do
                     write(res, '   ');
               end;
            lexSemi: 
               begin
                  if (prevLex <> lexEND) then begin
                     
                     if (blockVAR = false) then
                        writeln(res, ';');
                     
                     
                     if (c = false) then                        
                        for i: integer := 1 to indexTab do
                           write(res, '   ');                     
                  end;   
               end;
            lexDIV: write(res, ' / ');
            lexMOD: write(res, ' % ');   
         end;
   end;
   writeln(res, 'static private void OutLn() {', #10, '   System.out.println();', #10, '}');
   writeln(res, 'static private int MAX(int x) {', #10, '   return Integer.MAX_VALUE;', #10, '}');
   writeln(res, 'static private int MIN(int x) {', #10, '   return Integer.MIN_VALUE;', #10, '}');
   writeln(res, 'static private void INC(Int n) {', #10, '   n.n += 1;', #10, '}');
   writeln(res, 'static private void INC(Int n, int v) {', #10, '   n.n += v;', #10, '}');
   writeln(res, 'static private boolean ODD(int x){', #10, '   return x % 2 == 1;', #10, '}');
   writeln(res, 'static private void DEC(Int n, int v) {', #10, '   n.n -=  v;', #10, '}');
   writeln(res, 'static private void DEC(Int n) {', #10, '   n.n -=  1;', #10, '}');
   writeln(res, 'static private int ABS(int x) {', #10, '   return Math.abs(x);', #10, '}');
   writeln(res, 'static private void HALT(int n){', #10, '   System.exit(n);', #10, '}');
   writeln(res, 'static private void OutInt(int v, int n) {', #10, '   for (int i = 1;i < n;i++) {', #10, '      System.out.print(" ");', #10, '   }', #10, '   System.out.print(v);', #10, '}');
   writeln(res, 'static private void InOpen(){', #10, '}');
   writeln(res, 'static private void InInt(Int v){', #10, '   v.n = in.nextInt();', #10, '}', #10, '}');
   close(res);
   
end;

end.