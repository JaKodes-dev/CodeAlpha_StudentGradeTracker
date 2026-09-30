package com.codealpha.stocktrading.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Stock implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String symbol;
    private final String companyName;
    private double currentPrice;
    private double previousClose;
    private double openPrice;
    private double dayHigh;
    private double dayLow;
    private long volume;
    private final double volatility; // Daily sigma (0.01 - 0.05)
    private final List<Double> priceHistory = new ArrayList<>();

    public Stock(String symbol, String companyName, double initialPrice, double volatility) {
        this.symbol = symbol.toUpperCase().trim();
        this.companyName = companyName.trim();
        this.currentPrice = Math.round(initialPrice * 100.0) / 100.0;
        this.previousClose = this.currentPrice;
        this.openPrice = this.currentPrice;
        this.dayHigh = this.currentPrice;
        this.dayLow = this.currentPrice;
        this.volume = 100000 + (long)(Math.random() * 500000);
        this.volatility = volatility;
        this.priceHistory.add(this.currentPrice);
    }

    public synchronized void updatePrice(double newPrice, long tradeVolume) {
        this.currentPrice = Math.max(0.50, Math.round(newPrice * 100.0) / 100.0);
        if (this.currentPrice > dayHigh) dayHigh = this.currentPrice;
        if (this.currentPrice < dayLow) dayLow = this.currentPrice;
        this.volume += tradeVolume;
        priceHistory.add(this.currentPrice);
        if (priceHistory.size() > 100) {
            priceHistory.remove(0);
        }
    }

    public String getSymbol() { return symbol; }
    public String getCompanyName() { return companyName; }
    public synchronized double getCurrentPrice() { return currentPrice; }
    public synchronized double getPreviousClose() { return previousClose; }
    public synchronized double getOpenPrice() { return openPrice; }
    public synchronized double getDayHigh() { return dayHigh; }
    public synchronized double getDayLow() { return dayLow; }
    public synchronized long getVolume() { return volume; }
    public double getVolatility() { return volatility; }
    public synchronized List<Double> getPriceHistory() { return Collections.unmodifiableList(new ArrayList<>(priceHistory)); }

    public synchronized double getPriceChange() {
        return Math.round((currentPrice - previousClose) * 100.0) / 100.0;
    }

    public synchronized double getPriceChangePercent() {
        if (previousClose == 0.0) return 0.0;
        return Math.round(((currentPrice - previousClose) / previousClose * 100.0) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return String.format("%s (%s): $%.2f (%+.2f / %+.2f%%)",
                symbol, companyName, currentPrice, getPriceChange(), getPriceChangePercent());
    }
}
