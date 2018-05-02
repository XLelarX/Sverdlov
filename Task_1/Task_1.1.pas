program Task_1;

const
   EOT = #0;

type
   tOrdFrac = record
      nom, denom: integer;
   end;

type
   StrType = string[80];

var
   sum, f: tOrdFrac;
   ErrPos: integer;
   Ch: char;  
   i: integer; 
   Op: char;   
   S: StrType; 
   Int: integer;

procedure NextChar;
begin
   if ErrPos <> 0 then
      Ch := EOT
   else      
      repeat
         i := i + 1; 
         if i <= Length(S) then 
            Ch := S[i] 
         else 
            Ch := EOT; 
      until Ch <> ' ';
end;

procedure Error(Message: StrType );
var
   e: integer;
begin
   if ErrPos = 0 then begin
      WriteLn; 
      WriteLn('^':i );
      Writeln('Синтаксическая ошибка: ', Message);
      e := i;
      while Ch <> EOT do NextChar;
      ErrPos := e;
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
         c.nom := (a.nom * b.denom) + (a.denom * b.nom);
         c.denom := a.denom * b.denom;
      end  
      else
         Error('Переполнение типа Int');
   end      
   else
      Error('В знаменателе ноль');
   c := Reduction(c); 
end;

procedure DifFr(a, b: tOrdFrac; var c: tOrdFrac);
begin
   if (a.denom <> 0) and (b.denom <> 0) then 
   begin
      if (MaxInt div abs(b.denom) >= abs(a.denom)) and (MaxInt div abs(a.denom) >= abs(b.denom)) and (MaxInt - abs(a.nom * b.denom) >= b.nom * a.denom) and (MaxInt - abs(b.nom * a.denom) >= a.nom * b.denom) then    
      begin
         c.nom := (a.nom * b.denom) - (a.denom * b.nom);
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

procedure ResetText;
begin
   WriteLn('Введите числовой ряд' );
   ReadLn(S);
   i := 0;
   NextChar;
end;

procedure IntNumber;
var
   d: integer;
begin
   Int := 0;                    
   if not (Ch in ['0'..'9'] )  then
      Error('Ожидается цифра' );
   while Ch in ['0'..'9'] do 
   begin
      d := ord(Ch) - ord('0'); 
      if Int <= (Maxint - d) div 10 then
         Int := 10 * Int + d    
      else
         Error('Слишком большое число');
      NextChar;
   end;      
end;

procedure Addend;
begin
   if ch = '1' then begin
      f.denom := 1;
      f.nom := 1;
      NextChar;    
   end
   else
      Error('Ожидается ''1''');
   if ch = '/' then begin
      NextChar;
      IntNumber;
      if ch = '^' then begin
         NextChar;
         if not (ch = '2') then
            Error('Ожидается ''2''')
         else  
            NextChar;
         f.denom := Int * Int;
      end
      else Error('Ожидается ''^''');
   end
   else 
      f.denom := 1;
end;

procedure Summa;
begin
   sum.nom := 0;
   if Ch = '1' then begin
      sum.nom := 1;
      sum.denom := 1;
      NextChar
   end   
   else   
      Error('Ожидается ''1''' );                                                                                
   sum.nom := 1;
   while Ch in ['+', '-'] do 
   begin
      Op := Ch;         
      NextChar; 
      Addend;
      if Op = '+' then  
         AddFr(sum, f, sum)  
      else
         DifFr(sum, f, sum);
   end;
   
end;

procedure Translate;
begin
   ResetText; 
   ErrPos := 0;
   i := 0;
   NextChar;
   Summa;
   if Ch <> EOT then
      Error('Ожидается конец текста');
   if ErrPos = 0 then
      FWrite(sum);
end;

begin
   Translate;
end.