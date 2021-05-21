package com.ccmu.profiler.ui.contacts;

public class ContactModel {

    private String id;
    private String name;
    private String[] numbers;

    public ContactModel(String id, String name, String[] numbers) {
        this.id = id;
        this.name = name;
        this.numbers = numbers;
    }
    public ContactModel() {
        numbers = new String[1];
        id = name = numbers[0] = "";
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String[] getNumbers() { return numbers; }
    public int howManyNumbers() {return numbers.length;}


}
