program O;

uses
  OText, OScan, OPars, OVM, OGen, OConverter, OParsForConverter;

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
   InitConvertor;
   Compile2;
   Done;    {����������}
   
end.


