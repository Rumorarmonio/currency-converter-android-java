package com.example.currencyconverterjava;

public class Currency {
    private String id, numCode, charCode, name;
    private double value, previous;
    private int nominal;

    public Currency(String id, String numCode, String charCode, int nominal, String name, double value, double previous) {
        this.id = id;
        this.numCode = numCode;
        this.charCode = charCode;
        this.nominal = nominal;
        this.name = name;
        this.value = value;
        this.previous = previous;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumCode() {
        return numCode;
    }

    public void setNumCode(String numCode) {
        this.numCode = numCode;
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public int getNominal() {
        return nominal;
    }

    public void setNominal(int nominal) {
        this.nominal = nominal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getPrevious() {
        return previous;
    }

    public void setPrevious(double previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        return "id = " + id + '\n' +
                "numCode = " + numCode + '\n' +
                "charCode = " + charCode + '\n' +
                "nominal = " + nominal + '\n' +
                "name = " + name + '\n' +
                "value = " + value + '\n' +
                "previous = " + previous;
    }
}
