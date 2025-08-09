grammar Expression;

// Parser rules
expr
    : ifExpression                                     #ifExpr
    | receiver=expr '.' call=ID '(' (expr (',' expr)*)? ')'     # qualifiedCall
    | receiver=ID '[' sliceExpr ']'                     # slice
    | ID '(' (expr (',' expr)*)? ')'             # call
    | castType '(' expr ')'                      # cast
    | (plus=PLUS | minus=MINUS) expr                           # unaryPlusMinus
    | not=NOT expr                                          # unaryNot
    | left=expr (mul=MULT | div=DIV | mod=MOD) right=expr                # mulDivMod
    | left=expr (plus=PLUS | minus=MINUS) right=expr                      # addSub
    | left=expr '..' right=expr                             # concat
    | left=expr compareOp right=expr                    # compare
    | left=expr (and=AND | or=OR) right=expr                     # logicalAndOr
    | TRUE                                       # trueLiteral
    | FALSE                                      # falseLiteral
    | ID                                         # identifier
    | INT                                        # intLiteral
    | FLOAT                                      # floatLiteral
    | STRING                                     # stringLiteral
    | '(' expr ')'                               # parenthesized
    ;

compareOp
        : EQUAL
        | NOT_EQUAL
        | LESS
        | GREATER
        | LESS_EQUAL
        | GREATER_EQUAL
        ;

ifExpression
    : 'if' condition thenBlock (elseIfBlock)* (elseBlock)?
    ;

condition
    : expr
    ;

thenBlock
    : 'then' expr
    | block
    ;

elseIfBlock
    : 'else' 'if' condition thenBlock
    ;

elseBlock
    : 'else' expr
    | 'else' block
    ;

block
    : '{' expr '}'
    ;

sliceExpr
    : index=expr                                       # singleSlice
    | startSlice=expr ':' endSlice=expr                              # sliceStartEnd
    | ':' endSlice=expr                                   # sliceEnd
    | startSlice=expr ':'                                   # sliceStart
    | startSlice=expr ':' endSlice=expr ':' stepSlice=expr                     # sliceWithStep
    | startSlice=expr ':' ':' stepSlice=expr                          # sliceStartStep
    | ':' endSlice=expr ':' stepSlice=expr                          # sliceEndStep
    ;

castType
    : 'int' #intCast
    | 'float' #floatCast
    | 'string' #stringCast
    ;

// Lexer rules
fragment DIGIT : [0-9] ;
fragment LETTER : [a-zA-Z] ;
fragment UNDERSCORE : '_' ;

INT     : DIGIT+ ;
FLOAT   : DIGIT+ '.' DIGIT+ ;
STRING  : '"' (~["\r\n] | '\\"')* '"'
        | '\'' (~['\r\n] | '\\\'')* '\''
        ;
TRUE    : 'true' ;
FALSE   : 'false' ;
ID      : (LETTER | UNDERSCORE) (LETTER | DIGIT | UNDERSCORE)* ;

// Operators and punctuation
CONCAT  : '..' ;
DOT     : '.' ;
LPAREN  : '(' ;
RPAREN  : ')' ;
LBRACKET: '[' ;
RBRACKET: ']' ;
COMMA   : ',' ;
COLON   : ':' ;
PLUS    : '+' ;
MINUS   : '-' ;
MULT    : '*' ;
DIV     : '/' ;
MOD     : '%' ;
EQUAL   : '==' ;
NOT_EQUAL : '!=' ;
LESS    : '<' ;
GREATER : '>' ;
LESS_EQUAL : '<=' ;
GREATER_EQUAL : '>=' ;
AND     : '&&' ;
OR      : '||' ;
NOT     : '!' ;

// Whitespace and comments
WS      : [ \t\r\n]+ -> skip ;
COMMENT : '/*' .*? '*/' -> skip ;