unit OParsForConverter;
{ Распознаватель }

interface

procedure Compile2;

{======================================================}

implementation

uses
   OScan, OText;

procedure Compile2;
var
   i : integer;
begin
   While Lex <> lexEOT do begin
      NextLex;
      write(Lex);
   end;
  
   Lex := lexEOT; 
end;

end.