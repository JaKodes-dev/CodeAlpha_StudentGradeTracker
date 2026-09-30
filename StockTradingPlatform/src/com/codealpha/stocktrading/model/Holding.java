package com.codealpha.stocktrading.model;

import java.io.Serializable;

public class Holding implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String symbol;
    private final String companyName;
    private int quantity;
    private double totalCostBasis; // Total dollars paid for current active shares

    public Holding(String symbol, String companyName, int quantity, double pricePerShare) {
        this.symbol = symbol.toUpperCase().trim();
        this.companyName = companyName.trim();
        this.quantity = quantity;
        this.totalCostBasis = quantity * pricePerShare;
    }

    public synchronized void addShares(int additionalQuantity, double pricePerShare) {
        this.quantity += additionalQuantity;
        this.totalCostBasis += (additionalQuantity * pricePerShare);
    }

    public synchronized void removeShares(int sellQuantity) {
        if (sellQuantity > this.quantity) {
            throw new IllegalArgumentException("Cannot sell more shares than owned.");
        }
        double avgCost = getAverageCost();
        this.quantity -= sellQuantity;
        this.totalCostBasis = this.quantity * avgCost;
    }

    public String getSymbol() { return symbol; }
    public String getCompanyName() { return companyName; }
    public synchronized int getQuantity() { return quantity; }
    public synchronized double getTotalCostBasis() { return Math.round(totalCostBasis * 100.0) / 100.0; }

    public synchronized double getAverageCost() {
        if (quantity == 0) return 0.0;
        return Math.round((totalCostBasis / quantity) * 100.0) / 100.0;
    }

    public synchronized double getCurrentMarketValue(double currentStockPrice) {
        return Math.round((quantity * currentStockPrice) * 100.0) / 100.0;
    }

    public synchronized double getUnrealizedProfitLoss(double currentStockPrice) {
        return Math.round((getCurrentMarketValue(currentStockPrice) - totalCostBasis) * 100.0) / 100.0;
    }

    public synchronized double getUnrealizedProfitLossPercent(double currentStockPrice) {
        if (totalCostBasis == 0.0) return 0.0;
        return Math.round(((getCurrentMarketValue(currentStockPrice) - totalCostBasis) / totalCostBasis * 100.0) * 100.0) / 100.0;
    }
}
