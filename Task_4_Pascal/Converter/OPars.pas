unit OPars;
{ Распознаватель }

interface

procedure Compile;

{======================================================}

implementation

uses
   OScan, OText;

procedure Compile;
var
   i, j: integer;

begin
   //   writeln(str);  
   while Lex <> lexEOT do 
   begin
      NextLex;
      
      case Lex of
         lexBEGIN: writeln('{');
         lexEND: writeln('}');
         lexAss: write(' = ');
         lexNE: write(' != ');
         lexWHILE: write('while (');
         lexDO: writeln(') {');
         lexINTEGER: write('int');
         lexPlus: write(' + ');
         lexMinus: write(' - ');
         lexMult: write(' * ');
         lexMOD: write(' / ');
         lexDIV: write(' % ');
         lexLPar: write('(');
         lexRPar: write(')');
         lexIF: write(' if (');
         lexELSE: write(' else ');
         lexGE: write(' >= ');
         lexGT: write(' > ');
         lexLE: write(' <= ');
         lexLT: write(' < ');
         lexComma: write(', ');
         lexEQ: write(' == ');
         lexSemi : writeln(';'); 
         lexTHEN : writeln(')');
      end; 
   end;
   
   //   for i := 0 to index do 
   //      writeln(str[i]);
   
   
   //   writeln;
   //   writeln;
   //   writeln('package com.company;');
   //   writeln('public class Convertor {');
   //   writeln('public static void main(String[] args) {');
   //   for i := 0 to index do 
   //   begin
   //      for j := 1 to length(str[i]) do 
   //      begin
   //         if (str[i][j] = ':' and str[i][j+1] = '')
   //            //write(str[i][j]);
   //      end;
   //      writeln;
   //   end;
end;

end.