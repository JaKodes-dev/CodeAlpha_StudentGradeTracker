package com.codealpha.stocktrading.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String userId;
    private String username;
    private final Portfolio portfolio;

    public User(String userId, String username, double initialCash) {
        this.userId = userId;
        this.username = username;
        this.portfolio = new Portfolio(initialCash);
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Portfolio getPortfolio() { return portfolio; }
}
