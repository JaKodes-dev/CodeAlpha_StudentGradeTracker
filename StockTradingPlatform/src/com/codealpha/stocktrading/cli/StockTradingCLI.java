package com.codealpha.stocktrading.cli;

import com.codealpha.stocktrading.model.*;
import com.codealpha.stocktrading.service.MarketEngine;
import com.codealpha.stocktrading.service.TradingService;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

public class StockTradingCLI {
    private final TradingService tradingService;
    private final MarketEngine marketEngine;
    private final Scanner scanner;

    public StockTradingCLI(TradingService tradingService) {
        this.tradingService = tradingService != null ? tradingService : new TradingService(null);
        this.marketEngine = this.tradingService.getMarketEngine();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option (0-8): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> viewMarketQuotes();
                case "2" -> viewPortfolio();
                case "3" -> executeBuy();
                case "4" -> executeSell();
                case "5" -> viewTransactions();
                case "6" -> stepMarketTick();
                case "7" -> manageFunds();
                case "8" -> exportPortfolio();
                case "0" -> {
                    System.out.println("\nThank you for trading with CodeAlpha Stock Trading Platform. Goodbye!");
                    marketEngine.stopLiveFeed();
                    running = false;
                }
                default -> System.out.println("Error: Invalid option. Please enter a number between 0 and 8.");
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void printBanner() {
        System.out.println("===============================================================================");
        System.out.println("            Bot CODEALPHA - STOCK TRADING PLATFORM (TASK 2)                     ");
        System.out.println("        Real-Time Market Simulation, Order Execution & Portfolio Desk         ");
        System.out.println("===============================================================================");
    }

    private void printMenu() {
        Portfolio p = tradingService.getPortfolio();
        Map<String, Stock> market = marketEngine.getAllStocks();
        System.out.println("\n---------------------------- PORTFOLIO SUMMARY -----------------------------");
        System.out.printf(" Cash Balance: $%-12.2f | Portfolio Value: $%-12.2f | Unrealized P&L: %s$%.2f (%s%.2f%%)\n",
                p.getCashBalance(),
                p.getTotalPortfolioValue(market),
                p.getTotalUnrealizedProfitLoss(market) >= 0 ? "+" : "",
                p.getTotalUnrealizedProfitLoss(market),
                p.getTotalReturnPercent(market) >= 0 ? "+" : "",
                p.getTotalReturnPercent(market));
        System.out.println("--------------------------------- MAIN MENU ---------------------------------");
        System.out.println(" [1] Bot View Live Market Quotes");
        System.out.println(" [2] Bot View My Holdings & Performance");
        System.out.println(" [3] Bot Execute BUY Order");
        System.out.println(" [4] Bot Execute SELL Order");
        System.out.println(" [5] Bot View Transaction History");
        System.out.println(" [6] ? Step Market Simulation (Advance Prices)");
        System.out.println(" [7] Bot Deposit / Withdraw Cash");
        System.out.println(" [8] Bot Export Portfolio to CSV");
        System.out.println(" [0] Bot Exit Trading Desk");
        System.out.println("-----------------------------------------------------------------------------");
    }

    private void viewMarketQuotes() {
        Map<String, Stock> stocks = marketEngine.getAllStocks();
        System.out.println("\n" + "=".repeat(86));
        System.out.printf("| %-6s | %-26s | %-10s | %-10s | %-9s | %-10s |\n",
                "TICKER", "COMPANY NAME", "PRICE", "CHANGE ($)", "CHANGE (%)", "VOLUME");
        System.out.println("=".repeat(86));

        for (Stock s : stocks.values()) {
            System.out.printf("| %-6s | %-26s | $%-9.2f | %-+10.2f | %-+9.2f%% | %-10d |\n",
                    s.getSymbol(),
                    truncate(s.getCompanyName(), 26),
                    s.getCurrentPrice(),
                    s.getPriceChange(),
                    s.getPriceChangePercent(),
                    s.getVolume());
        }
        System.out.println("=".repeat(86));
    }

    private void viewPortfolio() {
        Portfolio p = tradingService.getPortfolio();
        Map<String, Stock> market = marketEngine.getAllStocks();
        Map<String, Holding> holdings = p.getHoldings();

        if (holdings.isEmpty()) {
            System.out.println("Bot You currently hold no stock positions. Use [3] to buy stocks.");
            return;
        }

        System.out.println("\n" + "=".repeat(92));
        System.out.printf("| %-6s | %-6s | %-10s | %-10s | %-12s | %-12s | %-10s |\n",
                "SYMBOL", "SHARES", "AVG COST", "PRICE", "MARKET VALUE", "UNREALIZED PL", "RETURN (%)");
        System.out.println("=".repeat(92));

        for (Holding h : holdings.values()) {
            Stock s = market.get(h.getSymbol());
            double price = s != null ? s.getCurrentPrice() : h.getAverageCost();
            double upl = h.getUnrealizedProfitLoss(price);
            double uplPct = h.getUnrealizedProfitLossPercent(price);

            System.out.printf("| %-6s | %-6d | $%-9.2f | $%-9.2f | $%-11.2f | %-+12.2f | %-+9.2f%% |\n",
                    h.getSymbol(),
                    h.getQuantity(),
                    h.getAverageCost(),
                    price,
                    h.getCurrentMarketValue(price),
                    upl,
                    uplPct);
        }
        System.out.println("=".repeat(92));
        System.out.printf("Total Realized Profit & Loss: %s$%.2f\n",
                p.getTotalRealizedProfitLoss() >= 0 ? "+" : "", p.getTotalRealizedProfitLoss());
    }

    private void executeBuy() {
        System.out.println("\n--- Bot Execute BUY Order ---");
        System.out.print("Enter Stock Symbol (e.g. AAPL, NVDA, TSLA): ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Stock stock = marketEngine.getStock(symbol);

        if (stock == null) {
            System.out.println("Error: ? Stock ticker '" + symbol + "' not found.");
            return;
        }

        System.out.printf("Current Market Price for %s: $%.2f\n", symbol, stock.getCurrentPrice());
        System.out.printf("Available Cash Balance: $%.2f\n", tradingService.getPortfolio().getCashBalance());

        System.out.print("Enter Quantity of Shares to BUY: ");
        try {
            int qty = Integer.parseInt(scanner.nextLine().trim());
            TradingService.TradeResult res = tradingService.buyStock(symbol, qty);
            if (res.isSuccess()) {
                System.out.println("Error: SUCCESS: " + res.getMessage());
            } else {
                System.out.println("Error: FAILED: " + res.getMessage());
            }
        } catch (NumberFormatException ex) {
            System.out.println("Error: ? Invalid integer quantity.");
        }
    }

    private void executeSell() {
        System.out.println("\n--- Bot Execute SELL Order ---");
        System.out.print("Enter Stock Symbol to SELL: ");
        String symbol = scanner.nextLine().trim().toUpperCase();
        Holding holding = tradingService.getPortfolio().getHolding(symbol);

        if (holding == null || holding.getQuantity() == 0) {
            System.out.println("Error: You do not own any shares of " + symbol + ".");
            return;
        }

        Stock stock = marketEngine.getStock(symbol);
        double price = stock != null ? stock.getCurrentPrice() : holding.getAverageCost();

        System.out.printf("Owned Shares: %d | Avg Cost: $%.2f | Current Price: $%.2f\n",
                holding.getQuantity(), holding.getAverageCost(), price);

        System.out.print("Enter Quantity of Shares to SELL: ");
        try {
            int qty = Integer.parseInt(scanner.nextLine().trim());
            TradingService.TradeResult res = tradingService.sellStock(symbol, qty);
            if (res.isSuccess()) {
                System.out.println("Error: SUCCESS: " + res.getMessage());
            } else {
                System.out.println("Error: FAILED: " + res.getMessage());
            }
        } catch (NumberFormatException ex) {
            System.out.println("Error: ? Invalid integer quantity.");
        }
    }

    private void viewTransactions() {
        var txList = tradingService.getPortfolio().getTransactions();
        if (txList.isEmpty()) {
            System.out.println("Bot No transactions recorded yet.");
            return;
        }

        System.out.println("\n" + "=".repeat(78));
        System.out.printf("| %-19s | %-8s | %-6s | %-6s | %-10s | %-12s |\n",
                "TIMESTAMP", "TYPE", "SYMBOL", "SHARES", "PRICE", "TOTAL VALUE");
        System.out.println("=".repeat(78));

        for (Transaction tx : txList) {
            System.out.printf("| %-19s | %-8s | %-6s | %-6s | %-10s | $%-11.2f |\n",
                    tx.getTimestamp(),
                    tx.getType().name(),
                    tx.getSymbol(),
                    tx.getQuantity() > 0 ? String.valueOf(tx.getQuantity()) : "-",
                    tx.getPricePerShare() > 0 ? String.format("$%.2f", tx.getPricePerShare()) : "-",
                    tx.getTotalAmount());
        }
        System.out.println("=".repeat(78));
    }

    private void stepMarketTick() {
        marketEngine.tick();
        System.out.println("Error: Market price simulation advanced! Prices updated based on market volatility.");
        viewMarketQuotes();
    }

    private void manageFunds() {
        System.out.println("\n--- Bot Cash Management ---");
        System.out.println(" [1] Deposit Cash");
        System.out.println(" [2] Withdraw Cash");
        System.out.print("Select action (1-2): ");
        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            System.out.print("Enter deposit amount ($): ");
            try {
                double amt = Double.parseDouble(scanner.nextLine().trim());
                var res = tradingService.depositCash(amt);
                System.out.println(res.isSuccess() ? "Error: " + res.getMessage() : "Error: " + res.getMessage());
            } catch (Exception ex) {
                System.out.println("Error: Invalid amount.");
            }
        } else if ("2".equals(choice)) {
            System.out.print("Enter withdrawal amount ($): ");
            try {
                double amt = Double.parseDouble(scanner.nextLine().trim());
                var res = tradingService.withdrawCash(amt);
                System.out.println(res.isSuccess() ? "Error: " + res.getMessage() : "Error: " + res.getMessage());
            } catch (Exception ex) {
                System.out.println("Error: Invalid amount.");
            }
        }
    }

    private void exportPortfolio() {
        System.out.print("\nEnter filename to export (default: portfolio_export.csv): ");
        String fname = scanner.nextLine().trim();
        if (fname.isEmpty()) fname = "portfolio_export.csv";
        try {
            File f = new File(fname);
            tradingService.exportPortfolioCSV(f);
            System.out.println("Error: Portfolio exported successfully to: " + f.getAbsolutePath());
        } catch (Exception ex) {
            System.out.println("Error: Error exporting CSV: " + ex.getMessage());
        }
    }

    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen - 2) + ".." : str;
    }
}
