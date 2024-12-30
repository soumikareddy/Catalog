import java.math.BigInteger;
import java.util.*;

public class Main {
    static class Coordinate {
        BigInteger xCoordinate;
        BigInteger yCoordinate;

        Coordinate(BigInteger x, BigInteger y) {
            this.xCoordinate = x;
            this.yCoordinate = y;
        }
    }

    public static void main(String[] args) {
        // Example input JSON strings
        String testCase1 = "{\"keys\":{\"n\":4,\"k\":3},\"1\":{\"base\":\"10\",\"value\":\"4\"},\"2\":{\"base\":\"2\",\"value\":\"111\"},\"3\":{\"base\":\"10\",\"value\":\"12\"},\"6\":{\"base\":\"4\",\"value\":\"213\"}}";
        String testCase2 = "{\"keys\":{\"n\":10,\"k\":7},\"1\":{\"base\":\"6\",\"value\":\"13444211440455345511\"},\"2\":{\"base\":\"15\",\"value\":\"aed7015a346d63\"},\"3\":{\"base\":\"15\",\"value\":\"6aeeb69631c227c\"},\"4\":{\"base\":\"16\",\"value\":\"e1b5e05623d881f\"},\"5\":{\"base\":\"8\",\"value\":\"316034514573652620673\"},\"6\":{\"base\":\"3\",\"value\":\"2122212201122002221120200210011020220200\"},\"7\":{\"base\":\"3\",\"value\":\"20120221122211000100210021102001201112121\"},\"8\":{\"base\":\"6\",\"value\":\"20220554335330240002224253\"},\"9\":{\"base\":\"12\",\"value\":\"45153788322a1255483\"},\"10\":{\"base\":\"7\",\"value\":\"1101613130313526312514143\"}}";

        System.out.println("Decrypted secret for Test Case 1: " + retrieveSecret(testCase1));
        System.out.println("Decrypted secret for Test Case 2: " + retrieveSecret(testCase2));
    }

    private static Map<String, String> extractJsonObject(String json, String key) {
        Map<String, String> parsedData = new HashMap<>();

        int startIndex = json.indexOf("\"" + key + "\":{");
        if (startIndex == -1) return parsedData;

        startIndex = json.indexOf("{", startIndex) + 1;
        int braceCounter = 1;
        int endIndex = startIndex;

        while (braceCounter > 0 && endIndex < json.length()) {
            if (json.charAt(endIndex) == '{') braceCounter++;
            if (json.charAt(endIndex) == '}') braceCounter--;
            endIndex++;
        }

        String jsonObject = json.substring(startIndex, endIndex - 1);
        String[] keyValuePairs = jsonObject.split(",");

        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                String keyString = keyValue[0].trim().replace("\"", "");
                String valueString = keyValue[1].trim().replace("\"", "");
                parsedData.put(keyString, valueString);
            }
        }

        return parsedData;
    }

    private static BigInteger retrieveSecret(String jsonInput) {
        Map<String, String> keysData = extractJsonObject(jsonInput, "keys");
        int k = Integer.parseInt(keysData.get("k"));

        List<Coordinate> dataPoints = new ArrayList<>();

        for (int i = 1; i <= 10 && dataPoints.size() < k; i++) {
            Map<String, String> dataPoint = extractJsonObject(jsonInput, String.valueOf(i));
            if (!dataPoint.isEmpty()) {
                int numberBase = Integer.parseInt(dataPoint.get("base"));
                String valueString = dataPoint.get("value");

                BigInteger xValue = BigInteger.valueOf(i);
                BigInteger yValue = new BigInteger(valueString, numberBase);

                dataPoints.add(new Coordinate(xValue, yValue));
            }
        }

        return performLagrangeInterpolation(dataPoints);
    }

    private static BigInteger performLagrangeInterpolation(List<Coordinate> points) {
        BigInteger result = BigInteger.ZERO;
        BigInteger targetX = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger term = points.get(i).yCoordinate;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger numerator = targetX.subtract(points.get(j).xCoordinate);
                    BigInteger denominator = points.get(i).xCoordinate.subtract(points.get(j).xCoordinate);
                    term = term.multiply(numerator).divide(denominator);
                }
            }

            result = result.add(term);
        }

        return result;
    }
}
