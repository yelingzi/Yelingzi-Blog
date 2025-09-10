package com.yeling.other.service.Impl;

import com.yeling.other.domian.entity.Expense;
import com.yeling.other.domian.entity.OrderLedger;
import com.yeling.other.mapper.LedgerMapper;
import com.yeling.other.domian.entity.PropertyLiabilities;
import com.yeling.other.service.LedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LedgerServiceImpl implements LedgerService {

    @Autowired
    private LedgerMapper ledgerMapper;

    @Override
    public List<PropertyLiabilities> propertylist(){return ledgerMapper.propertylist();}

    @Override
    public List<PropertyLiabilities> liabilitieslist(){return ledgerMapper.liabilitieslist();}

    @Override
    public List<PropertyLiabilities> incomelist(){return ledgerMapper.incomelist();}

    @Override
    public List<PropertyLiabilities> expenditurelist(){return ledgerMapper.expenditurelist();}

    @Override
    public List<OrderLedger> getIncomeList(Integer num){
        Integer integer = ( num - 1 ) * 10;
        return ledgerMapper.getIncomeList(integer);
    }

    @Override
    public List<Expense> getExpenseList(String classify){

        List<Expense> expenses = null;

//        if(classify == "日"){
//            expenses = ledgerMapper.getExpense();
//        }else if(classify == "月"){
//
//        }else if(classify == "年"){
//
//        }

        return expenses;
    }

    @Override
    public Integer getIncomeCount(){
        return ledgerMapper.getIncomeCount();
    }


    @Override
    public List<OrderLedger> getLedgerList(Integer num, Integer uid){
        Integer integer = ( num - 1 ) * 10;
        return ledgerMapper.getLedgerList(integer, uid);
    }

    @Override
    public Integer getLedgerCount(Integer uid){

        return ledgerMapper.getLedgerCount(uid);
    }

    @Override
    public List<OrderLedger> getSearchLedger(String search, Integer uid){

        return ledgerMapper.getSearchLedger(search, uid);

    }


}
