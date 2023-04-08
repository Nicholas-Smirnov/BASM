# Java Assembly Interpreter
A simple interpreter that executes assembly-like code in Java.


To execute an assembly-like file, simply run Main.java with the path to your file as an argument. For example:

```
java Main.java MyFile
```

The interpreter will read in the file, remove comments and blank lines, and execute the resulting code.

### Syntax
The interpreter recognizes the following commands:

- MOV: set the current register to the specified register
- READ: read a value from the console and store it in the current register
- LOAD: load a value into the current register
- ADD: add a value to the current register
- SUB: subtract a value from the current register
- MUL: multiply the current register by a value
- DIV: divide the current register by a value
- MOD: compute the remainder of the current register divided by a value
- CMP: compare the current register to a value and set CMPR to 1, -1, or 0 depending on the result
- JMP: jump to the specified label
- JE: jump to the specified label if CMPR is 0
- JNE: jump to the specified label if CMPR is not 0
- JG: jump to the specified label if CMPR is 1
- JL: jump to the specified label if CMPR is -1
- JGE: jump to the specified label if CMPR is 1 or 0
- JLE: jump to the specified label if CMPR is -1 or 0
- *END: exit the interpreter

### Examples

A factorial program:

```
; A basic factorial program

        MOV     RXA
        READ    RXA
        MOV     RXC 
        LOAD    RXA 
        MOV     RXB
        LOAD    =1

START   MOV     RXB
        MUL     RXA
        MOV     SX1 
        PUSH    RXB
        MOV     RXA
        SUB     =1
        JNZ     START

        PRINT   SX1
        FLAG    FactorialOf
        PRINT   RXC
        FLAG    Is 
        PRINT   RXB

        *END
```
