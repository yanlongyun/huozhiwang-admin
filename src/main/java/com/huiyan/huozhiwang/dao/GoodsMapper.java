/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.huiyan.huozhiwang.dao;

import com.huiyan.huozhiwang.entity.HuoZhiMallGoods;
import com.huiyan.huozhiwang.entity.StockNumDTO;
import com.huiyan.huozhiwang.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(HuoZhiMallGoods record);

    int insertSelective(HuoZhiMallGoods record);

    HuoZhiMallGoods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(HuoZhiMallGoods record);

    int updateByPrimaryKeyWithBLOBs(HuoZhiMallGoods record);

    int updateByPrimaryKey(HuoZhiMallGoods record);

    List<HuoZhiMallGoods> findMallGoodsList(PageQueryUtil pageUtil);

    int getTotalMallGoods(PageQueryUtil pageUtil);

    List<HuoZhiMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<HuoZhiMallGoods> findMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("mallGoodsList") List<HuoZhiMallGoods> huoZhiMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds") Long[] orderIds, @Param("sellStatus") int sellStatus);

}