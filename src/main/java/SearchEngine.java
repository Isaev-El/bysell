import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchEngine {
    public static void main(String[] args) {
        List<String> database = Arrays.asList("Playstation", "iPhone", "Samsung", "Laptop");
        String searchQuery = "Ip45ne";
        String closestMatch = "";
        int minDistance = Integer.MAX_VALUE;

        for (String item : database) {
            int distance = StringUtils.getLevenshteinDistance(item.toLowerCase(), searchQuery.toLowerCase());

            if (distance < minDistance) {
                minDistance = distance;
                closestMatch = item;
            }
        }

        System.out.println("Closest match: " + closestMatch);
        System.out.println("Minimum distance: " + minDistance);
    }
}