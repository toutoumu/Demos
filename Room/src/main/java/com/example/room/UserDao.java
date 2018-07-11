package com.example.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import io.reactivex.Flowable;
import java.util.Date;
import java.util.List;

@Dao
public interface UserDao {
  @Query("SELECT * FROM t_user")
  List<User> getAll();

  // 传入参数集合
  @Query("SELECT * FROM t_user WHERE uid IN (:userIds)")
  List<User> loadAllByIds(int[] userIds);

  // 向query传递参数
  @Query("SELECT * FROM t_user WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1")
  User findByName(String first, String last);

  // 如果@Insert方法只接收一个参数，它可以返回一个long，代表新插入元素的rowId，如果参数是一个数组或者集合，那么应该返回long[]或者List。
  @Insert
  void insertAll(User... users);

  // 方法返回一个int类型的值，表示更新影响的行数
  @Update
  public void updateUsers(User... users);

  @Delete
  int delete(User user);

  // 返回部分字段
  @Query("SELECT first_name, last_name FROM t_user")
  public List<NameTuple> loadFullName();

  // Room还可以让你定义的查询返回RxJava2的Publisher和Flowable对象。
  // 要使用这个功能，在Gradle dependencies中添加android.arch.persistence.room:rxjava2。
  // 然后你就可以返回RxJava2中定义的对象类型了，如下面的代码所示：
  @Query("SELECT * from t_user where uid = :id LIMIT 1")
  public Flowable<User> loadUserById(int id);

  // 这里使用到了类型转换器
  // @Query("SELECT * FROM user WHERE birthday BETWEEN :from AND :to")
  // List<User> findUsersBornBetweenDates(Date from, Date to);

  public class NameTuple {
    @ColumnInfo(name = "first_name") public String firstName;

    @ColumnInfo(name = "last_name") public String lastName;
  }
}