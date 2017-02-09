package com.west.samplewidget;

import java.util.Date;

public class ListItem {

    private String m_Heading;
    private String m_Content;
    private String m_Calendar;
    private Date m_DateTime;


    public String getHeading() {
        return m_Heading;
    }


    public void setHeading(String heading) {
        m_Heading = heading;
    }


    public String getContent() {
        return m_Content;
    }


    public void setContent(String content) {
        m_Content = content;
    }


    public String getCalendar() {
        return m_Calendar;
    }


    public void setCalendar(String calendar) {
        m_Calendar = calendar;
    }


    public Date getDateTime() {
        return m_DateTime;
    }


    public void setDateTime(Date dateTime) {
        m_DateTime = dateTime;
    }

}