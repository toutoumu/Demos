package liubin.com.contentprovider;

import android.support.v7.app.AppCompatActivity;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

  private ContentResolver resolver;
  private ListView listView;

  private static final String AUTHORITY = "com.liubin.contentprovider.PersonProvider";
  private static final Uri PERSON_ALL_URI = Uri.parse("content://" + AUTHORITY + "/persons");

  private Handler handler = new Handler() {
    public void handleMessage(Message msg) {
      //update records.
      requery();
    }

    ;
  };

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    resolver = getContentResolver();
    listView = (ListView) findViewById(R.id.listView);

    //为PERSON_ALL_URI注册变化通知
    getContentResolver().registerContentObserver(PERSON_ALL_URI, true, new PersonObserver(handler));
  }

  /**
   * 初始化
   *
   * @param view
   */
  public void init(View view) {
    ArrayList<Person> persons = new ArrayList<Person>();

    Person person1 = new Person("Ella", 22, "lively girl");
    Person person2 = new Person("Jenny", 22, "beautiful girl");
    Person person3 = new Person("Jessica", 23, "sexy girl");
    Person person4 = new Person("Kelly", 23, "hot baby");
    Person person5 = new Person("Jane", 25, "pretty woman");

    persons.add(person1);
    persons.add(person2);
    persons.add(person3);
    persons.add(person4);
    persons.add(person5);

    for (Person person : persons) {
      ContentValues values = new ContentValues();
      values.put("name", person.name);
      values.put("age", person.age);
      values.put("info", person.info);
      resolver.insert(PERSON_ALL_URI, values);
    }
  }

  /**
   * 查询所有记录
   *
   * @param view
   */
  public void query(View view) {
    //    	Uri personOneUri = ContentUris.withAppendedId(PERSON_ALL_URI, 1);查询_id为1的记录
    Cursor c = resolver.query(PERSON_ALL_URI, null, null, null, null);

    CursorWrapper cursorWrapper = new CursorWrapper(c) {

      @Override public String getString(int columnIndex) {
        //将简介前加上年龄
        if (getColumnName(columnIndex).equals("info")) {
          int age = getInt(getColumnIndex("age"));
          return age + " years old, " + super.getString(columnIndex);
        }
        return super.getString(columnIndex);
      }
    };

    //Cursor须含有"_id"字段
    SimpleCursorAdapter adapter =
        new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursorWrapper,
            new String[] { "name", "info" }, new int[] { android.R.id.text1, android.R.id.text2 });
    listView.setAdapter(adapter);

    startManagingCursor(cursorWrapper);  //管理Cursor
  }

  /**
   * 插入一条记录
   *
   * @param view
   */
  public void insert(View view) {
    Person person = new Person("Alina", 26, "attractive lady");
    ContentValues values = new ContentValues();
    values.put("name", person.name);
    values.put("age", person.age);
    values.put("info", person.info);
    resolver.insert(PERSON_ALL_URI, values);
  }

  /**
   * 更新一条记录
   *
   * @param view
   */
  public void update(View view) {
    Person person = new Person();
    person.name = "Jane";
    person.age = 30;
    //将指定name的记录age字段更新为30
    ContentValues values = new ContentValues();
    values.put("age", person.age);
    resolver.update(PERSON_ALL_URI, values, "name = ?", new String[] { person.name });

    //将_id为1的age更新为30
    //    	Uri updateUri = ContentUris.withAppendedId(PERSON_ALL_URI, 1);
    //    	resolver.update(updateUri, values, null, null);
  }

  /**
   * 删除一条记录
   *
   * @param view
   */
  public void delete(View view) {
    //删除_id为1的记录
    Uri delUri = ContentUris.withAppendedId(PERSON_ALL_URI, 1);
    resolver.delete(delUri, null, null);

    //删除所有记录
    //    	resolver.delete(PERSON_ALL_URI, null, null);
  }

  /**
   * 重新查询
   */
  private void requery() {
    //实际操作中可以查询集合信息后Adapter.notifyDataSetChanged();
    query(null);
  }
}