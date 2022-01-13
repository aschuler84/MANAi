/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.plot;

import java.util.Arrays;

public class DefaultSingleStackedBarPlotModel implements SingleStackedBarPlotModel {

    private String[] legend;
    private Double[] values;
    private String title;


    public DefaultSingleStackedBarPlotModel(String[] legend, Double[] values ) {
        if( legend.length != values.length ) {
            throw new IllegalArgumentException( "legend and values size must match" );
        }
        this.values = values;
        this.legend = legend;
    }

    public DefaultSingleStackedBarPlotModel(String title, String[] legend, Double[] values ) {
        if( legend.length != values.length ) {
            throw new IllegalArgumentException( "legend and values size must match" );
        }
        this.title = title;
        this.values = values;
        this.legend = legend;
    }

    @Override
    public double getValueFor(int index) {
        return values[index];
    }

    @Override
    public String getLegendFor(int index) {
        return legend[index];
    }

    @Override
    public double getTotalValue() {
        return Arrays.stream( values ).reduce(Double::sum).orElse( 0.0 );
    }

    @Override
    public int getNoOfStacks() {
        return values.length;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
