/* Copyright 2009 EB2 International Limited */
package util;

public class TimeGetter
{
    private static long lastTime = System.currentTimeMillis();

    public static long getDuration()
    {
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - lastTime;
        lastTime = currentTime;
        return duration;
    }

}
