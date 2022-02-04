package com.avdprog.exchangerates;

public class ValuteData {

    private String ID;
    private String NumCode;
    private String CharCode;
    private String Nominal;
    private String Name;
    private String Value;
    private String Previous;

    public ValuteData(String ID, String numCode, String charCode, String nominal, String name, String value, String previous) {
        this.ID = ID;
        NumCode = numCode;
        CharCode = charCode;
        Nominal = nominal;
        Name = name;
        Value = value;
        Previous = previous;
    }


    public void setID(String ID) {
        this.ID = ID;
    }

    public void setNumCode(String numCode) {
        NumCode = numCode;
    }

    public void setCharCode(String charCode) {
        CharCode = charCode;
    }

    public void setNominal(String nominal) {
        Nominal = nominal;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setValue(String value) {
        Value = value;
    }

    public void setPrevious(String previous) {
        Previous = previous;
    }

    public String getID() {
        return ID;
    }

    public String getNumCode() {
        return NumCode;
    }

    public String getCharCode() {
        return CharCode;
    }

    public String getNominal() {
        return Nominal;
    }

    public String getName() {
        return Name;
    }

    public String getValue() {
        return Value;
    }

    public String getPrevious() {
        return Previous;
    }
}
