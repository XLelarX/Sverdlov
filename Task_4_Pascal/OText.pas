unit OText;
{������� ��������� ������}

interface

const
   chSpace = ' '; {������ }
   chTab = #9; {��������� }
   chEOL = #10; {����� ������}
   chEOT = #0; {����� ������}
var
   Ch : char; {��������� ������ }
   Line : integer; {����� ������ }
   Pos : integer; {����� ������� � ������}

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