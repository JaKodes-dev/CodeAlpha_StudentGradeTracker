package com.codealpha.stocktrading.ui;

import com.codealpha.stocktrading.model.*;
import com.codealpha.stocktrading.service.MarketEngine;
import com.codealpha.stocktrading.service.TradingService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class StockTradingGUI extends JFrame {
    private final TradingService tradingService;
    private final MarketEngine marketEngine;

    private JLabel totalValueLabel;
    private JLabel cashBalanceLabel;
    private JLabel unrealizedPLLabel;
    private JLabel realizedPLLabel;
    private JLabel totalReturnLabel;

    private DefaultTableModel marketTableModel;
    private JTable marketTable;
    private DefaultTableModel holdingsTableModel;
    private JTable holdingsTable;
    private DefaultTableModel txTableModel;
    private JTable txTable;

    private JComboBox<String> tickerCombo;
    private JRadioButton buyRadio;
    private JRadioButton sellRadio;
    private JSpinner quantitySpinner;
    private JLabel pricePerShareLabel;
    private JLabel orderTotalLabel;
    private JLabel availableCashOrSharesLabel;
    private JToggleButton liveFeedToggle;

    public StockTradingGUI(TradingService tradingService) {
        this.tradingService = tradingService != null ? tradingService : new TradingService(null);
        this.marketEngine = this.tradingService.getMarketEngine();

        initUI();
        marketEngine.addMarketListener(this::onMarketTick);
        refreshUI();
    }

    private void initUI() {
        setTitle("CodeAlpha - Stock Trading Platform (Task 2)");
        setSize(1180, 760);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(245, 247, 250));
        root.setBorder(new EmptyBorder(12, 14, 12, 14));
        setContentPane(root);

        root.add(createHeaderPanel(), BorderLayout.NORTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createMarketWatchPanel(), createRightTradingPanel());
        mainSplit.setResizeWeight(0.48);
        mainSplit.setDividerSize(6);
        mainSplit.setBorder(null);
        root.add(mainSplit, BorderLayout.CENTER);

        root.add(createBottomToolbar(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        JLabel title = new JLabel("Bot Real-Time Stock Trading Platform");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(30, 41, 59));

        JLabel subtitle = new JLabel("CodeAlpha Java Programming Internship Portfolio Task 2 ? Market Simulation, Portfolio & Order Execution");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(100, 116, 139));
        titleBox.add(title);
        titleBox.add(subtitle);
        panel.add(titleBox, BorderLayout.NORTH);

        JPanel statRow = new JPanel(new GridLayout(1, 5, 10, 0));
        statRow.setOpaque(false);
        statRow.setBorder(new EmptyBorder(6, 0, 4, 0));

        totalValueLabel = new JLabel("$0.00", SwingConstants.CENTER);
        cashBalanceLabel = new JLabel("$0.00", SwingConstants.CENTER);
        unrealizedPLLabel = new JLabel("$0.00", SwingConstants.CENTER);
        realizedPLLabel = new JLabel("$0.00", SwingConstants.CENTER);
        totalReturnLabel = new JLabel("0.00%", SwingConstants.CENTER);

        statRow.add(createCard("Total Portfolio Value", totalValueLabel, new Color(37, 99, 235)));
        statRow.add(createCard("Cash Balance", cashBalanceLabel, new Color(13, 148, 136)));
        statRow.add(createCard("Unrealized P&L", unrealizedPLLabel, new Color(22, 163, 74)));
        statRow.add(createCard("Realized P&L", realizedPLLabel, new Color(124, 58, 237)));
        statRow.add(createCard("Total Return", totalReturnLabel, new Color(234, 88, 12)));

        panel.add(statRow, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 8, 10, 8)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        valueLabel.setForeground(accent);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMarketWatchPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JPanel topBar = new JPanel(new BorderLayout(4, 0));
        topBar.setOpaque(false);
        JLabel title = new JLabel("Bot Live Market Watch");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(30, 41, 59));
        topBar.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        controls.setOpaque(false);

        JButton tickBtn = new JButton("Error: Step Tick");
        tickBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tickBtn.addActionListener(e -> marketEngine.tick());

        liveFeedToggle = new JToggleButton("Bot Auto-Feed: OFF");
        liveFeedToggle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        liveFeedToggle.addActionListener(e -> {
            if (liveFeedToggle.isSelected()) {
                liveFeedToggle.setText("Bot Auto-Feed: ON");
                marketEngine.startLiveFeed(1200);
            } else {
                liveFeedToggle.setText("Bot Auto-Feed: OFF");
                marketEngine.stopLiveFeed();
            }
        });

        controls.add(tickBtn);
        controls.add(liveFeedToggle);
        topBar.add(controls, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Symbol", "Company", "Price", "Change ($)", "Change (%)", "Day High", "Day Low"};
        marketTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        marketTable = new JTable(marketTableModel);
        marketTable.setRowHeight(26);
        marketTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        marketTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        marketTable.getTableHeader().setBackground(new Color(241, 245, 249));
        marketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        marketTable.getColumnModel().getColumn(0).setCellRenderer(center);
        marketTable.getColumnModel().getColumn(2).setCellRenderer(center);
        marketTable.getColumnModel().getColumn(3).setCellRenderer(new PriceChangeRenderer());
        marketTable.getColumnModel().getColumn(4).setCellRenderer(new PriceChangeRenderer());
        marketTable.getColumnModel().getColumn(5).setCellRenderer(center);
        marketTable.getColumnModel().getColumn(6).setCellRenderer(center);

        marketTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = marketTable.getSelectedRow();
                if (row != -1) {
                    String sym = (String) marketTableModel.getValueAt(row, 0);
                    tickerCombo.setSelectedItem(sym);
                    updateOrderCalculations();
                }
            }
        });

        panel.add(new JScrollPane(marketTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRightTradingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        panel.add(createOrderTicketPanel(), BorderLayout.NORTH);
        panel.add(createPortfolioTabsPanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createOrderTicketPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel title = new JLabel("Bot Order Execution Ticket");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(30, 41, 59));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 4, 8, 8));
        form.setOpaque(false);

        buyRadio = new JRadioButton("BUY", true);
        sellRadio = new JRadioButton("SELL");
        ButtonGroup bg = new ButtonGroup();
        bg.add(buyRadio);
        bg.add(sellRadio);
        JPanel radioBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        radioBox.setOpaque(false);
        radioBox.add(buyRadio);
        radioBox.add(sellRadio);

        tickerCombo = new JComboBox<>(marketEngine.getAllStocks().keySet().toArray(new String[0]));
        tickerCombo.setFont(new Font("Segoe UI", Font.BOLD, 12));

        quantitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 10000, 1));
        quantitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        pricePerShareLabel = new JLabel("$0.00");
        pricePerShareLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        orderTotalLabel = new JLabel("$0.00");
        orderTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderTotalLabel.setForeground(new Color(37, 99, 235));

        availableCashOrSharesLabel = new JLabel("Avail Cash: $0.00");
        availableCashOrSharesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        availableCashOrSharesLabel.setForeground(new Color(100, 116, 139));

        form.add(new JLabel("Order Side:"));
        form.add(radioBox);
        form.add(new JLabel("Stock Symbol:"));
        form.add(tickerCombo);

        form.add(new JLabel("Order Quantity:"));
        form.add(quantitySpinner);
        form.add(new JLabel("Market Price:"));
        form.add(pricePerShareLabel);

        form.add(new JLabel("Estimated Total:"));
        form.add(orderTotalLabel);
        form.add(new JLabel("Available:"));
        form.add(availableCashOrSharesLabel);

        panel.add(form, BorderLayout.CENTER);

        JButton executeBtn = new JButton("Bot Execute Order");
        executeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        executeBtn.setBackground(new Color(37, 99, 235));
        executeBtn.setForeground(Color.WHITE);
        executeBtn.setFocusPainted(false);
        executeBtn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        executeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        executeBtn.addActionListener(e -> executeTrade());

        tickerCombo.addActionListener(e -> updateOrderCalculations());
        buyRadio.addActionListener(e -> updateOrderCalculations());
        sellRadio.addActionListener(e -> updateOrderCalculations());
        quantitySpinner.addChangeListener(e -> updateOrderCalculations());

        JPanel execBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        execBox.setOpaque(false);
        execBox.add(executeBtn);
        panel.add(execBox, BorderLayout.SOUTH);

        return panel;
    }

    private JTabbedPane createPortfolioTabsPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 12));

        String[] hCols = {"Symbol", "Company", "Shares", "Avg Cost", "Price", "Market Value", "Unrealized P&L", "Return (%)"};
        holdingsTableModel = new DefaultTableModel(hCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        holdingsTable = new JTable(holdingsTableModel);
        holdingsTable.setRowHeight(24);
        holdingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        holdingsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        holdingsTable.getTableHeader().setBackground(new Color(241, 245, 249));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        holdingsTable.getColumnModel().getColumn(0).setCellRenderer(center);
        holdingsTable.getColumnModel().getColumn(2).setCellRenderer(center);
        holdingsTable.getColumnModel().getColumn(3).setCellRenderer(center);
        holdingsTable.getColumnModel().getColumn(4).setCellRenderer(center);
        holdingsTable.getColumnModel().getColumn(5).setCellRenderer(center);
        holdingsTable.getColumnModel().getColumn(6).setCellRenderer(new PriceChangeRenderer());
        holdingsTable.getColumnModel().getColumn(7).setCellRenderer(new PriceChangeRenderer());

        tabs.addTab("Bot Active Holdings", new JScrollPane(holdingsTable));

        String[] txCols = {"Timestamp", "Type", "Symbol", "Shares", "Execution Price", "Total Amount"};
        txTableModel = new DefaultTableModel(txCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        txTable = new JTable(txTableModel);
        txTable.setRowHeight(24);
        txTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        txTable.getTableHeader().setBackground(new Color(241, 245, 249));

        txTable.getColumnModel().getColumn(0).setCellRenderer(center);
        txTable.getColumnModel().getColumn(1).setCellRenderer(new TransactionTypeRenderer());
        txTable.getColumnModel().getColumn(2).setCellRenderer(center);
        txTable.getColumnModel().getColumn(3).setCellRenderer(center);
        txTable.getColumnModel().getColumn(4).setCellRenderer(center);
        txTable.getColumnModel().getColumn(5).setCellRenderer(center);

        tabs.addTab("Bot Order History", new JScrollPane(txTable));

        return tabs;
    }

    private JPanel createBottomToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setOpaque(false);

        JButton depositBtn = new JButton("Bot Deposit Cash");
        depositBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        depositBtn.setBackground(new Color(13, 148, 136));
        depositBtn.setForeground(Color.WHITE);
        depositBtn.addActionListener(e -> depositCash());

        JButton withdrawBtn = new JButton("Bot Withdraw Cash");
        withdrawBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        withdrawBtn.setBackground(new Color(100, 116, 139));
        withdrawBtn.setForeground(Color.WHITE);
        withdrawBtn.addActionListener(e -> withdrawCash());

        JButton exportBtn = new JButton("Bot Export Portfolio CSV");
        exportBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportBtn.setBackground(new Color(37, 99, 235));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.addActionListener(e -> exportPortfolioCSV());

        bar.add(depositBtn);
        bar.add(withdrawBtn);
        bar.add(exportBtn);

        return bar;
    }

    private void onMarketTick(Map<String, Stock> market) {
        SwingUtilities.invokeLater(() -> {
            refreshMarketTable(market);
            refreshPortfolioStats(market);
            updateOrderCalculations();
        });
    }

    private void refreshUI() {
        Map<String, Stock> market = marketEngine.getAllStocks();
        refreshMarketTable(market);
        refreshPortfolioStats(market);
        refreshHoldingsTable(market);
        refreshTransactionsTable();
        updateOrderCalculations();
    }

    private void refreshMarketTable(Map<String, Stock> market) {
        int selectedRow = marketTable.getSelectedRow();
        marketTableModel.setRowCount(0);
        for (Stock s : market.values()) {
            marketTableModel.addRow(new Object[]{
                    s.getSymbol(),
                    s.getCompanyName(),
                    String.format("$%.2f", s.getCurrentPrice()),
                    String.format("%+.2f", s.getPriceChange()),
                    String.format("%+.2f%%", s.getPriceChangePercent()),
                    String.format("$%.2f", s.getDayHigh()),
                    String.format("$%.2f", s.getDayLow())
            });
        }
        if (selectedRow != -1 && selectedRow < marketTable.getRowCount()) {
            marketTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void refreshPortfolioStats(Map<String, Stock> market) {
        Portfolio p = tradingService.getPortfolio();
        totalValueLabel.setText(String.format("$%.2f", p.getTotalPortfolioValue(market)));
        cashBalanceLabel.setText(String.format("$%.2f", p.getCashBalance()));

        double upl = p.getTotalUnrealizedProfitLoss(market);
        unrealizedPLLabel.setText(String.format("%s$%.2f", upl >= 0 ? "+" : "", upl));
        unrealizedPLLabel.setForeground(upl >= 0 ? new Color(22, 163, 74) : new Color(220, 38, 38));

        double rpl = p.getTotalRealizedProfitLoss();
        realizedPLLabel.setText(String.format("%s$%.2f", rpl >= 0 ? "+" : "", rpl));
        realizedPLLabel.setForeground(rpl >= 0 ? new Color(22, 163, 74) : new Color(220, 38, 38));

        double ret = p.getTotalReturnPercent(market);
        totalReturnLabel.setText(String.format("%s%.2f%%", ret >= 0 ? "+" : "", ret));
        totalReturnLabel.setForeground(ret >= 0 ? new Color(22, 163, 74) : new Color(220, 38, 38));
    }

    private void refreshHoldingsTable(Map<String, Stock> market) {
        holdingsTableModel.setRowCount(0);
        Portfolio p = tradingService.getPortfolio();
        for (Holding h : p.getHoldings().values()) {
            Stock s = market.get(h.getSymbol());
            double price = s != null ? s.getCurrentPrice() : h.getAverageCost();
            double upl = h.getUnrealizedProfitLoss(price);
            double uplPct = h.getUnrealizedProfitLossPercent(price);

            holdingsTableModel.addRow(new Object[]{
                    h.getSymbol(),
                    h.getCompanyName(),
                    h.getQuantity(),
                    String.format("$%.2f", h.getAverageCost()),
                    String.format("$%.2f", price),
                    String.format("$%.2f", h.getCurrentMarketValue(price)),
                    String.format("%s$%.2f", upl >= 0 ? "+" : "", upl),
                    String.format("%s%.2f%%", uplPct >= 0 ? "+" : "", uplPct)
            });
        }
    }

    private void refreshTransactionsTable() {
        txTableModel.setRowCount(0);
        Portfolio p = tradingService.getPortfolio();
        for (Transaction tx : p.getTransactions()) {
            txTableModel.addRow(new Object[]{
                    tx.getTimestamp(),
                    tx.getType().name(),
                    tx.getSymbol(),
                    tx.getQuantity() > 0 ? String.valueOf(tx.getQuantity()) : "-",
                    tx.getPricePerShare() > 0 ? String.format("$%.2f", tx.getPricePerShare()) : "-",
                    String.format("$%.2f", tx.getTotalAmount())
            });
        }
    }

    private void updateOrderCalculations() {
        String sym = (String) tickerCombo.getSelectedItem();
        if (sym == null) return;
        Stock s = marketEngine.getStock(sym);
        if (s == null) return;

        int qty = (int) quantitySpinner.getValue();
        double price = s.getCurrentPrice();
        double total = qty * price;

        pricePerShareLabel.setText(String.format("$%.2f", price));
        orderTotalLabel.setText(String.format("$%.2f", total));

        Portfolio p = tradingService.getPortfolio();
        if (buyRadio.isSelected()) {
            availableCashOrSharesLabel.setText(String.format("Avail Cash: $%.2f", p.getCashBalance()));
        } else {
            Holding h = p.getHolding(sym);
            int shares = h != null ? h.getQuantity() : 0;
            availableCashOrSharesLabel.setText(String.format("Avail Shares: %d", shares));
        }
    }

    private void executeTrade() {
        String sym = (String) tickerCombo.getSelectedItem();
        int qty = (int) quantitySpinner.getValue();
        boolean isBuy = buyRadio.isSelected();

        TradingService.TradeResult result;
        if (isBuy) {
            result = tradingService.buyStock(sym, qty);
        } else {
            result = tradingService.sellStock(sym, qty);
        }

        if (result.isSuccess()) {
            refreshUI();
            JOptionPane.showMessageDialog(this, result.getMessage(), "Order Executed", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Order Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void depositCash() {
        String input = JOptionPane.showInputDialog(this, "Enter cash amount to deposit ($):", "Deposit Funds", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                double amt = Double.parseDouble(input.trim());
                TradingService.TradeResult res = tradingService.depositCash(amt);
                if (res.isSuccess()) {
                    refreshUI();
                    JOptionPane.showMessageDialog(this, res.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, res.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid dollar amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void withdrawCash() {
        String input = JOptionPane.showInputDialog(this, "Enter cash amount to withdraw ($):", "Withdraw Funds", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                double amt = Double.parseDouble(input.trim());
                TradingService.TradeResult res = tradingService.withdrawCash(amt);
                if (res.isSuccess()) {
                    refreshUI();
                    JOptionPane.showMessageDialog(this, res.getMessage(), "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, res.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid dollar amount.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportPortfolioCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("portfolio_export.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                tradingService.exportPortfolioCSV(chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Portfolio exported successfully to CSV!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class PriceChangeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            String val = value != null ? value.toString() : "";
            if (val.startsWith("+")) {
                setForeground(new Color(22, 163, 74));
            } else if (val.startsWith("-")) {
                setForeground(new Color(220, 38, 38));
            } else {
                setForeground(Color.DARK_GRAY);
            }
            return c;
        }
    }

    private static class TransactionTypeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            String val = value != null ? value.toString() : "";
            if ("BUY".equalsIgnoreCase(val) || "DEPOSIT".equalsIgnoreCase(val)) {
                setForeground(new Color(22, 163, 74));
            } else {
                setForeground(new Color(220, 38, 38));
            }
            return c;
        }
    }
}
