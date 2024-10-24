import re
import sys
import time

class Main:
    def __init__(self):
        self.code = []
        self.mcurrLine = 0
        self.LABELS = {}
        self.REGISTERS = {}
        self.SPECIALREGISTERS = {}
        self.currRegister = None

    def main(self, filename):
        self.code = self.read_file(filename).splitlines()
        self.get_all_labels(self.code)
        self.execute(self.code)

    def read_file(self, filename):
        with open(filename, 'r') as f:
            lines = f.readlines()
        
        sb = []
        for line in lines:
            if line.startswith(';'):
                continue
            elif ';' in line:
                line = line[:line.index(';')]
            if line.strip() == '':
                continue
            sb.append(line.strip())
        return '\n'.join(sb)

    def execute(self, code):
        currTime = time.time()
        while self.mcurrLine < len(code):
            lineData = self.get_line_data(code[self.mcurrLine])
            self.execute_line(lineData, self.mcurrLine, currTime)
            self.mcurrLine += 1

    def get_all_labels(self, code):
        for i, line in enumerate(code):
            lineData = self.get_line_data(line)
            if lineData[0]:
                self.LABELS[lineData[0]] = i

        for label, line in self.LABELS.items():
            print(f"{label} {line}")

    def execute_line(self, line, currLine, currTime):
        command = line[1]
        args = line[2].split(' ')
        
        if command == "END":
            print('-' * 40)
            print(f"Successfully executed program in {(time.time() - currTime):.2f} seconds")
            sys.exit(0)
        elif command == "MOV":
            self.currRegister = args[0]
        elif command == "READ":
            print('-' * 40)
            input_value = input(f"Enter a value for {self.currRegister}: ")
            self.REGISTERS[self.currRegister] = int(input_value)
        elif command == "LOAD":
            value = int(args[0][1:]) if args[0].startswith('=') else self.REGISTERS.get(args[0], 0)
            self.REGISTERS[self.currRegister] = value
        elif command == "ADD":
            value = int(args[0][1:]) if args[0].startswith('=') else self.REGISTERS.get(args[0], 0)
            self.REGISTERS[self.currRegister] = self.REGISTERS.get(self.currRegister, 0) + value
        elif command == "SUB":
            value = int(args[0][1:]) if args[0].startswith('=') else self.REGISTERS.get(args[0], 0)
            self.REGISTERS[self.currRegister] = self.REGISTERS.get(self.currRegister, 0) - value
        elif command == "MUL":
            value = int(args[0][1:]) if args[0].startswith('=') else self.REGISTERS.get(args[0], 0)
            self.REGISTERS[self.currRegister] = self.REGISTERS.get(self.currRegister, 0) * value
        elif command == "DIV":
            value = int(args[0][1:]) if args[0].startswith('=') else self.REGISTERS.get(args[0], 0)
            self.REGISTERS[self.currRegister] = self.REGISTERS.get(self.currRegister, 0) // value
        elif command == "MOD":
            value = int(args[0][1:]) if args[0].startswith('=') else self.REGISTERS.get(args[0], 0)
            self.REGISTERS[self.currRegister] = self.REGISTERS.get(self.currRegister, 0) % value
        elif command == "CMP":
            value = int(args[0][1:]) if args[0].startswith('=') else self.REGISTERS.get(args[0], 0)
            if self.REGISTERS.get(self.currRegister, 0) > value:
                self.REGISTERS["CMPR"] = 1
            elif self.REGISTERS.get(self.currRegister, 0) < value:
                self.REGISTERS["CMPR"] = -1
            else:
                self.REGISTERS["CMPR"] = 0
        elif command == "JMP":
            self.mcurrLine = self.LABELS[args[0]] - 1
        elif command == "JZ":
            if self.REGISTERS.get(self.currRegister, 0) == 0:
                self.mcurrLine = self.LABELS[args[0]] - 1
        elif command == "JNZ":
            if self.REGISTERS.get(self.currRegister, 0) != 0:
                self.mcurrLine = self.LABELS[args[0]] - 1
        elif command == "PRINT":
            if args[0].startswith("S"):
                register = self.SPECIALREGISTERS.get(args[0], [])
                print(register)
            else:
                print(self.REGISTERS.get(args[0], 0))
        elif command == "FLAG":
            print("FLAG -->", ' '.join(args))
        elif command == "PUSH":
            value = int(args[0][1:]) if args[0].startswith('=') else self.REGISTERS.get(args[0], 0)
            register = self.SPECIALREGISTERS.setdefault(self.currRegister, [])
            register.append(value)
        elif command == "POP":
            if args[0] == "LAST":
                register = self.SPECIALREGISTERS.get(self.currRegister, [])
                if not register:
                    print("Error: Cannot POP from empty register.")
                else:
                    register.pop()
            else:
                value = int(args[0][1:]) if args[0].startswith('=') else self.REGISTERS.get(args[0], 0)
                register = self.SPECIALREGISTERS.get(self.currRegister, [])
                if not register:
                    print("Error: Cannot POP from empty register.")
                else:
                    register.remove(value)

    def get_line_data(self, line):
        line = line.strip()
        line = re.sub(r'\s+', ' ', line)
        
        if '*END' in line:
            if len(line.split(' ')) == 2:
                return [line.split(' ')[0], 'END', '']
            return ['', 'END', '']
        elif len(line.split(' ')) == 2:
            return ['', line.split(' ')[0], line.split(' ')[1]]
        else:
            return line.split(' ')

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python main.py <filename>")
    else:
        main = Main()
        main.main(sys.argv[1])
