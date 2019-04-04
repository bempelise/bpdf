package bpdf.graph;

import bpdf.symbol.Product;

public class BPDFFiring {

    private String name;
    private Product times;

    public BPDFFiring(String n, Product t) {
        name = n;
        times = t;
    }

    public String getName() {
        return name;
    }

    public Product getTimes() {
        return times;
    }
}
