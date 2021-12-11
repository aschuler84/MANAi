/*
 * Copyright (c) 2020 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package at.mana.idea.component.plot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

public class BoxPlotComponent extends PlotComponent<List<Series>> {

    private static final String CHART_ID = "/static/bp_chart.html";

    @Override
    protected void initBrowser() {
        // navigate to url
        // execute javascript
            //String file = Files.readString(Path.of(this.getClass().getResource(CHART_ID).getFile()).toUri());
            try (BufferedReader br =
                         new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream( CHART_ID ))) ) {
                String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
                this.browser.loadHTML( content, "mana-ts-plot" );
            } catch( IOException e ) {

            }
        // convert data to json
        // call java script

    }

    public void update() {
        new Thread() {
            @Override
            public void run() {
                try{Thread.sleep( 2000 );} catch( InterruptedException e ){}
                browser.getCefBrowser()
                        .executeJavaScript( "updatePlot(" + modelToJson() + ");", browser.getCefBrowser().getURL(), 0 );
            }
        }.start();

    }

    private String modelToJson() {
        return "[" +
                model.stream().map( Series::toString ).collect(Collectors.joining( "," ))
                + "]";
    }

}
