package com.rxjava2;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  /**
   * 倒计时
   *
   * @param time 倒计时时长
   */
  @SuppressLint("CheckResult")
  private void countdown(int time) {
    final int countTime = time;
    Observable.interval(1, TimeUnit.SECONDS)
      /*//.compose(bindUntilEvent(FragmentEvent.DESTROY))*/
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .map(new Function<Long, Long>() {
        @Override
        public Long apply(Long aLong) throws Exception {
          return countTime - aLong;
        }
      })
      /*指定执行次数*/
      .take(countTime + 1)
      .subscribe(new Consumer<Long>() {
        @Override
        public void accept(Long aLong) throws Exception {

        }
      }, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable throwable) throws Exception {

        }
      });

    Observable.timer(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
      @Override
      public void accept(Long aLong) throws Exception {

      }
    });
  }

  /**
   * 1.startWith：给你被观察者的数据流前再增加一点同类型的数据，这里增加的是1,2
   */
  @SuppressLint("CheckResult")
  private void just() {
    Observable.just(1, 2, 3)
      .startWith(6)
      .startWithArray(4, 5)
      .observeOn(Schedulers.io())
      .subscribeOn(AndroidSchedulers.mainThread())
      .subscribe(new Consumer<Integer>() {
        @Override
        public void accept(Integer integer) throws Exception {
          Timber.e("startWith" + integer);
        }
      });
  }

  /**
   * 2.merge：把多个被观察者合并到一个被观察者身上一起输出，但是可能会让合并的被观察者发射的数据交错。
   */
  public void merge() {
    Observable<Integer> obs1 = Observable.just(1, 2, 3).subscribeOn(Schedulers.io());
    Observable<Integer> obs2 = Observable.just(4, 5, 6).subscribeOn(AndroidSchedulers.mainThread());
    Observable.merge(obs1, obs2).subscribe(new Consumer<Integer>() {
      @Override
      public void accept(Integer integer) throws Exception {
        Log.i("xbh", integer + "");
      }
    });
  }

  /**
   * 3.concat：也是合并数据，但是严格按照顺序发射，一个被观察者数据发送完前不会发送后一个被观察者的数据。
   */
  public void concat() {
    Observable<Integer> obs1 = Observable.just(1, 2, 3).subscribeOn(Schedulers.io());
    Observable<Integer> obs2 = Observable.just(4, 5, 6);
    Observable.concat(obs1, obs2).subscribe(new Consumer<Integer>() {
      @Override
      public void accept(Integer integer) throws Exception {
        Log.i("xbh", integer + "");
      }
    });
  }

  /**
   * 4.zip合并多个被观察者发出的数据项，但是如果一个被观察者的数据更多，多出来的那部分不会被发送。
   * 实现多个接口数据共同更新 UI
   */
  public void zip() {
    Observable<Integer> obs1 = Observable.just(1, 2, 3, 4, 5);
    Observable<String> obs2 = Observable.just("a", "b", "c");
    Observable.zip(obs1, obs2, new BiFunction<Integer, String, String>() {
      @Override
      public String apply(Integer integer, String s) throws Exception {
        return s + integer;
      }
    }).subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        Timber.e(s);
      }
    });
  }

  /**
   * 5.combineLatest：把第一个被观察者最新的数据，和另外的观察者相连。
   */
  public void combineLatest() {
    Observable<Integer> obs1 = Observable.just(1, 2, 3, 4, 5, 6, 7, 8, 9);
    Observable<String> obs2 = Observable.just("a", "b", "b");
    Observable.combineLatest(obs1, obs2, new BiFunction<Integer, String, String>() {
      @Override
      public String apply(Integer integer, String s) throws Exception {
        return integer + s;
      }
    }).subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        Log.i("xbh", s);
      }
    });
  }

  public void map() {
    Observable.just(1, 2, 3, 4).map(new Function<Integer, String>() {
      @Override
      public String apply(Integer integer) throws Exception {
        return "map" + integer;
      }
    }).subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        Timber.e(s);
      }
    });
  }

  /**
   * flatMap 实现多个网络请求依次依赖
   */
  public void flatMap() {
    Observable.just(1, 2, 3, 4).flatMap(new Function<Integer, ObservableSource<String>>() {
      @Override
      public ObservableSource<String> apply(Integer integer) throws Exception {
        return Observable.just("flatmap" + integer);
      }
    }).subscribe(new Consumer<String>() {
      @Override
      public void accept(String s) throws Exception {
        Timber.e(s);
      }
    });
  }
}
