package com.example.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

// 数据库注解，必须。entities指定实体类，version指定数据库版本
// 当一个类用@Entity注解并且被@Database注解中的entities属性所引用，Room就会在数据库中为那个entity创建一张表。
@Database(entities = { User.class }, version = 3)
// 将@TypeConverters注释添加到AppDatabase类，以便Room可以使用你为该AppDatabase中的每个实体和DAO定义的转换器：
@TypeConverters({ Converters.class })
public abstract class AppDatabase extends RoomDatabase {
  public abstract UserDao userDao();
}