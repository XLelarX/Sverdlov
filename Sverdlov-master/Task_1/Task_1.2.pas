program Task_1;

const
   nmax = 255;
   EOT = #0;

type
   tSynTable = array [1..16] of record
      ChSet: set of char;  {Символы}
      Go: integer;         {Переход}
      Err: Boolean;        {Ошибка}
      Call: Boolean;       {Вызов}
      Read: Boolean;       {Читать}
      Proc: integer;       {Семантическая процедура}
   end;
   
   tData = integer;
   
   tStack = record
      a: array [1..nmax] of tData;
      SP: integer;
   end;
   
   tOrdFrac = record
      nom, denom: integer;
   end;

type
   StrType = string[80];

var
   T: tSynTable;           {Таблица}
   Ch: char;               {Текущий символ}
   Err: integer;           {Признак ошибки}
   Stack: tStack;          {Стек}
   i: integer;             {Номер состояния}
   
   str: StrType;
   k: integer;             {Номер символа}
   ErrPos: integer;
   
   f: tOrdFrac;  
   sum: tOrdFrac;
   Op: char;
   Int: integer;{Значение целого}

procedure NextChar;
begin
   repeat
      k := k + 1; 
      if k <= Length(Str) then 
         Ch := Str[k] 
      else 
         Ch := EOT; 
   until Ch <> ' ';
end;

procedure ResetText;
begin
   sum.nom := 0;
   WriteLn('Введите числовой ряд: ' );
   ReadLn(Str);
   k := 0;
   NextChar; 
end;

procedure ErrorL(ChSet: set of char);
var
   i: integer;

begin
   Writeln('^':k);
   Write('Cинтаксическая ошибка: ожидается: ');
   for i := 0 to 255 do 
   begin
      if Chr(i) in ChSet then
         Write(Chr(i), ' ');
   end;
   WriteLn;
end;

procedure Error(Message: StrType );
var
   e: integer;
begin
   if ErrPos = 0 then begin
      WriteLn; 
      WriteLn('^':k );
      Writeln('Синтаксическая ошибка: ', Message);
      e := k;
      while Ch <> EOT do NextChar;
      ErrPos := e;
   end;  
end;


procedure LoadTable(var T: tSynTable);
var
   f: text;
   name: string;
   Ch: char;
   i: integer := 0;
   Error: integer;
begin
   Assign(f, 'table.txt');
   Reset(f);
   
   while not eof(f) do 
   begin
      Read(f, Ch);
      while Ch <> #9 do 
      begin
         str := str + Ch;
         Read(f, Ch);
      end;
      Val(str, i, Error);
      str := '';
      
      Read(f, Ch);
      while Ch <> #9 do 
      begin
         str := str + Ch;
         Read(f, Ch);
      end;
      
      if str = '[#0..#255]' then T[i].ChSet := [#0..#255]
      else if length(str) = length('[''+'',''-'']') then begin T[i].ChSet += [str[3]];T[i].ChSet += [str[7]]; end
      else if ((length(str) = length('[''1'']')) and (str[3] in ['0'..'9'])) then T[i].ChSet += [str[3]]
      else if str = '[''0''..''9'']' then T[i].ChSet := ['0'..'9']
      else if length(str) = Length('[''/'']') then T[i].ChSet += [str[3]]
      else if length(str) = Length('[''^'']') then T[i].ChSet += [str[3]]
      else if length(str) = Length('[''2'']') then T[i].ChSet += [str[3]];
      
      str := '';
      
      Read(f, Ch);
      while Ch = #9 do
         Read(f, Ch);
      while Ch <> #9 do 
      begin
         str := str + Ch;
         Read(f, Ch);
      end;
      Val(str, T[i].Go, Error);
      str := '';
      
      Read(f, Ch);
      if Ch = '+' then T[i].Err := true
      else if Ch = '-' then T[i].Err := false;
      
      Read(f, Ch);
      Read(f, Ch);
      if Ch = '+' then T[i].Call := true
      else if Ch = '-' then T[i].Call := false;
      
      Read(f, Ch);
      Read(f, Ch);
      if Ch = '+' then T[i].Read := true
      else if Ch = '-' then T[i].Read := false;
      
      Read(f, Ch);
      Read(f, Ch);
      Val(Ch, T[i].Proc, Error);
      
      if i <> 14 then begin
         Read(f, Ch);
         Read(f, Ch);
      end;
   end;
end;


function NOD(a: integer; b: integer): integer;
begin
   while(a <> 0) and (b <> 0) do
      if (abs(a) >= abs(b)) then a := a mod b
      else b := b mod a;
   NOD := a + b;
end;

function Reduction(a: tOrdFrac): tOrdFrac;
var
   n: integer;
begin
   if a.nom = a.denom then begin
      a.nom := 1;
      a.denom := 1;
      Reduction := a;
   end
   else if NOD(a.nom, a.denom) <> 1 then begin
      n := NOD(a.nom, a.denom);
      a.nom := a.nom div n;
      a.denom := a.denom div n;
   end;
   Reduction := a;
end;

procedure AddFr(a, b: tOrdFrac; var c: tOrdFrac);
begin
   if (a.denom <> 0) and (b.denom <> 0) then begin
      if (MaxInt div b.denom >= a.denom) and
             (MaxInt div a.denom >= b.denom) and
             (MaxInt - abs(a.nom * b.denom) >= b.nom * a.denom) and
             (MaxInt - abs(b.nom * a.denom) >= a.nom * b.denom) then 
      begin
         c.nom := (a.nom * b.denom) + (a.denom + b.nom);
         c.denom := a.denom * b.denom;
      end  
      else
         Error('Слишком большое число');
   end      
   else
      Error('В знаменателе ноль');
   
   c := Reduction(c); 
end;

procedure DifFr(a, b: tOrdFrac; var c: tOrdFrac);
begin
   if (a.denom <> 0) and (b.denom <> 0) then begin
      if (MaxInt div abs(b.denom) >= abs(a.denom)) and
             (MaxInt div abs(a.denom) >= abs(b.denom)) and
             (MaxInt - abs(a.nom * b.denom) >= b.nom * a.denom) and
             (MaxInt - abs(b.nom * a.denom) >= a.nom * b.denom) then 
      begin
         c.nom := (a.nom * b.denom) - (a.denom + b.nom);
         c.denom := a.denom * b.denom;
         
         if (a.nom < 0) then begin
            c.nom := c.nom * -1;
            c.denom := c.denom * -1;
         end;
         
      end  
      else
         Error('Переполнение типа Int');
   end      
   else
      Error('В знаменателе ноль');
   
   c := Reduction(c);   
end;

procedure FWrite(f: tOrdFrac);
begin
   if (f.denom < 0) then begin
      f.nom := -f.nom;
      f.denom := -f.denom;
   end;
   if f.denom = 1 then
      Write('Сумма: ', f.nom)
   else 
      Write('Сумма: ', f.nom, '/', f.denom);
end;

procedure Proc1(Ch: char);
begin
   Op := Ch;  
end;

procedure Proc2(Ch: char);
begin
   if Op = '+' then
      AddFr(sum, f, sum)  
   else
      DifFr(sum, f, sum);   
end;

procedure Proc7(Ch: char);
begin
   sum.denom := 1;
   case ch of
      '1': sum.nom := 1; 
      '2': sum.nom := 2;
      '3': sum.nom := 3;
      '4': sum.nom := 4;
      '5': sum.nom := 5;
      '6': sum.nom := 6;
      '7': sum.nom := 7;
      '8': sum.nom := 8;
      '9': sum.nom := 9;
   end;
end;

procedure Proc3(Ch: char);
begin
   f.denom := Int; 
end;

procedure Proc4(Ch: char);
begin
   f.denom := 1; 
   case ch of
      '1': begin f.nom := 0; end;
      '2': begin f.nom := 1; end;
      '3': begin f.nom := 2; end;
      '4': begin f.nom := 3; end;
      '5': begin f.nom := 4; end;
      '6': begin f.nom := 5; end;
      '7': begin f.nom := 6; end;
      '8': begin f.nom := 7; end;
      '9': begin f.nom := 8; end;
   end;
end;

procedure Proc5(Ch: char);
begin
   Int := 0; 
end;

procedure Proc6(Ch: char);
var
   d: integer;
begin
   d := ord(Ch) - ord('0');
   if (Int <= (Maxint - d) div 10) then
      Int := 10 * Int + d
   else
      Error('Слишком большое число');
end;

procedure Proc8(Ch: char);
begin
   Int := Int * Int;
end;

procedure Proc(i: integer);
begin
   case i of
      1: Proc1(Ch);
      2: Proc2(Ch);
      3: Proc3(Ch);
      4: Proc4(Ch);
      5: Proc5(Ch);
      6: Proc6(Ch);
      7: Proc7(Ch);
      8: Proc8(Ch);
   end;
end;

        {=======STACK======}

procedure Init(var S: tStack);
begin
   S.SP := 0;
end;

procedure Push(var S: tStack; D: tData);
begin
   if S.SP < nmax then begin
      S.SP := S.SP + 1;
      S.a[S.SP] := D;
   end
   else
      Error('Переполнение стека');
end;

procedure Pop(var S: tStack; var D: tData);
begin
   if S.SP > 0 then begin
      D := S.a[S.SP];
      S.SP := S.SP - 1;
   end
   else
      Error('Стек пуст');
end;

function NotEmpty(S: tStack): Boolean;
begin
   NotEmpty := S.SP > 0;
end;

function NotFull(S: tStack): Boolean;
begin
   NotFull := S.SP < nmax;
end;


begin
   LoadTable(T);
   ResetText;
   
   Init(Stack);
   Push(Stack, 0);
   Err := 0;
   i := 1;
   repeat
      if Ch in T[i].ChSet then begin
         if T[i].Proc <> 0 then Proc(T[i].Proc);
         if T[i].Read then NextChar;
         if T[i].Go = 0 then
            Pop(Stack, i)
         else begin
            if T[i].Call then Push(Stack, i + 1);
            i := T[i].Go;
         end;
      end
      else if T[i].Err then
         Err := i
      else
         i := i + 1;
   until (i = 0) or (Err <> 0);  
   
   if (Err <> 0) then
      ErrorL(T[Err].ChSet)
   
   
   else if (Ch <> EOT) then begin
      Writeln('^':k);
      WriteLn('Cинтаксическая ошибка: Ожидается конец текста')
   end
   else if (i = 0) and (ErrPos = 0) then begin
      WriteLn('Правильно');
      FWrite(sum);
   end;
   
end.

