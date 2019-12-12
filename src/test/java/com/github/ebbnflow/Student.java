package com.github.ebbnflow;

import com.google.common.base.Objects;

public class Student {
    private String name;
    private int age;
    private Address homeAddress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age &&
                Objects.equal(name, student.name) &&
                Objects.equal(homeAddress, student.homeAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, age, homeAddress);
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", homeAddress=" + homeAddress +
                '}';
    }
}
