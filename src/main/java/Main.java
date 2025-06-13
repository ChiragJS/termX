import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.print("$ ");
        Scanner scanner = new Scanner(System.in);
        while ( true ) {
            String input = scanner.nextLine();
            if ( input.equals("exit 0") ) System.exit(0);
            String command[] = input.split(" ");
            if ( command[0].equals( "echo")){
                for ( int i = 1 ; i < command.length ; i++ ) System.out.print(command[i]+" ");
                System.out.println();
            }
            else {
                System.out.println(input + ": command not found");
            }
            System.out.print("$ ");

        }
    }
}
