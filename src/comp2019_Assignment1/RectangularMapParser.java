package comp2019_Assignment1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Creates a rectangular map from a file or string.
 */
public class RectangularMapParser {

    public static RectangularMap fromString(String repr) {
        String[] lines = repr.trim().split("\n");
        return fromLines(new ArrayList<>(Arrays.asList(lines)));
    }

    public static RectangularMap fromFile(String filePath) {
        return fromFile(FileSystems.getDefault().getPath(filePath));
    }

    public static RectangularMap fromFile(Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            return fromLines(lines);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static RectangularMap fromLines(List<String> lines) {
        // skip any comment header
        while( lines.size() > 0 && lines.get(0).trim().startsWith("#") ) {
            lines.remove(0);
        }

        // peek at the first row to determine the map dimensions
        int rows = lines.size();
        int cols = lines.get(0).trim().split("\\p{javaWhitespace}+").length;

        // parse the array of integers and store them into the map
        ArrayMap map = new ArrayMap(rows, cols);
        for (int r = 0; r < rows; r++) {
            Scanner scanner = new Scanner(lines.get(r));
            for (int c = 0; c < cols; c++) {
                map.setValueAt(r, c, scanner.nextInt());
            }
            scanner.close();
        }

        return map;
    }

}
