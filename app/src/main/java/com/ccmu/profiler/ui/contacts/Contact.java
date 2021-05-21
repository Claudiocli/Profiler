package com.ccmu.profiler.ui.contacts;

public class Contact {

    private String name;
    private String surname;
    private String[] numbers;

    public Contact(String name, String surname, String[] numbers) {
        this.name = name;
        this.surname = surname;
        this.numbers = numbers;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String[] getNumbers() {
        return numbers;
    }

    public String getFullName() {
        return name + " " + surname;
    }
}
