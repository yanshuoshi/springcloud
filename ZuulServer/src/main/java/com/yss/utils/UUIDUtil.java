package com.yss.utils;

import java.util.UUID;

/**
 * @author shuoshi.yan
 * @className:
 * @description:UUID生产工具
 * @date 2020/08/03
 **/

public class UUIDUtil {

  public static String creatUUID() {
    String uuid = UUID.randomUUID().toString();
    //去掉“-”符号
    return uuid.replaceAll("-", "");
  }

  /**
   * 获得指定数目的UUID
   *
   * @param number int 需要获得的UUID数量
   * @return String[] UUID数组
   */
  public static String[] getUUID(int number) {
    if (number < 1) {
      return null;
    }
    String[] retArray = new String[number];
    for (int i = 0; i < number; i++) {
      retArray[i] = creatUUID();
    }
    return retArray;
  }
}
