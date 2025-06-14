import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                case "pwd" -> System.out.println(getPath(System.getProperty("user.dir")).toAbsolutePath().normalize());
                case "cd" -> changeDirectory(input.split(" ",2)[1]);
                default -> commandExec(input);
            }
            System.out.print("$ ");
        }
    }
    static void type ( String input ){
        String validCommands[] = { "exit" , "echo" , "type" , "pwd", "cd" };
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
    static void commandExec( String input ){
        String args[] = input.split(" ");
        try {
            ProcessBuilder builder = new ProcessBuilder(args);
            Process process = builder.start();
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
        catch (Exception e ) {
            System.out.printf("%s: command not found\n", input);
        }
    }

    static void changeDirectory ( String path ){

        Path workingDir = getPath(System.getProperty("user.dir"));
        Path normalizedPath = getPath(path);
        Path resolvedPath = workingDir.resolve(normalizedPath);
        if (Files.exists(resolvedPath) && Files.isDirectory(resolvedPath)){
            System.setProperty("user.dir", resolvedPath.toString());
        }
        else {
            System.out.printf("cd: %s: No such file or directory\n" , path);
        }
    }

    static Path getPath ( String path ){
        return Paths.get(path);
    }
}
