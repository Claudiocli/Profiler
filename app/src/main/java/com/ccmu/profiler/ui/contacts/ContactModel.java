package com.ccmu.profiler.ui.contacts;

public class ContactModel {

    private final String id;
    private final String name;
    private final String[] numbers;

    public ContactModel(String id, String name, String[] numbers) {
        this.id = id;
        this.name = name;
        this.numbers = numbers;
    }

    public ContactModel() {
        numbers = new String[1];
        id = name = numbers[0] = "";
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return name;
    }

    public String getOnlySurname() {
        if (!name.contains(" "))
            return "";
        return name.substring(name.indexOf(" "));
    }

    public String getOnlyName() {
        if (!name.contains(" "))
            return name;
        return name.substring(0, name.indexOf(" "));
    }

    public String[] getNumbers() {
        return numbers;
    }

    public int howManyNumbers() {
        return numbers.length;
    }


}
