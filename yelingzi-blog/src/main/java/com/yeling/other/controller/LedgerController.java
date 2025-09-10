package com.yeling.other.controller;


import com.yeling.user.domian.entity.User;
import com.yeling.entity.UserContext;
import com.yeling.other.domian.entity.Expense;
import com.yeling.other.domian.entity.OrderLedger;
import com.yeling.other.domian.entity.PropertyLiabilities;
import com.yeling.other.service.LedgerService;
import com.yeling.entity.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class LedgerController {

    @Autowired
    private LedgerService ledgerService;


    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/zcfzb/{num}")
    public ApiResponse propertyLiabilitiesList(@PathVariable Integer num){

        if(num == 1 ) {
            List<PropertyLiabilities> propertyLiabilitiesList = ledgerService.propertylist();
            return ApiResponse.success(propertyLiabilitiesList);
        } else if(num == 2)
        {
            List<PropertyLiabilities> propertyLiabilitiesList = ledgerService.liabilitieslist();
            return ApiResponse.success(propertyLiabilitiesList);
        } else if (num == 3) {
            List<PropertyLiabilities> propertyLiabilitiesList = ledgerService.incomelist();
            return ApiResponse.success(propertyLiabilitiesList);
        }else if (num == 4) {
            List<PropertyLiabilities> propertyLiabilitiesList = ledgerService.expenditurelist();
            return ApiResponse.success(propertyLiabilitiesList);
        }
        return ApiResponse.error("获取资产负债表失败");

    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/ledger/incomes/{num}")
    public ApiResponse getIncomeList(@PathVariable Integer num){

        List<OrderLedger> orderData = ledgerService.getIncomeList(num);

        return ApiResponse.success(orderData);

    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/ledger/incomes/count")
    public ApiResponse getIncomeCount(){

        Integer integer = ledgerService.getIncomeCount();

        return ApiResponse.success(integer);

    }

    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/api/ledger/expense/{classify}")
    public ApiResponse getExpenseList(@PathVariable String classify){

        List<Expense> expenses = ledgerService.getExpenseList(classify);

        return ApiResponse.success(expenses);

    }

    @GetMapping("/api/user/ledger/count")
    public ApiResponse getLedgerCount(){

        User user = UserContext.getUser();

        Integer integer = ledgerService.getLedgerCount(user.getId());

        return ApiResponse.success(integer);

    }

    @GetMapping("/api/user/ledger/{num}")
    public ApiResponse getLedgerList(@PathVariable Integer num, @RequestHeader("Authorization") String jwtToken){

        log.info("获取用户订单");

        User user = UserContext.getUser();

        List<OrderLedger> orderData = ledgerService.getLedgerList(num, user.getId());
        return ApiResponse.success(orderData);

    }

    @GetMapping("/api/user/ledger/search/{search}")
    public ApiResponse getSearchLedger(@PathVariable String search, @RequestHeader("Authorization") String jwtToken){

        User user = UserContext.getUser();

        List<OrderLedger> o = ledgerService.getSearchLedger(search, user.getId());

        return ApiResponse.success(o);

    }

}
