package com.example.rxjava;

import android.arch.lifecycle.LiveData;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  private SystemBarTintManager mTintManager;
  private View mParentView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    findViewById(R.id.text).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        just();
      }
    });
  }

  private void just() {
    // 各种方法默认都是在调用它的方法所在的线程中执行 .subscribeOn(Schedulers.io()) .subscribeOn(AndroidSchedulers.mainThread())
    Observable.just(1, 2)//
        .doOnNext(new Action1<Integer>() {
          @Override public void call(Integer integer) {
            Timber.e("方法 %s 在线程:%s中执行", "doOnNext", Thread.currentThread().getName());
          }
        })//
        .map(new Func1<Integer, Integer>() {
          @Override public Integer call(Integer integer) {
            Timber.e("方法 %s 在线程:%s中执行", "map", Thread.currentThread().getName());
            return integer;
          }
        })//
        .flatMap(new Func1<Integer, Observable<Integer>>() {
          @Override public Observable<Integer> call(Integer integer) {
            Timber.e("方法 %s 在线程:%s中执行", "flatMap", Thread.currentThread().getName());
            return Observable.just(integer);
          }
        })//
        .filter(new Func1<Integer, Boolean>() {
          @Override public Boolean call(Integer integer) {
            return true;
          }
        })//
        .take(2)//
        .takeFirst(new Func1<Integer, Boolean>() {
          @Override public Boolean call(Integer integer) {
            return true;
          }
        })//
        .first(new Func1<Integer, Boolean>() {
          @Override public Boolean call(Integer integer) {
            return true;
          }
        })//
        .first()//
        // 指定(被观察者方法) map flatMap doOnNext doOnSubscribe 方法的执行线程,注意只能指定在这个方法之前的这些方法的执行线程
        .subscribeOn(Schedulers.io())
        // 指定(观察者方法) onNext onError onComplete 方法的执行线程,即 .subscribe 里的方法执行的线程
        .observeOn(Schedulers.io())//
        .doOnSubscribe(new Action0() {
          @Override public void call() {
            // doOnSubscribe 默认在调用它的方法所在的线程中执行,可以用紧跟着的 subscribeOn 指定执行线程
            Timber.e("方法 %s 在线程:%s中执行", "doOnSubscribe", Thread.currentThread().getName());
          }
        })
        // 指定(被观察者方法) map flatMap doOnNext doOnSubscribe 方法的执行线程,注意只能指定在这个方法之前的这些方法的执行线程
        .subscribeOn(AndroidSchedulers.mainThread())//
        .subscribe(new Action1<Integer>() {
          @Override public void call(Integer integer) {
            Timber.e("执行结果: %s", integer);
            Timber.e("方法 %s 在线程:%s中执行", "onNext", Thread.currentThread().getName());
          }
        }, new Action1<Throwable>() {
          @Override public void call(Throwable throwable) {
            Timber.e("方法 %s 在线程:%s中执行", "onError", Thread.currentThread().getName());
          }
        }, new Action0() {
          @Override public void call() {
            Timber.e("方法 %s 在线程:%s中执行", "onComplete", Thread.currentThread().getName());
          }
        });
  }

  private void create() {
    Observable.create(new Observable.OnSubscribe<Integer>() {
      @Override public void call(Subscriber<? super Integer> subscriber) {
        try {
          subscriber.onStart();
          subscriber.onNext(1);
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    }).subscribe(new Action1<Integer>() {
      @Override public void call(Integer integer) {
        Timber.e(integer + "");
      }
    });
  }

  private void zip() {
    Observable//
        .zip(Observable.just(1, 1), Observable.just(3, 2, 2),
            new Func2<Integer, Integer, String>() {
              @Override public String call(Integer integer, Integer integer2) {
                return integer + integer2 + "";
              }
            })//
        .subscribe(new Action1<String>() {
          @Override public void call(String s) {
            Timber.e(s);
          }
        });
  }
}
