package com.yeling.other.mapper;

import com.yeling.other.domian.entity.OrderLedger;
import com.yeling.other.domian.entity.PropertyLiabilities;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LedgerMapper {

    /**
     * 根据查询资产负债表
     *
     */
    @Select("select * from property")
    List<PropertyLiabilities> propertylist();
    @Select("select * from liabilities")
    List<PropertyLiabilities> liabilitieslist();
    @Select("select * from income")
    List<PropertyLiabilities> incomelist();
    @Select("select * from expenditure")
    List<PropertyLiabilities> expenditurelist();

    @Select("select * from incomes ORDER BY order_time DESC LIMIT #{num}, 10")
    List<OrderLedger> getIncomeList(Integer num);

    @Select("select COUNT(*) from incomes")
    Integer getIncomeCount();

    @Select("select * from incomes WHERE user_id = #{uid} ORDER BY order_time DESC LIMIT #{num}, 10")
    List<OrderLedger> getLedgerList(Integer num, Integer uid);

    @Select("select COUNT(*) from incomes WHERE user_id = #{id}")
    Integer getLedgerCount(Integer id);

    @Select("select * from incomes WHERE user_id =#{uid} and order_num = #{search} ")
    List<OrderLedger> getSearchLedger(String search, Integer uid);

}
