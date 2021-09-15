package com.example.myPlants;

/**
 * The type Notification.
 */
public class Notification {
    private int notify, hour, minute;

    /**
     * Instantiates a new Notification.
     *
     * @param notify the notify- 1 to notify 0 to cancel
     * @param hour   the hour of alarm
     * @param minute the minute of alarm
     */
    public Notification(int notify, int hour, int minute){
        this.notify = notify;
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Gets notify.
     *
     * @return if notify
     */
    public int getNotify() {
        return notify;
    }

    /**
     * Sets notify.
     *
     * @param notify the notify
     */
    public void setNotify(int notify) {
        this.notify = notify;
    }

    /**
     * Gets hour.
     *
     * @return the hour
     */
    public int getHour() {
        return hour;
    }

    /**
     * Sets hour.
     *
     * @param hour the hour
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Gets minute.
     *
     * @return the minute
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Sets minute.
     *
     * @param minute the minute
     */
    public void setMinute(int minute) {
        this.minute = minute;
    }
}
