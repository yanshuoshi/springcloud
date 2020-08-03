package com.yss.redis;

import com.alibaba.fastjson.JSONObject;
import com.yss.utils.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/test")
public class TestController {

  @Autowired
  private RedisService redisService;

  @GetMapping(value = "/redis")
  public void testRedis(){
//    redisService.set("1","this is first redis test");
//    String str = redisService.get("1");
//    System.out.println(str);
    List<Map<String,Object>> list = new ArrayList<>();
    Map<String,Object> map = new HashMap<>();
    map.put("111","111");
    map.put("222","222");
    list.add(map);
    redisService.setBean("list", list);

    List<Map<String,Object>> mapList = redisService.getBean("list");
    System.out.println(mapList.get(0));

  }
}
