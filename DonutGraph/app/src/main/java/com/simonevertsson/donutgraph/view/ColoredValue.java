package com.simonevertsson.donutgraph.view;

import android.graphics.Color;

/**
 * Created by simon.evertsson on 2015-09-21.
 */
public class ColoredValue {

    private int value;
    private int color;

    public ColoredValue(int value, int a, int r, int g, int b) {
        this.value = value;
        this.color = Color.argb(a,r,g,b);
    }

    public ColoredValue(int value, int color) {
        this.value = value;
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }
}
