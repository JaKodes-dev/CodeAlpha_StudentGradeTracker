package com.codealpha.stocktrading.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { BUY, SELL, DEPOSIT, WITHDRAW }

    private final String id;
    private final String timestamp;
    private final Type type;
    private final String symbol;
    private final int quantity;
    private final double pricePerShare;
    private final double totalAmount;

    public Transaction(String id, Type type, String symbol, int quantity, double pricePerShare, double totalAmount) {
        this.id = id;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.type = type;
        this.symbol = symbol != null ? symbol.toUpperCase() : "CASH";
        this.quantity = quantity;
        this.pricePerShare = Math.round(pricePerShare * 100.0) / 100.0;
        this.totalAmount = Math.round(totalAmount * 100.0) / 100.0;
    }

    public String getId() { return id; }
    public String getTimestamp() { return timestamp; }
    public Type getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPricePerShare() { return pricePerShare; }
    public double getTotalAmount() { return totalAmount; }

    @Override
    public String toString() {
        if (type == Type.DEPOSIT || type == Type.WITHDRAW) {
            return String.format("[%s] %-8s | %s $%.2f", timestamp, type, symbol, totalAmount);
        }
        return String.format("[%s] %-4s %4d shares of %-5s @ $%.2f (Total: $%.2f)",
                timestamp, type, quantity, symbol, pricePerShare, totalAmount);
    }
}
