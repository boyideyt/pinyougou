package com.pinyougou.sellergoods.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.sellergoods.service.ItemCatService;

import entity.PageResult;

/**
 * 服务实现层
 * 
 * @author Administrator
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat) {
		itemCatMapper.updateByPrimaryKey(itemCat);
	}

	/**
	 * 根据ID获取实体
	 * 
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id) {
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for (Long id : ids) {
			itemCatMapper.deleteByPrimaryKey(id);
		}
	}

	/**
	 * 模板CDUR均会执行的方法
	 */
	@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();

		if (itemCat != null) {
			if (itemCat.getParentId() != null && itemCat.getParentId() >= 0) {
				criteria.andParentIdEqualTo(itemCat.getParentId());
			}

		}
		Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(example);
		
		// 每次执行查询的时候，一次性读取缓存进行存储 (因为每次增删改都要执行此方法)
		List<TbItemCat> itemCatList = findAll();
		RedisSaveRunner redisSaveRunner = new RedisSaveRunner(itemCatList, null, redisTemplate);
		new Thread(redisSaveRunner).start();
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbItemCat> findByParentId(Long id) {
		TbItemCatExample example = new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(id);
		return itemCatMapper.selectByExample(example);
	}

}
