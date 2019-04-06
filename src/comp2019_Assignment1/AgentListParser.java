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
 * Creates a list of Agents from a file or string.
 */
public class AgentListParser {

    public static List<Agent> fromString(String repr) {
        String[] lines = repr.trim().split("\n");
        return fromLines(new ArrayList<>(Arrays.asList(lines)));
    }

    public static List<Agent> fromFile(String filePath) {
        return fromFile(FileSystems.getDefault().getPath(filePath));
    }

    public static List<Agent> fromFile(Path filePath) {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            return fromLines(lines);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static List<Agent> fromLines(List<String> lines) {
        // skip any comment header
        while( lines.size() > 0 && lines.get(0).trim().startsWith("#") ) {
            lines.remove(0);
        }

        // parse the agents and gather them in a list
        List<Agent> agents = new ArrayList<>();
        for (String row: lines) {
            Scanner scanner = new Scanner(row);
            int priority = scanner.nextInt();
            Location startLoc =  new Location(scanner.nextInt(), scanner.nextInt());
            Location goalLoc =  new Location(scanner.nextInt(), scanner.nextInt());
            scanner.close();
            Agent agent = new Agent(priority, startLoc, goalLoc);
            agents.add(agent);
        }

        return agents;
    }

}
