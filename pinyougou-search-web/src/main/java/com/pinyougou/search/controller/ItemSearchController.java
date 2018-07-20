package com.pinyougou.search.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
	
	@Reference(timeout=5000)
	private ItemSearchService itemSearchService;
	
	/**
	 * 
	 * @param searchMap
	 * @return
	 */
	@RequestMapping("/search")
	public Map<String,Object> search(@RequestBody Map searchMap){
		return itemSearchService.search(searchMap);
	}
}