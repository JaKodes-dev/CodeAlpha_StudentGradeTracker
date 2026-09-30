package com.codealpha.stocktrading;

import com.codealpha.stocktrading.cli.StockTradingCLI;
import com.codealpha.stocktrading.service.MarketEngine;
import com.codealpha.stocktrading.service.TradingService;
import com.codealpha.stocktrading.ui.StockTradingGUI;

import javax.swing.*;
import java.awt.GraphicsEnvironment;

public class Main {
    public static void main(String[] args) {
        MarketEngine marketEngine = new MarketEngine();
        TradingService tradingService = new TradingService(marketEngine);

        boolean forceCli = false;
        boolean forceGui = false;

        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg)) forceCli = true;
            if ("--gui".equalsIgnoreCase(arg)) forceGui = true;
        }

        boolean isHeadless = GraphicsEnvironment.isHeadless();

        if (forceCli || isHeadless) {
            System.out.println("Starting Stock Trading Platform in Console CLI mode...");
            new StockTradingCLI(tradingService).start();
        } else {
            System.out.println("Launching Stock Trading Platform Desktop GUI...");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> {
                StockTradingGUI gui = new StockTradingGUI(tradingService);
                gui.setVisible(true);
            });
        }
    }
}
