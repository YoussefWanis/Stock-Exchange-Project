package group.starRock;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class User extends Person {
    private float moneyBalance;


    public User(float moneyBalance) {
    this.moneyBalance =moneyBalance;
    }
    public float getMoneyBalance() {return moneyBalance;}

    @Override
    public String getUsername() {
        return this.userName;
    }

    public void buy(Stock stock, int quantity)
    {
        float totalCost = (float) (stock.getPrice() * quantity);
        if (this.moneyBalance >= totalCost) {
            this.moneyBalance -= totalCost;
            stock.setQuantity(stock.getQuantity() - quantity);
            updateVolume(stock.getName(), -quantity);
            updateTransactionHistory(stock, quantity);
        } else {
            System.out.println("Insufficient balance to buy the stocks.");
        }
    }

        public void sell(String companyName, int quantity) {
            try {
                List<String> userData = CSVModifier.readCSV("src/UserInfo/"+MenuController.username+".csv");
                boolean hasStocks = false;
                for (String line : userData) {
                    String[] values = line.split(",");
                    if (values[1].equals(companyName) && values[2].equals("Buy")) {
                        hasStocks = true;
                        break;
                    }
                }

                if (hasStocks)
                {
                    float stockPrice = getStockPrice(companyName);
                    float totalValue = stockPrice * quantity;

                    this.moneyBalance += totalValue;

                    updateTransactionHistorySell(companyName, quantity, stockPrice, totalValue);
                    updateVolume(companyName,quantity);
                    System.out.println("Stocks sold successfully.");
                } else {
                    System.out.println("You don't own enough stocks of " + companyName + " to sell.");
                }
            } catch (IOException e) {
                System.out.println("Error reading CSV file.");
            }
        }

        private float getStockPrice(String companyName) {
            if (companyName.equals("Google")) {
                return 168.100006f;
            } else if (companyName.equals("Tesla")) {
                return 184.759995f;
            }
            return 0;
        }

        private void updateTransactionHistorySell(String companyName, int quantity, float stockPrice, float totalValue) {
            String fileName = this.userName + ".csv";
            String filePath = "src/UserInfo/" + fileName;

            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);

            String[] rowData = {
                    formattedDateTime,
                    companyName,
                    "Sell",
                    String.valueOf(stockPrice),
                    String.valueOf(quantity),
                    String.valueOf(totalValue),
                    String.valueOf(this.moneyBalance)
            };

            List<String[]> data = new ArrayList<>();
            data.add(rowData);

            CSVModifier.writeDataToCSVBelow(filePath, data);
        }
    private static void updateVolume(String companyName, int change) {
        String fileName = "src/Stocks/Session.CSV";

        List<String> companies = CSVModifier.readCSVColumn(fileName, 0);

        int companyIndex = companies.indexOf(companyName);
        if (companyIndex != -1) {
            List<String> newRowData = new ArrayList<>();
            try {
                List<String> rowData = CSVModifier.readCSVRow(fileName, companyIndex);
                if (!rowData.isEmpty()) {
                    String[] row = rowData.toArray(new String[0]);
                    int currentVolume = Integer.parseInt(row[4]);
                    int newVolume = currentVolume + change;
                    row[4] = String.valueOf(newVolume);
                    Collections.addAll(newRowData, row);
                }

                CSVModifier.editRow(fileName, companyIndex, newRowData);
            } catch (IOException _) {

            }
        } else {
            System.out.println("Company not found in CSV file.");
        }
    }

    private void updateTransactionHistory(Stock stock, int quantity) {
        String fileName = this.userName + ".csv";
        String filePath = "src/UserInfo/" + fileName;

        File file = new File(filePath);
        boolean fileExists = file.exists();


        if (!fileExists) {
            CSVModifier.createBlankCSV(filePath);
        }

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        @SuppressWarnings({"EqualsWithItself", "UnreachableCode"}) float totalValue = (float) (stock.getPrice() * quantity * ("Buy".equals("Buy") ? -1 : 1));

        float remainingBalance = this.moneyBalance + totalValue;

        String[] rowData = {
                formattedDateTime,
                stock.getName(),
                "Buy",
                String.valueOf(stock.getPrice()),
                String.valueOf(quantity),
                String.valueOf(totalValue),
                String.valueOf(remainingBalance)
        };

        List<String[]> data = new ArrayList<>();
        data.add(rowData);

        if (!fileExists) {
            String[] header = {"Date and Time", "Stock Name", "Transaction Type", "Price per Stock", "Volume", "Total Transaction Value"};
            data.addFirst(header);
        }

        CSVModifier.writeDataToCSVBelow(filePath, data);
    }

    public void deposit(float amount) {
        this.moneyBalance += amount;
        updateTransactionHistoryDepositWithdraw("Deposit", amount);
    }

    public void withdraw(float amount) {
        if (this.moneyBalance >= amount) {
            this.moneyBalance -= amount;
            updateTransactionHistoryDepositWithdraw("Withdraw", amount);
        } else {
            System.out.println("Insufficient balance to withdraw.");
        }
    }

    private void updateTransactionHistoryDepositWithdraw(String transactionType, float amount) {
        String fileName = this.userName + ".csv";
        String filePath = "src/UserInfo/" + fileName;

        File file = new File(filePath);

        if (!file.exists()) {
            CSVModifier.createBlankCSV(filePath);
        }

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = dateTime.format(formatter);

        String[] rowData = {
                formattedDateTime,
                "Deposit/Withdraw",
                transactionType,
                "",
                "",
                String.valueOf(amount),
                String.valueOf(this.moneyBalance)
        };

        List<String[]> data = new ArrayList<>();
        data.add(rowData);

        if (!file.exists()) {
            String[] header = {"Date and Time", "Stock Name", "Transaction Type", "Price per Stock", "Volume", "Total Transaction Value", "Remaining Balance"};
            data.addFirst(header);
        }
        CSVModifier.writeDataToCSVBelow(filePath,data);
    }
}