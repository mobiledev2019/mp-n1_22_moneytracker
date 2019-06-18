package com.example.t2m.moneytracker.dataaccess;

import com.example.t2m.moneytracker.model.Budget;
import com.example.t2m.moneytracker.model.Category;
import com.example.t2m.moneytracker.model.DateRange;

import java.util.List;

public interface IBudgetDAO {
    public boolean insertBudget(Budget budget);
    public boolean updateBudget(Budget budget);
    public boolean deleteBudget(Budget budget);
    public List<Budget> getAllBudget();
    public List<Budget> getAllBudget(long walletId);
    public List<Budget> getBudgetByPeriod(long walletId,DateRange dateRange);
    public List<Budget> getBudgetByCategory(long walletId,int categoryId);
    public void updateBudgetSpent(Budget budget);
}
