package com.jkydjk.healthier.clock.util;

/**
 * Created by miclle on 13-6-13.
 *
 * A thread timer.
 */
public class TimerThread extends Thread {

  private Callback callback;
  private long period;

  public TimerThread(Callback callback, long period) {
    this.callback = callback;
    this.period = period;
  }

  @Override
  public void run() {
    super.run();

    try {
      do {
        if (callback != null)
          callback.run();

        if(period <= 0)
          return;

        Thread.sleep(period);
      } while (Thread.interrupted() == false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public interface Callback{
    public void run();
  }

}
