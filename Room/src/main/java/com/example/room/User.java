package com.example.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import io.reactivex.annotations.NonNull;
import java.util.Date;

import static android.arch.persistence.room.ForeignKey.CASCADE;

// 当一个类用@Entity注解并且被@Database注解中的entities属性所引用，Room就会在数据库中为那个entity创建一张表。
// Room默认把类名作为数据库的表名。如果你想用其它的名称，使用@Entity注解的tableName属性
// 注：SQLite中的表名是大小写敏感的。

/*
 * 注意: 这里在测试的时候如果@Entity注解,加上了 (tableName = "t_user") 那么版本升级的时候 如果添加int 类型的值那么需要这样写
 * database.execSQL("ALTER TABLE `t_user` ADD COLUMN 'age'      INTEGER NOT NULL default 0")
 * NOT NULL default 0 不能省略,省略会报错
 */
@Entity(tableName = "t_user")

// 如果你的entity有一个组合主键，你可以使用@Entity注解的primaryKeys属性
// @Entity(primaryKeys = {"firstName", "lastName"})

// 为了提高查询的效率，你可能想为特定的字段建立索引。要为一个entity添加索引，在@Entity注解中添加indices属性，列出你想放在索引或者组合索引中的字段。下面的代码片段演示了这个注解的过程：
// @Entity(indices = {@Index("name"), @Index("last_name", "address")})

// 有时候，某个字段或者几个字段必须是唯一的。你可以通过把@Index注解的unique属性设置为true来实现唯一性。下面的代码防止了一个表中的两行数据出现firstName和lastName字段的值相同的情况：
// @Entity(indices = {@Index(value = {"first_name", "last_name"}, unique = true)})

// 假设有另外一个entity叫做Book，可以使用@ForeignKey注解定义它和User entity之间的关联，如下
// onDelete = CASCADE， 你可以告诉SQLite，如果相应的User实例被删除，那么删除这个User下的所有book。
// @Entity(foreignKeys = @ForeignKey(onDelete = CASCADE, entity = User.class, parentColumns = "id", childColumns = "user_id"))
// class Book {
//   @PrimaryKey
//   public int bookId;
//
//   public String title;
//
//   @ColumnInfo(name = "user_id")
//   public int userId;
// }
public class User {
  // 每个entity必须至少定义一个field作为主键（primary key）。
  // 即使只有一个field，你也必须用@PrimaryKey注释这个field。
  // 如果你想让Room为entity设置自增ID，你可以设置@PrimaryKey的autoGenerate属性。
  // 如果你的entity有一个组合主键，你可以使用@Entity注解的primaryKeys属性
  @PrimaryKey(autoGenerate = true) private int uid;

  // 和tableName属性类似，Room默认把field名称作为数据库表的column名。如果你想让column有不一样的名称，为field添加@ColumnInfo属性
  @ColumnInfo(name = "first_name") // name指定的是表的字段名
  private String firstName;

  @ColumnInfo(name = "last_name") private String lastName;

  // 可以不指定ColumnInfo，那么表的字段名和属性名一致
  private int age;

  private Date birthday;

  // 默认Room会为entity中定义的每一个field都创建一个column。
  // 如果一个entity中有你不想持久化的field，那么你可以使用@Ignore来注释它们
  @Ignore private String fullName;

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public String getFullName() {
    return firstName + lastName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public int getUid() {
    return uid;
  }

  public void setUid(int uid) {
    this.uid = uid;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}