package com.ccmu.profiler.ui.contacts;

public class Contact {

    private String id;
    private String name;
    private String[] numbers;

    public Contact(String id, String name, String[] numbers)    {
        this.id=id;
        this.name=name;
        this.numbers=numbers;
    }
    public Contact()    {
        numbers=new String[1];
        id=name=numbers[0]="";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String[] getNumbers() { return numbers; }
    public int howManyNumbers() {return numbers.length;}


}
