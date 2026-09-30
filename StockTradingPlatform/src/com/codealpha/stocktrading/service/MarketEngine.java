package com.codealpha.stocktrading.service;

import com.codealpha.stocktrading.model.Stock;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class MarketEngine {
    private final Map<String, Stock> stockUniverse = new ConcurrentHashMap<>();
    private final List<Consumer<Map<String, Stock>>> listeners = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService scheduler;
    private final Random random = new Random();
    private boolean isRunning = false;

    public MarketEngine() {
        initializeStocks();
    }

    private void initializeStocks() {
        addStock(new Stock("AAPL", "Apple Inc.", 185.50, 0.015));
        addStock(new Stock("MSFT", "Microsoft Corporation", 425.20, 0.012));
        addStock(new Stock("GOOGL", "Alphabet Inc.", 175.80, 0.016));
        addStock(new Stock("AMZN", "Amazon.com Inc.", 182.40, 0.018));
        addStock(new Stock("TSLA", "Tesla Inc.", 245.30, 0.035));
        addStock(new Stock("NVDA", "NVIDIA Corporation", 128.90, 0.028));
        addStock(new Stock("META", "Meta Platforms Inc.", 512.60, 0.022));
    }

    public void addStock(Stock stock) {
        stockUniverse.put(stock.getSymbol(), stock);
    }

    public Stock getStock(String symbol) {
        return stockUniverse.get(symbol.toUpperCase());
    }

    public Map<String, Stock> getAllStocks() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(stockUniverse));
    }

    public synchronized void tick() {
        for (Stock stock : stockUniverse.values()) {
            double oldPrice = stock.getCurrentPrice();
            double vol = stock.getVolatility();
            double shock = (random.nextGaussian() * vol * oldPrice) + (0.0002 * oldPrice);
            double newPrice = Math.max(1.0, oldPrice + shock);
            long tradeVol = 1000 + random.nextInt(25000);
            stock.updatePrice(newPrice, tradeVol);
        }
        notifyListeners();
    }

    public synchronized void startLiveFeed(long intervalMillis) {
        if (isRunning) return;
        isRunning = true;
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "MarketFeedThread");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::tick, 1000, intervalMillis, TimeUnit.MILLISECONDS);
    }

    public synchronized void stopLiveFeed() {
        if (!isRunning) return;
        isRunning = false;
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    public boolean isRunning() { return isRunning; }

    public void addMarketListener(Consumer<Map<String, Stock>> listener) {
        listeners.add(listener);
    }

    public void removeMarketListener(Consumer<Map<String, Stock>> listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        Map<String, Stock> snapshot = getAllStocks();
        for (Consumer<Map<String, Stock>> l : listeners) {
            try {
                l.accept(snapshot);
            } catch (Exception ignored) {}
        }
    }
}
