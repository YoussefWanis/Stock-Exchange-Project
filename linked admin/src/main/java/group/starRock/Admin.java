package group.starRock;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
public class Admin extends Person {

    public Admin(String email, String password, String userName)
    {
        super(email, password, userName);
    }

    public Admin() {
    }

    @Override
    public String getUsername() {
        return "";
    }

    public void createStock(String companyName, float price, int volume) {
        // Get today's date
        LocalDate today = LocalDate.now();

        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        // Format the date
        String date = today.format(formatter);

        boolean alreadyCreated;
        List<String> alreadyCreatedCompanies;
        alreadyCreatedCompanies = CSVModifier.readCSVColumn("src/Stocks/Companies.CSV", 0);
        alreadyCreated = alreadyCreatedCompanies.contains(companyName);
        if (!alreadyCreated) {
            CSVModifier.createBlankCSV("src/Stocks/" + companyName + ".csv");
            List<String[]> addedRows = new ArrayList<>();
            String[] row1 = {"Date" , "Open" , "High" , "Low" , "Close" , "Volume"};

            String[] row2 = {date, "" + price, "" + price,"" + price,"" + price, "" + volume};
            addedRows.add(row1);
            addedRows.add(row2);
            CSVModifier.writeDataToCSVBelow("src/Stocks/" + companyName + ".csv", addedRows);
            addedRows.clear();
            addedRows.add(new String[]{companyName});
            CSVModifier.writeDataToCSVBelow("src/Stocks/Companies.CSV", addedRows);
        }
    }

    public void startSession() throws IOException {
        boolean activated;
        List<String> checkSessionList;
        checkSessionList = CSVModifier.readCSVColumn("src/Stocks/Session.CSV" , 0);
        activated = checkSessionList.size() > 1;
        if (!activated){
        List<String> companiesToActivate;
        companiesToActivate = CSVModifier.readCSVColumn("src/Stocks/Companies.CSV" , 0);
            for(String company: companiesToActivate){
                    int lastRowIndex = CSVModifier.readCSVColumn("src/Stocks/" + company + ".csv", 0).size() - 1;
                    List<String> lastRow = CSVModifier.readCSVRow("src/Stocks/" + company + ".csv", lastRowIndex);
                    double price = Double.parseDouble(lastRow.get(4));
                    int volume = Integer.parseInt(lastRow.get(5));
                    List<String[]> addRow = new ArrayList<>();
                    addRow.add(new String[]{company, "" + price, "" + price, "" + price, "" + volume});
                    CSVModifier.writeDataToCSVBelow("src/Stocks/Session.CSV",addRow);

            }
        }
    }

    public void closeSession(){
        boolean alreadyClosed;
        List<String> companies = CSVModifier.readCSVColumn("src/Stocks/session.CSV" , 0);
        alreadyClosed = !(companies.size()>1);
        if(!alreadyClosed){
            LocalDate today = LocalDate.now();

            // Define the date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            List<String> priceHigh = CSVModifier.readCSVColumn("src/Stocks/Session.CSV" , 1);
            List<String> priceLow = CSVModifier.readCSVColumn("src/Stocks/Session.CSV" , 2);
            List<String> closePrice = CSVModifier.readCSVColumn("src/Stocks/Session.CSV" , 3);
            List<String> volume = CSVModifier.readCSVColumn("src/Stocks/Session.CSV" , 4);
            // Format the date
            String formattedDate = today.format(formatter);
            //gets the data of the csv file to the history of each company

            for (int i = 0; i < companies.size()-1; i++) {
                String openPrice = CSVModifier.readCSVColumn("src/Stocks/"+companies.get(i+1)+".CSV" , 4).getLast();
                String[] row = new String[]{formattedDate ,openPrice,priceHigh.get(i) , priceLow.get(i) ,closePrice.get(i) , volume.get(i)};
                List<String[]> addRow = new ArrayList<>();
                addRow.add(row);
                CSVModifier.writeDataToCSVBelow("src/Stocks/"+companies.get(i)+".CSV",addRow);
            }

            //clears the session csv file
            CSVModifier.clearCSVFile("src/Stocks/Session.CSV");
            List<String[]> addRow = new ArrayList<>();
            addRow.add(new String[] {"CompanyName","PriceHigh","PriceLow","CurrentPrice","Volume"});
            CSVModifier.writeDataToCSVTop("src/Stocks/Session.CSV", addRow);
        }
    }

    public void removeStock(String company) {
        List<String> companies = CSVModifier.readCSVColumn("src/Stocks/Companies.CSV", 0);
        companies.remove(company);
        CSVModifier.clearCSVFile("src/Stocks/Companies.CSV");
        for( String companyInList : companies){
            List<String[]> companyToAdd = new ArrayList<>();
            companyToAdd.add(new String[]{companyInList});
            CSVModifier.writeDataToCSVBelow("src/Stocks/Companies.CSV",companyToAdd);
        }
    }

    public void updateStockPrice(String companyName , double newPrice) throws IOException {
        List<String> listToGetId = CSVModifier.readCSVColumn("src/Stocks/Session.CSV", 0);
        int row = listToGetId.indexOf(companyName);
        if (row != -1) {
            if (CSVModifier.readCSVRow("src/Stocks/Session.CSV", row).size() <= 1) {
                List<String> addRow = new ArrayList<>();
                addRow.add(companyName);
                addRow.add(newPrice + "");
                addRow.add(newPrice + "");
                addRow.add(newPrice + "");
                CSVModifier.editRow("src/Stocks/Session.CSV", row, addRow);
            } else {
                List<String> rowToModify = CSVModifier.readCSVRow("src/Stocks/Session.CSV", row);
                double highPrice = Double.parseDouble(rowToModify.get(1));
                double lowPrice = Double.parseDouble(rowToModify.get(2));
                int volume = Integer.parseInt(rowToModify.get(4));
                if(newPrice>highPrice){
                    highPrice = newPrice;
                } else if (newPrice<lowPrice) {
                    lowPrice = newPrice;
                }
                rowToModify.clear();
                rowToModify.add(companyName);
                rowToModify.add("" + highPrice);
                rowToModify.add("" + lowPrice);
                rowToModify.add("" + newPrice);
                rowToModify.add("" + volume);
                CSVModifier.editRow("src/Stocks/Session.CSV", row, rowToModify);
            }
        }
    }
    public void updateStockVolume(String companyName , int newVolume) throws IOException {
        List<String> listToGetId = CSVModifier.readCSVColumn("src/Stocks/Session.CSV", 0);
        int row = listToGetId.indexOf(companyName);
        if (row != -1) {
                List<String> rowToModify = CSVModifier.readCSVRow("src/Stocks/Session.CSV", row);
                double highPrice = Double.parseDouble(rowToModify.get(1));
                double lowPrice = Double.parseDouble(rowToModify.get(2));
                double currentPrice = Double.parseDouble(rowToModify.get(3));
                rowToModify.clear();
                rowToModify.add(companyName);
                rowToModify.add("" + highPrice);
                rowToModify.add("" + lowPrice);
                rowToModify.add("" + currentPrice);
                rowToModify.add("" + newVolume);
                CSVModifier.editRow("src/Stocks/Session.CSV", row, rowToModify);
        }
    }
}

