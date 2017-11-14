# hextractor
Tool with different modes to help translate games using table files and fixed string length.  
Herramienta con diferentes modos para ayudar a traducir juegos utilizando tablas y cadenas de longitud fija.  
#### Type -? for help. / Escribe -? para ayuda  
#### EXTRACT ASCII FILE FROM HEX / EXTRAER ARCHIVO ASCII DE HEXADECIMAL  
-a tableFile file scriptAsciiFile offsetsList (START-END-STRING_END_CHAR(1+))  
#### INSERT ASCII AS HEX / INSERTAR ASCII COMO HEX  
-h scriptAsciiFile tableFile targetFile  
#### INSERT DIRECT HEX VALUES / INSERTAR HEXADECIMAL DIRECTO  
-ih scriptHexFile targetFile  
#### EXTRACT DIRECT HEX VALUES / EXTRAER HEXADECIMAL  
-eh srcFile destFile ((INIT-END,)|(INIT:LENGTH,))+  
#### INTERLEAVE FILES / INTERCALAR ARCHIVOS  
-i evenLinesFile oddLinesFile FIX MEGADRIVE CHECKSUM / REPARAR CHECKSUM MEGADRIVE  
-fcm rom FIX GAME BOY CHECKSUM / REPARAR CHECKSUM GAME BOY  
-fcg rom  
#### FIX SNES CHECKSUM / REPARAR CHECKSUM SNES  
-fcs rom  
#### FIX ZX TAP CHECKSUM / REPARAR CHECKSUM ZX TAP  
-fctap tap  
#### FIX ZX TAP CHECKSUM / REPARAR CHECKSUM ZX TAP  
-fctap tap originalTap  
#### FIX ZX TZX OR CPC CDT CHECKSUM / REPARAR CHECKSUM ZX TZX O CPC CDT  
-fctap tzx/cdt   
#### FIX ZX TZX OR CPC CDT CHECKSUM / REPARAR CHECKSUM ZX TZX O CPC CDT  
-fctap tzx/cdt originalTzx/Cdt  
#### CLEAN EXTRACTED TEXT FILE / LIMPIAR FICHERO DE TEXTO EXTRAIDO  
-ca file fileCleaned  
#### SEARCH ALL STRINGS / BUSCAR TODAS LAS CADENAS  
-sa table file maxIgnoredUnknownChars lineEndChars dictFile (optional)  
#### CLEAN EXTRACTED FILE / LIMPIAR ARCHIVO EXTRACCION  
-cef extractFile fileOut  
#### TRANSLATE SIMILAR / TRADUCIR SIMILAR  
-trs toTransFile transFile outputFile  
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
