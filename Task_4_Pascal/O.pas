program O;

uses
  OText, OScan, OPars, OVM, OGen;

procedure Init;
begin
   ResetText;
   InitScan;
   InitGen;
end;

procedure Done;
begin
   CloseText;
end;

begin
   WriteLn('���������� ����� O');
   Init;    {�������������}
   Compile; {����������}
   Run;     {����������}
   Done;    {����������}
end.


