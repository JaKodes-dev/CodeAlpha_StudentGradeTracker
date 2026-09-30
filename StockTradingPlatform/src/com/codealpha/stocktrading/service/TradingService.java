package com.codealpha.stocktrading.service;

import com.codealpha.stocktrading.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TradingService {
    private final MarketEngine marketEngine;
    private final User currentUser;
    private int transactionCounter = 1001;

    public static class TradeResult {
        private final boolean success;
        private final String message;
        private final Transaction transaction;

        public TradeResult(boolean success, String message, Transaction transaction) {
            this.success = success;
            this.message = message;
            this.transaction = transaction;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Transaction getTransaction() { return transaction; }
    }

    public TradingService(MarketEngine marketEngine) {
        this.marketEngine = marketEngine != null ? marketEngine : new MarketEngine();
        this.currentUser = new User("USR-001", "Alex Trader", 25000.00);
        seedInitialPortfolio();
    }

    private void seedInitialPortfolio() {
        executeSeedBuy("AAPL", 20, 180.00);
        executeSeedBuy("MSFT", 10, 415.50);
        executeSeedBuy("NVDA", 35, 120.25);
    }

    private void executeSeedBuy(String symbol, int qty, double price) {
        Stock s = marketEngine.getStock(symbol);
        if (s != null) {
            double cost = qty * price;
            currentUser.getPortfolio().setCashBalance(currentUser.getPortfolio().getCashBalance() - cost);
            Holding h = new Holding(symbol, s.getCompanyName(), qty, price);
            currentUser.getPortfolio().addHolding(h);
            Transaction tx = new Transaction("TX-" + (transactionCounter++), Transaction.Type.BUY, symbol, qty, price, cost);
            currentUser.getPortfolio().addTransaction(tx);
        }
    }

    public MarketEngine getMarketEngine() { return marketEngine; }
    public User getCurrentUser() { return currentUser; }
    public Portfolio getPortfolio() { return currentUser.getPortfolio(); }

    public synchronized TradeResult buyStock(String symbol, int quantity) {
        if (quantity <= 0) {
            return new TradeResult(false, "Order quantity must be greater than zero.", null);
        }

        Stock stock = marketEngine.getStock(symbol);
        if (stock == null) {
            return new TradeResult(false, "Stock symbol '" + symbol + "' not found.", null);
        }

        double price = stock.getCurrentPrice();
        double totalCost = Math.round((quantity * price) * 100.0) / 100.0;
        Portfolio portfolio = currentUser.getPortfolio();

        if (portfolio.getCashBalance() < totalCost) {
            return new TradeResult(false,
                    String.format("Insufficient funds. Required: $%.2f, Available: $%.2f", totalCost, portfolio.getCashBalance()),
                    null);
        }

        portfolio.setCashBalance(portfolio.getCashBalance() - totalCost);

        Holding holding = portfolio.getHolding(symbol);
        if (holding == null) {
            holding = new Holding(symbol, stock.getCompanyName(), quantity, price);
            portfolio.addHolding(holding);
        } else {
            holding.addShares(quantity, price);
        }

        Transaction tx = new Transaction("TX-" + (transactionCounter++), Transaction.Type.BUY, symbol, quantity, price, totalCost);
        portfolio.addTransaction(tx);

        return new TradeResult(true,
                String.format("Successfully bought %d shares of %s at $%.2f (Total: $%.2f)", quantity, symbol, price, totalCost),
                tx);
    }

    public synchronized TradeResult sellStock(String symbol, int quantity) {
        if (quantity <= 0) {
            return new TradeResult(false, "Order quantity must be greater than zero.", null);
        }

        Stock stock = marketEngine.getStock(symbol);
        if (stock == null) {
            return new TradeResult(false, "Stock symbol '" + symbol + "' not found.", null);
        }

        Portfolio portfolio = currentUser.getPortfolio();
        Holding holding = portfolio.getHolding(symbol);

        if (holding == null || holding.getQuantity() < quantity) {
            int available = holding != null ? holding.getQuantity() : 0;
            return new TradeResult(false,
                    String.format("Insufficient shares. Requested: %d, Available: %d", quantity, available),
                    null);
        }

        double price = stock.getCurrentPrice();
        double totalProceeds = Math.round((quantity * price) * 100.0) / 100.0;
        double costBasis = Math.round((quantity * holding.getAverageCost()) * 100.0) / 100.0;
        double realizedPL = totalProceeds - costBasis;

        holding.removeShares(quantity);
        if (holding.getQuantity() == 0) {
            portfolio.removeHolding(symbol);
        }

        portfolio.setCashBalance(portfolio.getCashBalance() + totalProceeds);
        portfolio.addRealizedProfit(realizedPL);

        Transaction tx = new Transaction("TX-" + (transactionCounter++), Transaction.Type.SELL, symbol, quantity, price, totalProceeds);
        portfolio.addTransaction(tx);

        return new TradeResult(true,
                String.format("Successfully sold %d shares of %s at $%.2f (Proceeds: $%.2f, Realized P&L: %s$%.2f)",
                        quantity, symbol, price, totalProceeds, realizedPL >= 0 ? "+" : "", realizedPL),
                tx);
    }

    public synchronized TradeResult depositCash(double amount) {
        if (amount <= 0.0) {
            return new TradeResult(false, "Deposit amount must be positive.", null);
        }
        Portfolio portfolio = currentUser.getPortfolio();
        portfolio.setCashBalance(portfolio.getCashBalance() + amount);
        Transaction tx = new Transaction("TX-" + (transactionCounter++), Transaction.Type.DEPOSIT, "USD", 0, 1.0, amount);
        portfolio.addTransaction(tx);
        return new TradeResult(true, String.format("Deposited $%.2f successfully.", amount), tx);
    }

    public synchronized TradeResult withdrawCash(double amount) {
        if (amount <= 0.0) {
            return new TradeResult(false, "Withdrawal amount must be positive.", null);
        }
        Portfolio portfolio = currentUser.getPortfolio();
        if (portfolio.getCashBalance() < amount) {
            return new TradeResult(false, String.format("Insufficient cash balance. Available: $%.2f", portfolio.getCashBalance()), null);
        }
        portfolio.setCashBalance(portfolio.getCashBalance() - amount);
        Transaction tx = new Transaction("TX-" + (transactionCounter++), Transaction.Type.WITHDRAW, "USD", 0, 1.0, amount);
        portfolio.addTransaction(tx);
        return new TradeResult(true, String.format("Withdrew $%.2f successfully.", amount), tx);
    }

        public synchronized void exportPortfolioCSV(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write("Symbol,Company,Shares,AvgCostBasis,TotalCost,MarketPrice,MarketValue,UnrealizedPL,ReturnPct");
            writer.newLine();
            Map<String, Stock> market = marketEngine.getAllStocks();
            for (Holding h : currentUser.getPortfolio().getHoldings().values()) {
                Stock s = market.get(h.getSymbol());
                double price = s != null ? s.getCurrentPrice() : h.getAverageCost();
                String line = h.getSymbol() + ",\"" + h.getCompanyName() + "\"," +
                        h.getQuantity() + "," +
                        String.format("%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%%",
                                h.getAverageCost(), h.getTotalCostBasis(), price,
                                h.getCurrentMarketValue(price),
                                h.getUnrealizedProfitLoss(price),
                                h.getUnrealizedProfitLossPercent(price));
                writer.write(line);
                writer.newLine();
            }
        }
    }
}