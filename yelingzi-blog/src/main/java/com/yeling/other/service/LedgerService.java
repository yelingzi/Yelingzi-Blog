package com.yeling.other.service;


import com.yeling.other.domian.entity.Expense;
import com.yeling.other.domian.entity.OrderLedger;
import com.yeling.other.domian.entity.PropertyLiabilities;

import java.util.List;

public interface LedgerService {

    /**
     * 查询资产负债表-资产
     * @return
     */

    List<PropertyLiabilities> propertylist();

    /**
     * 查询资产负债表-负债
     * @return
     */

    List<PropertyLiabilities> liabilitieslist();

    /**
     * 查询资产负债表-收入
     * @return
     */

    List<PropertyLiabilities> incomelist();
    List<Expense> getExpenseList(String classify);
    /**
     * 查询资产负债表-支出
     * @return
     */

    List<PropertyLiabilities> expenditurelist();

    List<OrderLedger> getIncomeList(Integer num);

    Integer getIncomeCount();

    List<OrderLedger> getLedgerList(Integer num, Integer uid);

    Integer getLedgerCount(Integer uid);

    List<OrderLedger> getSearchLedger(String search,Integer uid);

}
