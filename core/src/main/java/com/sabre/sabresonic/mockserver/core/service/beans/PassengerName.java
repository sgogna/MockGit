package com.sabre.sabresonic.mockserver.core.service.beans;

/**
 * PassengerName
 */
public class PassengerName {
    public String last;
    public String first;
    public String title;
    public String type;

    public PassengerName(String last, String first) {
        this(last, first, null);
    }

    public PassengerName(String last, String first, String title) {
        this(last, first, title, null);
    }

    public PassengerName(String last, String first, String title, String type) {
        this.last = last;
        this.first = first;
        this.title = title;
        this.type = type;
    }

    @Override
    public String toString() {
        return last + "/" + first + (title != null ? title : "") + (type != null ? "[" + type + "]" : "");
    }
}
