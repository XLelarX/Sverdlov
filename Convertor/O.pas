program O;

uses
  OText, OScan, OPars;

procedure Init;
begin
   ResetText;
   InitScan;
end;

procedure Done;
begin
   CloseText;
end;

begin
   Init;    {Инициализация}
   Convert; {Конвертация}
   Done;    {Завершение}
   
end.


