package com.kuzetech.bigdata.study.funnel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AppTest {

    List<List<Event>> dataList = new ArrayList<>();
    List<List<EventsTimestamp>> resultList = new ArrayList<>();


    @Before
    public void init(){
        List<Event> e1 = new ArrayList<>();
        e1.add(new Event(1, 1));
        e1.add(new Event(2, 2));
        e1.add(new Event(3, 3));
        e1.add(new Event(4, 2));
        e1.add(new Event(5, 4));
        dataList.add(e1);

        List<EventsTimestamp> r1 = new ArrayList<>();
        r1.add(new EventsTimestamp(1,1));
        r1.add(new EventsTimestamp(2,2));
        r1.add(new EventsTimestamp(3,3));
        r1.add(new EventsTimestamp(5,5));
        resultList.add(r1);


        List<Event> e2 = new ArrayList<>();
        e2.add(new Event(1, 1));
        e2.add(new Event(2, 2));
        e2.add(new Event(3, 1));
        e2.add(new Event(4, 2));
        e2.add(new Event(5, 3));
        e2.add(new Event(6, 1));
        e2.add(new Event(7, 2));
        e2.add(new Event(8, 1));
        e2.add(new Event(9, 2));
        e2.add(new Event(10, 3));
        dataList.add(e2);

        List<EventsTimestamp> r2 = new ArrayList<>();
        r2.add(new EventsTimestamp(3,3));
        r2.add(new EventsTimestamp(4,4));
        r2.add(new EventsTimestamp(5,5));
        resultList.add(r2);


        List<Event> e3 = new ArrayList<>();
        e3.add(new Event(1, 1));
        e3.add(new Event(2, 1));
        e3.add(new Event(3, 1));
        e3.add(new Event(4, 2));
        e3.add(new Event(5, 2));
        e3.add(new Event(6, 2));
        e3.add(new Event(7, 3));
        e3.add(new Event(8, 3));
        e3.add(new Event(9, 3));
        e3.add(new Event(10, 4));
        e3.add(new Event(11, 4));
        e3.add(new Event(12, 4));
        dataList.add(e3);

        List<EventsTimestamp> r3 = new ArrayList<>();
        r3.add(new EventsTimestamp(3,3));
        r3.add(new EventsTimestamp(6,6));
        r3.add(new EventsTimestamp(9,9));
        r3.add(new EventsTimestamp(10,10));
        resultList.add(r3);

        List<Event> e4 = new ArrayList<>();
        e4.add(new Event(1, 1));
        e4.add(new Event(2, 2));
        e4.add(new Event(3, 1));
        e4.add(new Event(4, 2));
        e4.add(new Event(5, 3));
        e4.add(new Event(6, 1));
        e4.add(new Event(7, 2));
        e4.add(new Event(8, 3));
        e4.add(new Event(9, 4));
        dataList.add(e4);

        List<EventsTimestamp> r4 = new ArrayList<>();
        r4.add(new EventsTimestamp(6,6));
        r4.add(new EventsTimestamp(7,7));
        r4.add(new EventsTimestamp(8,8));
        r4.add(new EventsTimestamp(9,9));
        resultList.add(r4);

        List<Event> e5 = new ArrayList<>();
        e5.add(new Event(1, 1));
        e5.add(new Event(2, 1));
        e5.add(new Event(3, 2));
        e5.add(new Event(4, 2));
        e5.add(new Event(5, 3));
        e5.add(new Event(6, 3));
        e5.add(new Event(7, 1));
        e5.add(new Event(8, 2));
        dataList.add(e5);

        List<EventsTimestamp> r5 = new ArrayList<>();
        r5.add(new EventsTimestamp(2,2));
        r5.add(new EventsTimestamp(4,4));
        r5.add(new EventsTimestamp(5,5));
        resultList.add(r5);


        List<Event> e6 = new ArrayList<>();
        e6.add(new Event(1, 1));
        e6.add(new Event(2, 1));
        e6.add(new Event(3, 2));
        e6.add(new Event(4, 1));
        e6.add(new Event(5, 3));
        e6.add(new Event(6, 2));
        e6.add(new Event(7, 2));
        e6.add(new Event(8, 4));
        dataList.add(e6);

        List<EventsTimestamp> r6 = new ArrayList<>();
        r6.add(new EventsTimestamp(2,2));
        r6.add(new EventsTimestamp(3,3));
        r6.add(new EventsTimestamp(5,5));
        r6.add(new EventsTimestamp(8,8));
        resultList.add(r6);

    }


    @Test
    public void shouldAnswerWithTrue()
    {
        for (int i = 0; i < resultList.size(); i++) {
            Assert.assertArrayEquals(resultList.get(i).toArray(), App.findFunnel(dataList.get(i),5, 100));
            System.out.println(i + 1);
        }

    }
}
