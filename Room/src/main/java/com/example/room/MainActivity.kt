package com.example.room

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.Date

/**
 * 具体例子网站 https://blog.csdn.net/hubinqiang/article/details/73012353
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    // 可以使用下面的代码来得到database的实例
    // 注意: 在实例化AppDatabase对象的时候应该遵循单例模式，因为每个Database实例都是相当耗费的，而且也很少需要多个实例。
    val db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "database-name")
      // 数据库版本从 1 升级到 2 User表添加字段 age ,birthday
      // Room让你可以让你写Migration类来保存用户数据。每个Migration类指定from和to版本。
      // 运行时Room运行每个Migration类的 migrate() 方法，使用正确的顺序把数据库迁移到新版本。
      .addMigrations(object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
          // SQLite的alter不支持同时添加多列
          /*
           * 注意: 这里在测试的时候如果@Entity注解,加上了 (tableName = "t_user") 那么版本升级的时候 如果添加int 类型的值那么需要这样写
           * database.execSQL("ALTER TABLE `t_user` ADD COLUMN 'age'      INTEGER NOT NULL default 0")
           * NOT NULL default 0 不能省略,省略会报错
           */
          database.execSQL("ALTER TABLE `t_user` ADD COLUMN 'age'      INTEGER NOT NULL default 0")
          //database.execSQL("ALTER TABLE `t_user` ADD COLUMN 'birthday' INTEGER")
        }
      }, object : Migration(2, 3) { // 数据库版本从 2 升级到 E User表添加字段 birthday
        override fun migrate(database: SupportSQLiteDatabase) {
          // SQLite的alter不支持同时添加多列
          //database.execSQL("ALTER TABLE `t_user` ADD COLUMN 'age'      INTEGER NOT NULL default 0")
          database.execSQL("ALTER TABLE `t_user` ADD COLUMN 'birthday' INTEGER")
        }
      })
      .build()

    findViewById<TextView>(R.id.text_view).setOnClickListener {

      Single.fromCallable<List<User>> {
        val user = User().apply {
          age = 12
          birthday = Date()
          firstName = "liub"
          lastName = "bin"
        }
        db.userDao()
          .let {
            it.insertAll(user)
            it.all
          }
      }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
                     Log.e("tag", it?.size.toString())
                     it?.last()
                       ?.let {
                         Toast.makeText(this, it.firstName + it.uid + it.birthday, Toast.LENGTH_SHORT)
                           .show()
                       }
                   }, {
                     Log.e("tag", "tag", it)
                     Toast.makeText(this, it.message, Toast.LENGTH_SHORT)
                       .show()
                   })

      //
      /*db.userDao()
          .loadUserById(1)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe {

          }*/
    }
  }
}
