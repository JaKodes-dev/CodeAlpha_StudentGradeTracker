# CodeAlpha Java Programming Internship Tasks

This repository contains my completed projects for the CodeAlpha Java Programming Internship.

## Included Projects

### 1. Student Grade Tracker
A Java desktop (Swing) and CLI application for managing student academic records, calculating averages, GPA (4.0 scale), letter grades, and viewing class statistics with a grade distribution chart.

- **Location**: `StudentGradeTracker/`
- **Features**: Grade input, GPA calculation, class analytics (average, highest, lowest, pass rate), CSV export/import, visual grade chart.

### 2. Stock Trading Platform
A stock market simulation platform with live price updates, BUY/SELL order execution, portfolio valuation, unrealized/realized P&L tracking, and transaction history.

- **Location**: `StockTradingPlatform/`
- **Features**: Market price fluctuation simulation, order execution with validation, portfolio tracking, transaction logs, CSV export.

## How to Build and Run

### Student Grade Tracker
```bash
cd StudentGradeTracker
javac -d bin -sourcepath src src/com/codealpha/gradetracker/Main.java
java -cp bin com.codealpha.gradetracker.Main --gui
# or for CLI:
java -cp bin com.codealpha.gradetracker.Main --cli
```
On Windows, you can also run `run_gui.bat` or `run_cli.bat` inside the `StudentGradeTracker` folder.

### Stock Trading Platform
```bash
cd StockTradingPlatform
javac -d bin -sourcepath src src/com/codealpha/stocktrading/Main.java
java -cp bin com.codealpha.stocktrading.Main --gui
# or for CLI:
java -cp bin com.codealpha.stocktrading.Main --cli
```
On Windows, you can also run `run_gui.bat` or `run_cli.bat` inside the `StockTradingPlatform` folder.

## Author
Joseph Appiah Karikari
