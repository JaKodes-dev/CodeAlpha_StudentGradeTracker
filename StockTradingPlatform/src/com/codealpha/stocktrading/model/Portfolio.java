package com.codealpha.stocktrading.model;

import java.io.Serializable;
import java.util.*;

public class Portfolio implements Serializable {
    private static final long serialVersionUID = 1L;

    private double cashBalance;
    private double initialCash;
    private double totalRealizedProfitLoss;
    private final Map<String, Holding> holdings = new LinkedHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();

    public Portfolio(double initialCash) {
        this.initialCash = initialCash;
        this.cashBalance = initialCash;
        this.totalRealizedProfitLoss = 0.0;
    }

    public synchronized double getCashBalance() { return Math.round(cashBalance * 100.0) / 100.0; }
    public synchronized double getInitialCash() { return initialCash; }
    public synchronized double getTotalRealizedProfitLoss() { return Math.round(totalRealizedProfitLoss * 100.0) / 100.0; }

    public synchronized void setCashBalance(double cash) { this.cashBalance = cash; }

    public synchronized Map<String, Holding> getHoldings() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(holdings));
    }

    public synchronized Holding getHolding(String symbol) {
        return holdings.get(symbol.toUpperCase());
    }

    public synchronized List<Transaction> getTransactions() {
        return Collections.unmodifiableList(new ArrayList<>(transactions));
    }

    public synchronized void addHolding(Holding holding) {
        holdings.put(holding.getSymbol(), holding);
    }

    public synchronized void removeHolding(String symbol) {
        holdings.remove(symbol.toUpperCase());
    }

    public synchronized void addTransaction(Transaction tx) {
        transactions.add(0, tx); // Recent first
    }

    public synchronized void addRealizedProfit(double profit) {
        this.totalRealizedProfitLoss += profit;
    }

    public synchronized double getTotalHoldingsMarketValue(Map<String, Stock> marketStocks) {
        double sum = 0.0;
        for (Holding h : holdings.values()) {
            Stock s = marketStocks.get(h.getSymbol());
            double price = s != null ? s.getCurrentPrice() : h.getAverageCost();
            sum += h.getCurrentMarketValue(price);
        }
        return Math.round(sum * 100.0) / 100.0;
    }

    public synchronized double getTotalPortfolioValue(Map<String, Stock> marketStocks) {
        return Math.round((cashBalance + getTotalHoldingsMarketValue(marketStocks)) * 100.0) / 100.0;
    }

    public synchronized double getTotalUnrealizedProfitLoss(Map<String, Stock> marketStocks) {
        double sum = 0.0;
        for (Holding h : holdings.values()) {
            Stock s = marketStocks.get(h.getSymbol());
            double price = s != null ? s.getCurrentPrice() : h.getAverageCost();
            sum += h.getUnrealizedProfitLoss(price);
        }
        return Math.round(sum * 100.0) / 100.0;
    }

    public synchronized double getTotalReturnPercent(Map<String, Stock> marketStocks) {
        if (initialCash == 0.0) return 0.0;
        double netValue = getTotalPortfolioValue(marketStocks);
        return Math.round(((netValue - initialCash) / initialCash * 100.0) * 100.0) / 100.0;
    }
}
