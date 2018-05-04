{Krylov Michael(MKN-21)}
unit OScan;
{ Сканер }

interface

const
   NameLen = 31;{Наибольшая длина имени}

type
   tName = string[NameLen];
   tLex =  (lexNone, lexName, lexNum,
   lexMODULE, lexIMPORT, lexBEGIN, lexEND,
   lexCONST, lexVAR, lexWHILE, lexDO,
   lexIF, lexTHEN, lexELSIF, lexELSE,
   lexMult, lexDIV, lexMOD, lexPlus, lexMinus,
   lexEQ, lexNE, lexLT, lexLE, lexGT, lexGE,
   lexDot, lexComma, lexColon, lexSemi, lexAss,
   lexLpar, lexRpar,
   lexEOT);

var
   Lex: tLex;    {Текущая лексема            }
   Name: tName;   {Строковое значение имени   }
   Num: integer; {Значение числовых литералов}
   LexPos: integer;{Позиция начала лексемы     }

procedure InitScan;
procedure NextLex;
//procedure CountOfLex;

{=======================================================}
implementation

uses
   OText, OError;

const
   KWNum = 34;

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
   b : boolean;
begin
   b := true;
   l := 1;
   r := nkw;
   while (l <= r) do
   begin
      i := l + (r - l) div 2;
      if KWTable[i].Word = Name then begin
         TestKW := KWTable[i].Lex;
         Inc(KWTable[i].Count);
         b := false;
      end;
      if KWTable[i].Word < Name then 
         l := i + 1
      else 
         r := i - 1;
   end; 
   if b then begin
         TestKW := lexName;
         Inc(LexTable[2].Count);
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
            Inc(LexTable[30].Count);
         end;
      ':':
         begin
            NextCh;
            if Ch = '=' then begin
               NextCh;
               Lex := lexAss;
               Inc(LexTable[31].Count);
            end
            else begin
               Lex := lexColon;
               Inc(LexTable[29].Count);
            end;
         end;
      '.': 
         begin
            NextCh;
            Lex := lexDot;
            Inc(LexTable[27].Count);
         end;
      ',': 
         begin
            NextCh;
            Lex := lexComma;
            Inc(LexTable[28].Count);
         end;
      '=': 
         begin
            NextCh;
            Lex := lexEQ;
            Inc(LexTable[21].Count);
         end;
      '#': 
         begin
            NextCh;
            Lex := lexNE;
            Inc(LexTable[22].Count);
         end;
      '<': 
         begin
            NextCh;
            if Ch = '=' then begin
               NextCh;
               Lex := lexLE;
               Inc(LexTable[24].Count);
            end
            else begin
               Lex := lexLT;
               Inc(LexTable[23].Count);
            end;   
         end;
      '>': 
         begin
            NextCh;
            if Ch = '=' then begin
               NextCh;
               Lex := lexGE;
               Inc(LexTable[26].Count);
            end
            else begin
               Lex := lexGT;
               Inc(LexTable[25].Count);
            end;
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
               Inc(LexTable[32].Count);
            end;   
         end;
      ')': 
         begin
            NextCh;
            Lex := lexRpar;
            Inc(LexTable[33].Count);
         end;
      '+': 
         begin
            NextCh;
            Lex := lexPlus;
            Inc(LexTable[19].Count);
         end;
      '-': 
         begin
            NextCh;
            Lex := lexMinus;
            Inc(LexTable[20].Count);
         end;
      '*': 
         begin
            NextCh;
            Lex := lexMult;
            Inc(LexTable[16].Count);
         end;
      chEOT: 
         begin
            Lex := lexEOT;
            Inc(LexTable[34].Count);
         end;
   else
      Error('Недопустимый символ');
   end;
end;

procedure InitScan;
begin
   nkw := 0;
   InitLT;
   EnterKW('ARRAY',     lexNone);
   EnterKW('BEGIN',     lexBEGIN);
   EnterKW('BY',        lexNone);
   EnterKW('CASE',      lexNone);
   EnterKW('CONST',     lexCONST);
   EnterKW('DIV',       lexDIV);
   EnterKW('DO',        lexDO);
   EnterKW('ELSE',      lexELSE);
   EnterKW('ELSIF',     lexELSIF);
   EnterKW('END',       lexEND);
   EnterKW('EXIT',      lexNone);
   EnterKW('FOR',       lexNone);
   EnterKW('IF',        lexIF);
   EnterKW('IMPORT',    lexIMPORT);
   EnterKW('IN',        lexNone);
   EnterKW('IS',        lexNone);
   EnterKW('LOOP',      lexNone);
   EnterKW('MOD',       lexMOD);
   EnterKW('MODULE',    lexMODULE);
   EnterKW('NIL',       lexNone);
   EnterKW('OF',        lexNone);
   EnterKW('OR',        lexNone);
   EnterKW('POINTER',   lexNone);
   EnterKW('PROCEDURE', lexNone);
   EnterKW('RECORD',    lexNone);
   EnterKW('REPEAT',    lexNone);
   EnterKW('RETURN',    lexNone);
   EnterKW('THEN',      lexTHEN);
   EnterKW('TO',        lexNone);
   EnterKW('TYPE',      lexNone);
   EnterKW('UNTIL',     lexNone);
   EnterKW('VAR',       lexVAR);
   EnterKW('WHILE',     lexWHILE);
   EnterKW('WITH',      lexNone);
   
   NextLex;
end;

//procedure CountOfLex;// Вывод
//var
//   i, j: integer;
//begin
//   for j := 1 to KWNum do 
//      for i := 1 to KWNum do 
//         if (KWTable[j].Lex = LexTable[i].Lex) then
//            Inc(LexTable[i].Count, KWTable[j].Count);
//   for i := 1 to KWNum do 
//      Writeln(LexTable[i].Lex, ' = ', LexTable[i].Count)
//end;

end.

