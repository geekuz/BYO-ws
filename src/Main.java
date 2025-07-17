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
                if (parts.length < 2) {
                    System.err.println("Invalid input. Please provide both an option and a filepath.");
                    continue;
                }

                String command = parts[1];
                String filePathStr = "src/"+parts[2];
                Path path = Paths.get(filePathStr);

                if (!Files.exists(path)) {
                    System.err.println("Error: File not found -> " + filePathStr);
                    continue;
                }


                String fileNameForDisplay = path.getFileName().toString();

                try {
                    switch (command) {
                        case "-c":
                            countBytes(path, fileNameForDisplay);
                            break;
                        case "-l":
                            countLines(path, fileNameForDisplay);
                            break;
                        case "-w":
                            countWords(path, fileNameForDisplay);
                            break;
                        case "-m":
                            countChars(path, fileNameForDisplay);
                            break;
                        default:
                            System.err.println("Unknown command: " + command);
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
    private static void countChars(Path path, String fileName) throws IOException {
        // This reads the entire file into a single string. It's simple and accurate for character
        // counting but can be memory-intensive for very large files.
        // For UTF-8 files with multibyte characters, this will be different from the byte count.
        String content = Files.readString(path);
        System.out.println(content.length() + " " + fileName);
    }

    /**
     * Counts the number of words in a file. Corresponds to `wc -w`.
     */
    private static void countWords(Path path, String fileName) throws IOException {
        long wordCount;
        try (Stream<String> stream = Files.lines(path)) {
            wordCount = stream.flatMap(line -> Arrays.stream(line.trim().split("\\s+")))
                    .filter(word -> !word.isEmpty())
                    .count();
        }
        System.out.println(wordCount + " " + fileName);
    }

    /**
     * Counts the number of lines in a file. Corresponds to `wc -l`.
     */
    private static void countLines(Path path, String fileName) throws IOException {
        try {
            long lineCount = Files.lines(path).count();
            System.out.println(lineCount + " " + fileName);
        } catch (IOException e) {
            System.err.println("An error occurred while processing the file: " + e.getMessage());
        }
    }

    /**
     * Counts the number of bytes in a file. Corresponds to `wc -c`.
     */
    private static void countBytes(Path path, String fileName) throws IOException {
        long byteCount = Files.size(path);
        System.out.println(byteCount + " " + fileName);
    }
}