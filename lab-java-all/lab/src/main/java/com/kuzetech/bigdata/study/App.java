package com.kuzetech.bigdata.study;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        int startIndex = (("funnydb-ingest-receive".hashCode() * 31) & 0x7FFFFFFF) % 18;
        System.out.println(startIndex);

        // 1581549697
        // 1581549697 % 9 = 1
        // 1581549697 % 18 = 1

    }
}
