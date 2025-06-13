import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.print("$ ");
        Scanner scanner = new Scanner(System.in);

        while ( scanner.hasNextLine() ) {
            String input = scanner.nextLine();
            String command = input.split(" ")[0];
            switch (command) {
                case "exit" -> System.exit(0);
                case "echo" -> System.out.println(input.split(" ", 2)[1]);
                case "type" -> type( input );
                default -> System.err.printf("%s: command not found\n", input);
            }
            System.out.print("$ ");
        }
    }
    static void type ( String input ){
        String validCommands[] = { "exit" , "echo" , "type" };
        String command = input.split(" ",2)[1] ;
        String[] PATH = System.getenv("PATH").split(":");
        boolean isPresent = false ;
        for ( String validCommand :  validCommands ){
            if ( validCommand.equals(command) ){
                System.out.printf("%s is a shell builtin\n",command);
                return ;
            }
        }

        for ( String path : PATH ){
            File[] directory = new File(path).listFiles();
            // now check if the command exists in this directory
            if ( directory != null ) {
                for (File file : directory) {
                    if (file.getName().equals(command)) {
                        isPresent = true;
                        System.out.printf("%s is %s\n", command, file.getAbsolutePath());
                        return;
                    }
                }
            }
        }
        System.out.printf("%s: not found\n",command);
    }
}
