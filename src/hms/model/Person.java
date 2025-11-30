package hms.model;

import java.io.Serializable;

public abstract class Person implements Serializable {
    protected String id;
    protected String name;
    protected String contact;
    protected String email;
    protected String address;
    protected int age;
    protected String gender;

    public Person(String id, String name, int age, String contact) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.gender = "";
        this.email = "";
        this.address = "";
    }

    public Person(String id, String name, int age, String contact, String email, String address, String gender) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Идентификатор не может быть пустым");
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age <= 0 || age > 120) {
            throw new IllegalArgumentException("Возраст должен быть от 1 до 120");
        }
        this.age = age;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            throw new IllegalArgumentException("Контактная информация не может быть пустой");
        }
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Идентификатор: " + id + ", Имя: " + name + ", Возраст: " + age +
                ", Контакт: " + contact + ", Пол: " + gender;
    }
}

