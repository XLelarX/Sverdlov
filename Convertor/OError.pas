unit OError;
{��������� ������}

interface

procedure Error(Msg : string);
procedure Expected(Msg : string);
procedure Warning(Msg : string);

{=======================================================}

implementation

uses
   OText, OScan;

procedure Error(Msg : string);
var
   ELine : integer;
begin
   ELine := Line;
   while (Ch <> chEOL) and (Ch <> chEOT) do NextCh;
   if Ch = chEOT then WriteLn;
   WriteLn('^': LexPos);
   Writeln('(������ ', ELine, ') ������: ', Msg);
   WriteLn;
   WriteLn('������� ����');
   Readln;
   Halt(1);
end;

procedure Expected(Msg: string);
begin
   Error('��������� ' + Msg);
end;

procedure Warning(Msg : string);
begin
   WriteLn;
   Writeln('��������������: ', Msg);
   WriteLn;
end;

end.

