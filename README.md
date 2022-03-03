  ![Build Status](https://github.com/sewave/hextractor/actions/workflows/maven.yml/badge.svg?branch=master)
  [![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=com.wave%3Ahextractor&metric=ncloc)](https://sonarcloud.io/dashboard?id=com.wave%3Ahextractor)
  [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=com.wave%3Ahextractor&metric=coverage)](https://sonarcloud.io/dashboard?id=com.wave%3Ahextractor)
  [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=com.wave%3Ahextractor&metric=bugs)](https://sonarcloud.io/dashboard?id=com.wave%3Ahextractor)
  [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=com.wave%3Ahextractor&metric=code_smells)](https://sonarcloud.io/dashboard?id=com.wave%3Ahextractor)
  [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=com.wave%3Ahextractor&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=com.wave%3Ahextractor) 

# hextractor
Tool with different modes to help translate games/files using table files and fixed string length.  
Herramienta con diferentes modos para ayudar a traducir juegos/ficheros utilizando tablas y cadenas de longitud fija.  
#### Type -? for help. / Escribe -? para ayuda  
#### EXTRACT ASCII FILE FROM HEX / EXTRAER ARCHIVO ASCII DE HEXADECIMAL  
-a tableFile file scriptAsciiFile offsetsList or file (START-END-STRING_END_CHAR(1+))  
#### EXTRACT ASCII FILE FROM HEX TURNING 3 8 BIT BYTES INTO 4 6 BIT BYTES / EXTRAER ARCHIVO ASCII DE HEXADECIMAL TRANSFORMANDO 3 BYTES DE 8 BITS EN 4 BYTES DE 6 BITS
-a34 tableFile file scriptAsciiFile offsetsList or file (START-END-STRING_END_CHAR(1+))  
#### INSERT ASCII AS HEX / INSERTAR ASCII COMO HEX  
-h scriptAsciiFile tableFile targetFile
#### INSERT BINARY FILE / INSERTAR ARCHIVO BINARIO
-if baseBinFile replacementFile hexOffset  
#### INSERT ASCII AS HEX TURNING 4 6 BIT BYTES INTO 3 8 BIT BYTES / INSERTAR ASCII COMO HEX TRANSFORMANDO 4 BYTES DE 6 BITS EN 3 BYTES DE 8 BITS
-h43 tableFile file scriptAsciiFile offsetsList or file (START-END-STRING_END_CHAR(1+))  
#### INSERT DIRECT HEX VALUES / INSERTAR HEXADECIMAL DIRECTO  
-ih scriptHexFile targetFile  
#### EXTRACT DIRECT HEX VALUES / EXTRAER HEXADECIMAL  
-eh srcFile destFile ((INIT-END,)|(INIT:LENGTH,))+  
#### FIX MEGADRIVE CHECKSUM / REPARAR CHECKSUM MEGADRIVE  
-fcm rom 
#### FIX GAME BOY CHECKSUM / REPARAR CHECKSUM GAME BOY  
-fcg rom  
#### FIX SNES CHECKSUM / REPARAR CHECKSUM SNES  
-fcs rom  
#### FIX ZX TAP CHECKSUM / REPARAR CHECKSUM ZX TAP  
-fctap tap  
#### FIX ZX TAP CHECKSUM / REPARAR CHECKSUM ZX TAP  
-fctap tap originalTap  
#### FIX ZX TZX OR CPC CDT CHECKSUM / REPARAR CHECKSUM ZX TZX O CPC CDT  
-fctzx tzx/cdt   
#### FIX ZX TZX OR CPC CDT CHECKSUM / REPARAR CHECKSUM ZX TZX O CPC CDT  
-fctzx tzx/cdt originalTzx/Cdt
#### FIX SMS EXPORT CHECKSUM / REPARAR CHECKSUM SMS NO JAPONESA
-fcsms sms file  
#### CLEAN EXTRACTED TEXT FILE / LIMPIAR FICHERO DE TEXTO EXTRAIDO  
-ca file fileCleaned  
#### SEARCH ALL STRINGS / BUSCAR TODAS LAS CADENAS  
-sa table file maxIgnoredUnknownChars lineEndChars dictFile (optional)  
#### CLEAN EXTRACTED FILE / LIMPIAR ARCHIVO EXTRACCION  
-cef extractFile fileOut  
#### FIND RELATIVE 8 bits / BUSCAR RELATIVO 8 bits  
-sr8 file baseTable word  
#### Hex Viewer / Visor Hexadecimal  
-hv file (optional) table (optional)  
#### CREATE IPS PATCH / CREAR PARCHE IPS 
 -cip originalFile modifiedFile patchFile  
#### VERIFY IPS PATCH / VERIFICAR PATCH IPS 
 -vip originalFile modifiedFile patchFile  
#### APPLY IPS PATCH / APLICAR PARCHE IPS  
-aip originalFile modifiedFile patchFile  
#### CHECK LINE LENGTHS / VERIFICAR TAMAÑO LINEA  
-cll extFile
#### SEPARATE BY FIRST CHAR LENGTH / SEPARAR POR TAMAÑO DEL PRIMER CARACTER
-scl file tableFile outFile
#### INSERT FILE AT OFFSET / INSERTAR FICHERO EN OFFSET
-if originalFile insertFile offset
#### GENERATE FILE DIGESTS / GENERAR HASHES DE FICHERO
-gd file
#### FILL READ ME / RELLENAR LEEME
-frm readme file

