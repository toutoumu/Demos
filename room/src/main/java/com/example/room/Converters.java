package com.example.room;

import android.arch.persistence.room.TypeConverter;
import java.util.Date;

/**
 * Room内置了原始类型。但是，有时你会希望使用自定义数据类型。 要为自定义类型添加这种支持，可以提供一个TypeConverter，
 * 它将一个自定义类转换为Room保留的已知类型。
 *
 * 比如，如果我们要保留Date的实例，我们可以编写以下TypeConverter来存储数据库中的等效的Unix时间戳记：
 */
public class Converters {
  @TypeConverter
  public static Date fromTimestamp(Long value) {
    return value == null ? null : new Date(value);
  }

  @TypeConverter
  public static Long dateToTimestamp(Date date) {
    return date == null ? null : date.getTime();
  }
}
