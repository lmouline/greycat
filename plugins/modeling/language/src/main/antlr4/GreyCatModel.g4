/**
 * Copyright 2017 The GreyCat Authors.  All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
grammar GreyCatModel;

fragment ESC :   '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;

STRING :  '"' (ESC | ~["\\])* '"' | '\'' (ESC | ~["\\])* '\'' ;
IDENT : [a-zA-Z_][a-zA-Z_0-9]*;
TYPE_NAME : [a-zA-Z_][a-z0-9]*;
NUMBER : [\-]?[0-9]+'.'?[0-9]*;
WS : ([ \t\r\n]+ | SL_COMMENT) -> skip ; // skip spaces, tabs, newlines
SL_COMMENT :  '//' ~('\r' | '\n')* ;

model: (enumDec | classDec)* ;

enumDec : 'enum' (TYPE_NAME|IDENT) '{' enumLiterals '}';
enumLiterals : IDENT (',' IDENT)*;

classDec : 'class' (TYPE_NAME|IDENT) parentDec? '{' (attributeDec | relationDec | indexDec)* '}';
parentDec : 'extends' (TYPE_NAME|IDENT);
attributeType : ('String' | 'Double' | 'Long' | 'Integer' | 'Boolean') ('[]')?;
attributeDec : 'att' IDENT ':' attributeType;
relationDec : 'rel' IDENT ':' (TYPE_NAME|IDENT);

indexDec : 'index' IDENT (',' IDENT)* ('as' indexName)?;
indexName : IDENT;