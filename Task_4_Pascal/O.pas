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
   WriteLn('Компилятор языка O');
   Init;    {Инициализация}
   Compile; {Компиляция}
   Run;     {Выполнение}
   InitConvertor;
   Compile2;
   Done;    {Завершение}
   
end.


