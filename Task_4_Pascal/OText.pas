unit OText;
{Драйвер исходного текста}

interface

const
   chSpace = ' '; {Пробел }
   chTab = #9; {Табуляция }
   chEOL = #10; {Конец строки}
   chEOT = #0; {Конец текста}
var
   Ch : char; {Очередной символ }
   Line : integer; {Номер строки }
   Pos : integer; {Номер символа в строке}

procedure NextCh;
procedure ResetText;
procedure CloseText;

{======================================================}

implementation

uses
   OError;

const
   TabSize = 3;

var
   f : text;

procedure ResetText;
begin
   Assign(f, 'TEST.txt');
   Reset(f);
   Pos := 0;
   Line := 1;
   NextCh;
end;

procedure CloseText;
begin
   Close(f);
//   Close(outF);
end;

procedure NextCh;
begin
   if eof(f) then
      Ch := chEOT
   else if eoln(f) then begin
      ReadLn(f);
      //WriteLn;
      Line := Line + 1;
      Pos := 0;
      Ch := chEOL;
      end
   else begin
      Read(f, Ch);
      if Ch <> chTab then begin
         //Write(Ch);
         Pos := Pos+1;
         end
      else
         repeat
            //Write(' ');
            Pos := Pos+1;
         until Pos mod TabSize = 0;
   end;
end;

end.