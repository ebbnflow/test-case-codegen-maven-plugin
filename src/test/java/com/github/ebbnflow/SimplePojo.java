package com.github.ebbnflow;

import java.util.List;
import java.util.Objects;

public class SimplePojo {

    private int myNumber;
    private String myString;
    private List<NestedClass> myNestedList;

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

    @Override
    public String toString() {
        return "SimplePojo{" +
                "myNumber=" + myNumber +
                ", myString='" + myString + '\'' +
                ", myNestedList=" + myNestedList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePojo that = (SimplePojo) o;
        return myNumber == that.myNumber &&
                Objects.equals(myString, that.myString) &&
                Objects.equals(myNestedList, that.myNestedList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(myNumber, myString, myNestedList);
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


