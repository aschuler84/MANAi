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
