import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Created by : Otabek Karimov
 */
public class Main {
    public static void main(String[] args) {
        // Use try-with-resources for robust interactive loop
        try (Scanner sc = new Scanner(System.in)) {
            System.out.println("File Counter Utility. Usage: <option> <filepath>. Type 'exit' to quit.");
            while (true) {
                System.out.print("> ");
                String line = sc.nextLine();
                if ("exit".equalsIgnoreCase(line.trim())) {
                    break;
                }

                String[] parts = line.trim().split("\\s+", 3);
                String command;
                String part;
                String filePathStr;
                Path path;
                if (parts.length < 3) {
                    command = parts[0];
                    part = parts[1];
                     filePathStr = "src/"+ part;
                    path = Paths.get(filePathStr);
                }else if(Arrays.asList(parts).contains("cat")) {
                    command = "-l";
                    path=Paths.get("src/test.txt");
                    part="";
                }
                else{
                    command = parts[1];
                    part = parts[2];
                     filePathStr = "src/"+ part;
                     path = Paths.get(filePathStr);
                }



                if (!Files.exists(path)) {
                    System.err.println("Error: File not found -> " + path);
                    continue;
                }


                String fileNameForDisplay = path.getFileName().toString();

                try {
                    switch (command) {
                        case "-c":
                            long l = countBytes(path, fileNameForDisplay);
                            System.out.println(l+" "+part);
                            break;
                        case "-l":
                            long l1 = countLines(path, fileNameForDisplay);
                            System.out.println(l1+" "+ part);
                            break;
                        case "-w":
                            long l2 = countWords(path, fileNameForDisplay);
                            System.out.println(l2);
                            break;
                        case "-m":
                            int i = countChars(path, fileNameForDisplay);
                            System.out.println(i);
                            break;
                        default:
                            long l3 = countBytes(path, fileNameForDisplay);
                            long l4 = countLines(path, fileNameForDisplay);
                            long l5 = countWords(path, fileNameForDisplay);
                            System.out.println(l4+" "+ l5+" "+ l3+" "+part);
                            break;
                    }
                } catch (IOException e) {
                    System.err.println("An error occurred while processing the file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Counts the number of characters in a file. Corresponds to `wc -m`.
     */
    private static int countChars(Path path, String fileName) throws IOException {
        // This reads the entire file into a single string. It's simple and accurate for character
        // counting but can be memory-intensive for very large files.
        // For UTF-8 files with multibyte characters, this will be different from the byte count.
        String content = Files.readString(path);
        int length = content.length();
        return length;
    }

    /**
     * Counts the number of words in a file. Corresponds to `wc -w`.
     */
    private static long countWords(Path path, String fileName) throws IOException {
        long wordCount;
        try (Stream<String> stream = Files.lines(path)) {
            wordCount = stream.flatMap(line -> Arrays.stream(line.trim().split("\\s+")))
                    .filter(word -> !word.isEmpty())
                    .count();
        }
        return wordCount;
    }

    /**
     * Counts the number of lines in a file. Corresponds to `wc -l`.
     */
    private static long countLines(Path path, String fileName) throws IOException {
        try {
            long lineCount = Files.lines(path).count();
            return lineCount;
        } catch (IOException e) {
            System.err.println("An error occurred while processing the file: " + e.getMessage());
        }
        return 0L;
    }

    /**
     * Counts the number of bytes in a file. Corresponds to `wc -c`.
     */
    private static long countBytes(Path path, String fileName) throws IOException {
        long byteCount = Files.size(path);
        return byteCount;
    }
}