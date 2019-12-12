package com.github.ebbnflow;

import java.util.List;
import java.util.Objects;

public class SimplePojo {

    private int myNumber;
    private String myString;
    private List<NestedClass> myNestedList;
    private Student student;

    public int getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public String getMyString() {
        return myString;
    }

    public void setMyString(String myString) {
        this.myString = myString;
    }

    public List<NestedClass> getMyNestedList() {
        return myNestedList;
    }

    public void setMyNestedList(List<NestedClass> myNestedList) {
        this.myNestedList = myNestedList;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "SimplePojo{" +
                "myNumber=" + myNumber +
                ", myString='" + myString + '\'' +
                ", myNestedList=" + myNestedList +
                ", student=" + student +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePojo that = (SimplePojo) o;
        return myNumber == that.myNumber &&
                com.google.common.base.Objects.equal(myString, that.myString) &&
                com.google.common.base.Objects.equal(myNestedList, that.myNestedList) &&
                com.google.common.base.Objects.equal(student, that.student);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(myNumber, myString, myNestedList, student);
    }

    public static class NestedClass{
        private String nestedString;

        public String getNestedString() {
            return nestedString;
        }

        public void setNestedString(String nestedString) {
            this.nestedString = nestedString;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NestedClass that = (NestedClass) o;
            return Objects.equals(nestedString, that.nestedString);
        }

        @Override
        public int hashCode() {
            return Objects.hash(nestedString);
        }

        @Override
        public String toString() {
            return "NestedClass{" +
                    "nestedString='" + nestedString + '\'' +
                    '}';
        }
    }
}


