/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本系统已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2020 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package com.huiyan.huozhiwang.controller;

import com.huiyan.huozhiwang.common.Constants;
import com.huiyan.huozhiwang.common.CategoryLevelEnum;
import com.huiyan.huozhiwang.common.ServiceResultEnum;
import com.huiyan.huozhiwang.entity.GoodsCategory;
import com.huiyan.huozhiwang.entity.HuoZhiMallGoods;
import com.huiyan.huozhiwang.service.CategoryService;
import com.huiyan.huozhiwang.service.GoodsService;
import com.huiyan.huozhiwang.util.PageQueryUtil;
import com.huiyan.huozhiwang.util.Result;
import com.huiyan.huozhiwang.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 慧燕

 * @link https://github.com/huozhiwang
 */
@Controller
@RequestMapping("/admin")
public class GoodsController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private CategoryService categoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "mall_goods");
        return "admin/mall_goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/mall_goods_edit";
            }
        }
        return "error/error_5xx";
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        HuoZhiMallGoods huoZhiMallGoods = goodsService.getMallGoodsById(goodsId);
        if (huoZhiMallGoods == null) {
            return "error/error_400";
        }
        if (huoZhiMallGoods.getGoodsCategoryId() > 0) {
            if (huoZhiMallGoods.getGoodsCategoryId() != null || huoZhiMallGoods.getGoodsCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = categoryService.getGoodsCategoryById(huoZhiMallGoods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == CategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = categoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firestCategory = categoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firestCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (huoZhiMallGoods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), CategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = categoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), CategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", huoZhiMallGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/mall_goods_edit";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(goodsService.getMallGoodsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody HuoZhiMallGoods huoZhiMallGoods) {
        if (StringUtils.isEmpty(huoZhiMallGoods.getGoodsName())
                || StringUtils.isEmpty(huoZhiMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(huoZhiMallGoods.getTag())
                || Objects.isNull(huoZhiMallGoods.getOriginalPrice())
                || Objects.isNull(huoZhiMallGoods.getGoodsCategoryId())
                || Objects.isNull(huoZhiMallGoods.getSellingPrice())
                || Objects.isNull(huoZhiMallGoods.getStockNum())
                || Objects.isNull(huoZhiMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(huoZhiMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(huoZhiMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = goodsService.saveMallGoods(huoZhiMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody HuoZhiMallGoods huoZhiMallGoods) {
        if (Objects.isNull(huoZhiMallGoods.getGoodsId())
                || StringUtils.isEmpty(huoZhiMallGoods.getGoodsName())
                || StringUtils.isEmpty(huoZhiMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(huoZhiMallGoods.getTag())
                || Objects.isNull(huoZhiMallGoods.getOriginalPrice())
                || Objects.isNull(huoZhiMallGoods.getSellingPrice())
                || Objects.isNull(huoZhiMallGoods.getGoodsCategoryId())
                || Objects.isNull(huoZhiMallGoods.getStockNum())
                || Objects.isNull(huoZhiMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(huoZhiMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(huoZhiMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = goodsService.updateMallGoods(huoZhiMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        HuoZhiMallGoods goods = goodsService.getMallGoodsById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (goodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

}