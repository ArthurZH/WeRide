package com.example.deanwen.myapplication;

/**
 * Created by liuchang on 8/1/15.
 */
public class Record {
    String _email;
    String _name;
    String _start_time;
    String _end_time;

    public Record() {
    }

    public Record(String _email, String _name) {
        this._email = _email;
        this._name = _name;
    }

    public Record(String _email, String _name, String _start_time, String _end_time) {
        this._email = _email;
        this._name = _name;
        this._start_time = _start_time;
        this._end_time = _end_time;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_name() {

        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_start_time() {

        return _start_time;
    }

    public void set_start_time(String _start_time) {
        this._start_time = _start_time;
    }

    public String get_end_time() {

        return _end_time;
    }

    public void set_end_time(String _end_time) {
        this._end_time = _end_time;
    }
}
