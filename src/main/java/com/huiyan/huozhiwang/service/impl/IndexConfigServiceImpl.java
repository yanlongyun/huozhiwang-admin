/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.huiyan.huozhiwang.service.impl;

import com.huiyan.huozhiwang.common.ServiceResultEnum;
import com.huiyan.huozhiwang.controller.vo.IndexConfigGoodsVO;
import com.huiyan.huozhiwang.dao.IndexConfigMapper;
import com.huiyan.huozhiwang.dao.GoodsMapper;
import com.huiyan.huozhiwang.entity.IndexConfig;
import com.huiyan.huozhiwang.entity.HuoZhiMallGoods;
import com.huiyan.huozhiwang.service.IndexConfigService;
import com.huiyan.huozhiwang.util.BeanUtil;
import com.huiyan.huozhiwang.util.PageQueryUtil;
import com.huiyan.huozhiwang.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IndexConfigServiceImpl implements IndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }

    @Override
    public List<IndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<IndexConfigGoodsVO> indexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<HuoZhiMallGoods> huoZhiMallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
            indexConfigGoodsVOS = BeanUtil.copyList(huoZhiMallGoods, IndexConfigGoodsVO.class);
            for (IndexConfigGoodsVO indexConfigGoodsVO : indexConfigGoodsVOS) {
                String goodsName = indexConfigGoodsVO.getGoodsName();
                String goodsIntro = indexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    indexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    indexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return indexConfigGoodsVOS;
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}
