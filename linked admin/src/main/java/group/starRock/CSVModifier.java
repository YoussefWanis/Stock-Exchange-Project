package group.starRock;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
public class CSVModifier
{
    public static List<String> readCSVColumn(String fileName, int columnIndex)
    {
        List<String> columnData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                String[] values = line.split(",");
                if (values.length > columnIndex)
                    columnData.add(values[columnIndex]);
                else
                    columnData.add("");
            }
        }
        catch (IOException _) {}

        return columnData;
    }

    public static List<String> readCSVRow(String fileName, int rowIndex) throws IOException {
        List<String> rowData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                if (lineNumber == rowIndex) {
                    String[] values = line.split(",");
                    for (String value : values) {
                        rowData.add(value.trim());
                    }
                    return rowData;
                }
                lineNumber++;
            }
        }
        throw new IOException("Row index " + rowIndex + " not found in file " + fileName);
    }
    public static void writeDataToCSVTop(String filePath, List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String[] rowData : data) {
                writeRow(bw, rowData);
            }
        } catch (IOException _) {
        }
    }

    private static void writeRow(BufferedWriter bw, String[] rowData) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rowData.length; i++) {
            sb.append(rowData[i]);
            if (i < rowData.length - 1) {
                sb.append(",");
            }
        }
        sb.append("\n");
        bw.write(sb.toString());
    }
    public static void writeDataToCSVBelow(String filePath, List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String[] rowData : data) {
                writeRow(bw, rowData);
            }
        } catch (IOException _) {
        }
    }
    public static void createBlankCSV(String filePath) {
        try {
            FileWriter fw = new FileWriter(filePath);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.close();
        } catch (IOException _) {
        }
    }
    public static void clearCSVFile(String filePath) {
        try {
            new BufferedWriter(new FileWriter(filePath));
        }   catch (IOException _) {
        }
    }
    public static void editRow(String filePath, int rowIndex, List<String> newRowData) {
        List<String> fileContent = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentRow = 0;
            while ((line = br.readLine()) != null) {
                if (currentRow == rowIndex) {
                    line = String.join(",", newRowData);
                }
                fileContent.add(line);
                currentRow++;
            }
        } catch (IOException _) {
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : fileContent) {
                bw.write(line);
                bw.write("\n");
            }
        } catch (IOException _) {
        }
    }
    public static List<String> readCSV(String fileName) throws IOException {
        List<String> allData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                allData.add(line);
            }
        }
        return allData;
    }

    public static void writeDataToCSV(String filePath, String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to CSV file.");
        }
    }

        public static void removeSoldStocks(String filePath, String companyName, int quantity)
        {
            List<String> fileContent = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    fileContent.add(line);
                }
            } catch (IOException _) {}

            for (int i = 0; i < quantity; i++) {
                fileContent.remove(companyName);
            }


            try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                for (String line : fileContent) {
                    bw.write(line);
                    bw.newLine();
                }
            } catch (IOException _) {}
        }

}