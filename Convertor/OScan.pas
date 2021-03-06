﻿unit OScan;
{ Конвертор }



interface

const
   
   NameLen = 31;{Наибольшая длина имени}

type
   
   tName = string[NameLen];
   tLex = (lexNone, lexName, lexNum,
   lexMODULE, lexIMPORT, lexBEGIN, lexEND,
   lexCONST, lexVAR, lexWHILE, lexDO,
   lexIF, lexTHEN, lexELSIF, lexELSE,
   lexMult, lexDIV, lexMOD, lexPlus, lexMinus,
   lexEQ, lexNE, lexLT, lexLE, lexGT, lexGE,
   lexDot, lexComma, lexColon, lexSemi, lexAss,
   lexLpar, lexRpar,
   lexEOT, lexINTEGER);

var
   Lex: tLex;    {Текущая лексема            }
   Name: tName;   {Строковое значение имени   }
   Num: integer; {Значение числовых литералов}
   LexPos: integer;{Позиция начала лексемы     }
   checkVAR2: boolean := false;
   
   res: text;
   
   constTable: array [0..100] of string;
   size: integer := 0;
   
   blockCONST: boolean := false;
   blockVAR: boolean := false;
   blockPROGRAM: boolean := false;
   proc: boolean := false;
   checkForConst: boolean;
   procNames: boolean := false;
   procName: string;

procedure InitScan;
procedure NextLex;

{=======================================================}
implementation

uses
   OText, OError, OPars;

const
   KWNum = 35;

type
   tKeyWord = string[9];{Длина слова PROCEDURE}

var
   LexTable: array [1..KWNum] of
   record
      Lex: tLex;
      Count: integer = 0;
   end;   
   nkw: integer;
   KWTable: array [1..KWNum] of
   record
      Word: tKeyWord;
      Lex: tLex;
      Count: integer = 0;
   end;
   
   prevLex: tLex;
   checkCONST2: boolean := false;



procedure InitLT;
var
   i: integer;
   lex: tLex;

begin
   LexTable[1].Lex := lex;
   lex := tLex.lexNone;
   for i := 2 to KWNum do 
   
   begin
      LexTable[i].Lex := succ(lex);
      lex := LexTable[i].Lex;
   end;
end;



procedure EnterKW(Name: tKeyWord; Lex: tLex);
var
   i: integer;

begin
   nkw := nkw + 1;
   KWTable[nkw].Word := Name;
   KWTable[nkw].Lex := Lex;
end;



function TestKW: tLex;
var
   i, l, r: integer;
   b: boolean;
   c: boolean;
   ind: integer;

begin
   b := true;
   l := 1;
   r := nkw;
   while (l <= r) do
   
   begin
      i := l + (r - l) div 2;
      if KWTable[i].Word = Name then begin
         
         if (Name = 'VAR') then begin
            blockVAR := true;
         end;
         if (Name = 'CONST') then begin
            blockCONST := true;
         end;
         
         
         if ((Name = 'BEGIN') or (Name = 'CONST')) then begin
            blockVAR := false;
         end;
         if ((Name = 'BEGIN') or (Name = 'VAR')) then begin
            blockCONST := false;
         end;
         
         if (Name = 'BEGIN') then
            blockPROGRAM := true;  
         
         prevLex := Lex;
         TestKW := KWTable[i].Lex;
         b := false;
      end;
      if KWTable[i].Word < Name then 
         l := i + 1
      else 
         r := i - 1;
   end; 
   if b then begin
      prevLex := Lex;
      TestKW := lexName;
      //procNames := false;
//      procName := '';
               
      if (blockVAR) then
         writeln(res, 'private static Int $', Name, ' = new Int();');
      
      if (blockCONST) then begin
         write(res, 'private static final int $', Name);
         constTable[size] := Name;
         size += 1;
      end;
      
      if (blockPROGRAM and (prevLex <> lexEND)) then begin
         
         checkForConst := false;
         for ind := 0 to size - 1 do 
         begin
            if (Name = constTable[ind]) then
               checkForConst := true; 
         end;
         
         if ((Name = 'Out') or (Name = 'Int') or (Name = 'In') or (Name = 'Ln') or (Name = 'Open') or (Name = 'ABS') or (Name = 'DEC') or (Name = 'INC') or (Name = 'HALT') or (Name = 'ODD') or (Name = 'MIN') or (Name = 'MAX')) then begin
            begin
               procNames := true;
               procName := Name;
               //write(res,Name);
            end;   
            if ((Name = 'INC') or (Name = 'DEC') or (Name = 'In')) then
               proc := true;
         end
         else
         if (checkForConst) then
            write(res, '$', Name)  
         else begin
            if (proc = false) then
               write(res, '$', Name, '.n')
            else
               write(res, '$', Name);
         end;
         if ((Name = 'Ln') or (Name = 'Open')) then write(res, '()');  
      end;  
      
      
      
      
      
      
      
      
      //      if (checkVAR or checkCONST2) then begin
      //         if(checkVAR2) then begin
      //            
      //            if ((Name <> 'Int') and (Name <> 'Out') and (Name <> 'In') and (Name <> 'DEC') and (Name <> 'Open') and (Name <> 'INC') and (Name <> 'MIN') and (Name <> 'MAX') and (Name <> 'ODD') and (Name <> 'ABS') and (Name <> 'Ln') and (Name <> 'HALT')) then 
      //               writeln('private static Int $', Name, ' = new Int();');
      //            
      //         end
      //         
      //         else if (checkCONST) then begin
      //            
      //            write('static final int $', Name);
      //            ind += 1;
      //            constTable[ind] := Name;
      //            
      //         end
      //         else begin
      //            if (prevLex <> lexEND) then begin
      //               
      //               
      //               //if (prevLex = lexVAR) then write ('static int  ');
      //               
      //               if ((Name = 'INC') or (Name = 'DEC') or (Name = 'In')) then proc := true;
      //               
      //               if ((proc = false)) then
      //                  if ((Name <> 'Int') and (Name <> 'Out') and (Name <> 'In') and (Name <> 'DEC') and (Name <> 'Open') and (Name <> 'INC') and (Name <> 'MIN') and (Name <> 'MAX') and (Name <> 'ODD') and (Name <> 'ABS') and (Name <> 'Ln') and (Name <> 'HALT')) then 
      //                  begin
      //                     write('$', Name, '.n');
      //                     
      //                  end
      //                  else
      //                     write(Name)
      //               else 
      //               
      //               if ((Name <> 'Int') and (Name <> 'Out') and (Name <> 'In') and (Name <> 'DEC') and (Name <> 'Open') and (Name <> 'INC') and (Name <> 'MIN') and (Name <> 'MAX') and (Name <> 'ODD') and (Name <> 'ABS') and (Name <> 'Ln') and (Name <> 'HALT')) then 
      //                  write('$', Name)
      //               else 
      //                  write(Name);
      //               
      //               if ((Name = 'Ln') or (Name = 'Open')) then write('()');
      //            end;
      //         end;
      //      end;  
   end; 
   
end;



procedure Ident;
var
   
   i: integer;
begin
   i := 0;
   Name := '';
   repeat
      if i < NameLen then begin
         i := i + 1;
         Name := Name + Ch;
      end
      else
         Error('Слишком длинное имя');
      NextCh;
   until not (Ch in ['A'..'Z', 'a'..'z', '0'..'9']);
   Lex := TestKW;     {Проверка на ключевое слово}
end;



procedure Number;
var
   
   d: integer;
begin
   Lex := lexNum;
   Inc(LexTable[3].Count);
   Num := 0;
   repeat
      d := ord(Ch) - ord('0');
      if (Maxint - d) div 10 >= Num then
         Num := 10 * Num + d
      else
         Error('Слишком большое число');
      NextCh;
   until not (Ch in ['0'..'9']);
   write(res, Num);
end;



procedure Comment;
begin
   NextCh;
   repeat
      while (Ch <> '*') and (Ch <> chEOT) do
         
         if Ch = '(' then begin
            NextCh;
            if Ch = '*' then
               Comment;
         end
         else
            NextCh;
      if Ch = '*' then
         NextCh
      else Error('Не закончен комментарий');
   until Ch in [')', chEOT];
   if Ch = ')' then
      NextCh
   else begin
      LexPos := Pos;
      Error('Комментарий не закончен');
   end;
end;



procedure NextLex;
begin
   while Ch in [chSpace, chTab, chEOL] do 
      NextCh;
   LexPos := Pos;
   case Ch of
      'A'..'Z', 'a'..'z': Ident;
      '0'..'9': Number;
      ';': 
         
         begin
            NextCh;
            Lex := lexSemi;
            proc := false;
         end;
      ':':
         
         begin
            NextCh;
            if Ch = '=' then begin
               NextCh;
               Lex := lexAss;
            end
            else begin
               Lex := lexColon;
            end;
         end;
      '.': 
         
         begin
            NextCh;
            Lex := lexDot;
         end;
      ',': 
         
         begin
            NextCh;
            proc := false;
            Lex := lexComma;
         end;
      '=': 
         
         begin
            NextCh;
            Lex := lexEQ;
         end;
      '#': 
         
         begin
            NextCh;
            Lex := lexNE;
         end;
      '<': 
         
         begin
            NextCh;
            if Ch = '=' then begin
               NextCh;
               Lex := lexLE;
               
            end
            else begin
               Lex := lexLT;
            end;   
         end;
      '>': 
         
         begin
            NextCh;
            if Ch = '=' then begin
               NextCh;
               Lex := lexGE;
            end
            else
               Lex := lexGT;
         end;
      '(': 
         
         begin
            NextCh;
            if Ch = '*' then begin
               Comment;
               NextLex;
            end
            else begin
               Lex := lexLpar;
            end;   
         end;
      ')': 
         
         begin
            NextCh;
            Lex := lexRpar;
         end;
      '+': 
         
         begin
            NextCh;
            Lex := lexPlus;
         end;
      '-': 
         
         begin
            NextCh;
            Lex := lexMinus;
         end;
      '*': 
         
         begin
            NextCh;
            Lex := lexMult;
         end;
      chEOT: 
         
         begin
            Lex := lexEOT;
         end;
   else
      Error('Недопустимый символ');
   end;
end;



procedure InitScan;
begin
   nkw := 0;
   
   InitLT;
   EnterKW('ARRAY', lexNone);
   EnterKW('BEGIN', lexBEGIN);
   EnterKW('BY', lexNone);
   EnterKW('CASE', lexNone);
   EnterKW('CONST', lexCONST);
   EnterKW('DIV', lexDIV);
   EnterKW('DO', lexDO);
   EnterKW('ELSE', lexELSE);
   EnterKW('ELSIF', lexELSIF);
   EnterKW('END', lexEND);
   EnterKW('EXIT', lexNone);
   EnterKW('FOR', lexNone);
   EnterKW('IF', lexIF);
   EnterKW('IMPORT', lexIMPORT);
   EnterKW('IN', lexNone);
   EnterKW('INTEGER', lexINTEGER);
   EnterKW('IS', lexNone);
   EnterKW('LOOP', lexNone);
   EnterKW('MOD', lexMOD);
   EnterKW('MODULE', lexMODULE);
   EnterKW('NIL', lexNone);
   EnterKW('OF', lexNone);
   EnterKW('OR', lexNone);
   EnterKW('POINTER', lexNone);
   EnterKW('PROCEDURE', lexNone);
   EnterKW('RECORD', lexNone);
   EnterKW('REPEAT', lexNone);
   EnterKW('RETURN', lexNone);
   EnterKW('THEN', lexTHEN);
   EnterKW('TO', lexNone);
   EnterKW('TYPE', lexNone);
   EnterKW('UNTIL', lexNone);
   EnterKW('VAR', lexVAR);
   EnterKW('WHILE', lexWHILE);
   EnterKW('WITH', lexNone);
   
   NextLex;
end;

end.
