import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Main {

    private static String[] code;
    private static int mcurrLine = 0;
    private static final Map<String, Integer> LABELS = new HashMap<>();
    private static final Map<String, Integer> REGISTERS = new HashMap<>();
    private static final Map<String, ArrayList<Integer>> SPECIALREGISTERS = new HashMap<>();

    private static final Scanner scanner = new Scanner(System.in);

    private static String currRegister;

    public static void main(String[] args) throws IOException {
        code = readFile(args[0]).split("\n");
        getAllLabels(code);
        execute(code);
    }

    private static String readFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            if (line.startsWith(";")) {
                line = reader.readLine();
                continue;
            } else if (line.contains(";")) {
                line = line.substring(0, line.indexOf(";"));
            }
            if (line.strip().equals("")) {
                line = reader.readLine();
                continue;
            }
            sb.append(line);
            sb.append(System.lineSeparator());
            line = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }

    private static void execute(String[] code) {
        long currTime = System.currentTimeMillis();
        while (mcurrLine < code.length) {
            String[] lineData = getLineData(code[mcurrLine]);
            executeLine(lineData, mcurrLine, currTime);
            mcurrLine++;
        }
        scanner.close();
    }

    private static void getAllLabels(String[] code) {
        for (int i = 0; i < code.length; i++) {
            String[] lineData = getLineData(code[i]);
            if (!lineData[0].isEmpty()) {
                LABELS.put(lineData[0], i);
            }
        }
        for (String label : LABELS.keySet()) {
            System.out.println(label + " " + LABELS.get(label));
        }
    }

    private static void executeLine(String[] line, int currLine, long currTime) {
        String command = line[1];
        String[] args = line[2].split(" ");
        
        if (command.equals("END")) {
            System.out.println("-".repeat(40));
            System.out.println("Successfully executed program in " + (System.currentTimeMillis() - currTime) / 1000.0
                    + " seconds");
            System.exit(0);
        } else if (command.equals("MOV")) {
            currRegister = args[0];
        } else if (command.equals("READ")) {
            System.out.println("-".repeat(40));
            System.out.print("Enter a value for " + currRegister + ": ");
            int myVal = scanner.nextInt();
            REGISTERS.put(currRegister, myVal);
            System.out.println("-".repeat(40));
        } else if (command.equals("LOAD")) {
            int value = args[0].startsWith("=") ? Integer.parseInt(args[0].substring(1))
                    : REGISTERS.getOrDefault(args[0], 0);
            REGISTERS.put(currRegister, value);
        } else if (command.equals("ADD")) {
            int value = args[0].startsWith("=") ? Integer.parseInt(args[0].substring(1))
                    : REGISTERS.getOrDefault(args[0], 0);
            REGISTERS.put(currRegister, REGISTERS.getOrDefault(currRegister, 0) + value);
        } else if (command.equals("SUB")) {
            int value = args[0].startsWith("=") ? Integer.parseInt(args[0].substring(1))
                    : REGISTERS.getOrDefault(args[0], 0);
            REGISTERS.put(currRegister, REGISTERS.getOrDefault(currRegister, 0) - value);
        } else if (command.equals("MUL")) {
            int value = args[0].startsWith("=") ? Integer.parseInt(args[0].substring(1))
                    : REGISTERS.getOrDefault(args[0], 0);
            REGISTERS.put(currRegister, REGISTERS.getOrDefault(currRegister, 0) * value);
        } else if (command.equals("DIV")) {
            int value = args[0].startsWith("=") ? Integer.parseInt(args[0].substring(1))
                    : REGISTERS.getOrDefault(args[0], 0);
            REGISTERS.put(currRegister, REGISTERS.getOrDefault(currRegister, 0) / value);
        } else if (command.equals("MOD")) {
            int value = args[0].startsWith("=") ? Integer.parseInt(args[0].substring(1))
                    : REGISTERS.getOrDefault(args[0], 0);
            REGISTERS.put(currRegister, REGISTERS.getOrDefault(currRegister, 0) % value);
        } else if (command.equals("CMP")) {
            int value = args[0].startsWith("=") ? Integer.parseInt(args[0].substring(1))
                    : REGISTERS.getOrDefault(args[0], 0);
            System.out.println(value);
            if (REGISTERS.getOrDefault(currRegister, 0) > value) {
                REGISTERS.put("CMPR", 1);
            } else if (REGISTERS.getOrDefault(currRegister, 0) < value) {
                REGISTERS.put("CMPR", -1);
            } else {
                REGISTERS.put("CMPR", 0);
            }
        } else if (command.equals("JMP")) {
            mcurrLine = LABELS.get(args[0]) - 1;
        } else if (command.equals("JZ")) {
            if (REGISTERS.getOrDefault(currRegister, 0) == 0) {
                mcurrLine = LABELS.get(args[0]) - 1;
            }
        } else if (command.equals("JNZ")) {
            if (REGISTERS.getOrDefault(currRegister, 0) != 0) {
                mcurrLine = LABELS.get(args[0]) - 1;
            }
        } else if (command.equals("PRINT")) {
            if (args[0].startsWith("S")) {
                ArrayList<Integer> register = SPECIALREGISTERS.get(args[0]);
                if (register == null) {
                    register = new ArrayList<Integer>();
                    SPECIALREGISTERS.put(args[0], register);
                }
                System.out.println(register);
            } else {
                System.out.println(REGISTERS.getOrDefault(args[0], 0));
            }
        } else if (command.equals("FLAG")) {
            System.out.print("FLAG --> ");
            for (String arg : args) {
                System.out.print(arg + " ");
            }
            System.out.println();
        } else if (command.equals("PUSH")) {
            int value = args[0].startsWith("=") ? Integer.parseInt(args[0].substring(1))
                    : REGISTERS.getOrDefault(args[0], 0);
            ArrayList<Integer> register = SPECIALREGISTERS.get(currRegister);
            if (register == null) {
                register = new ArrayList<Integer>();
                SPECIALREGISTERS.put(currRegister, register);
            }
            register.add(value);
        } else if (command.equals("POP")) {
            if (args[0].startsWith("LAST")) {
                ArrayList<Integer> register = SPECIALREGISTERS.get(currRegister);
                if (register == null || register.isEmpty()) {
                    System.out.println("Error: Cannot POP from empty register.");
                    return;
                }
                register.remove(register.size() - 1);
                return;
            }
            else {
                int value = args[0].startsWith("=") ? Integer.parseInt(args[0].substring(1))
                        : REGISTERS.getOrDefault(args[0], 0);
                ArrayList<Integer> register = SPECIALREGISTERS.get(currRegister);
                if (register == null || register.isEmpty()) {
                    System.out.println("Error: Cannot POP from empty register.");
                    return;
                }
                register.remove(value);
                return;
            }
        }
    }

    private static String[] getLineData(String line) {
        line = line.trim();
        line = Pattern.compile("\\s+").matcher(line).replaceAll(" ");
        // Make sure that contains EXACTLY END, not ENDING
        if (line.contains("*END")) {
            // Check if line contains LABEL
            if (line.split(" ").length == 2) {
                return new String[] {line.split(" ")[0], "END", ""};
            } 
            return new String[] { "", "END", "" };
        } else if (line.split(" ").length == 2) {
            return new String[] { "", line.split(" ")[0], line.split(" ")[1] };
        } else {
            return line.split(" ");
        }
    }

}
