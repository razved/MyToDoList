package com.example.android.mytodolist;

class TypeItem {
    private int id;
    private String name;
    private int colorCode;

    public TypeItem(int id, String name, int colorCode) {
        this.id = id;
        this.name = name;
        this.colorCode = colorCode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setColorCode(int colorCode) {
        this.colorCode = colorCode;
    }

    public void setName(String name) {
        this.name = name;
    }
}
