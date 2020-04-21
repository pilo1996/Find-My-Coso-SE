package com.camoli.findmycoso;

public class PositionItem {
    private String via, data, ora;

    public PositionItem(String via, String data, String ora) {
        this.via = via;
        this.data = data;
        this.ora = ora;
    }

    public PositionItem(Position pos){

    }

    public String getVia() {
        return via;
    }

    public String getData() {
        return data;
    }

    public String getOra() {
        return ora;
    }
}
