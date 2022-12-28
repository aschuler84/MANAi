package at.mana.idea.component.plot.flamegraph;

import at.mana.idea.component.plot.PlotComponent;
import at.mana.idea.component.plot.FunctionTrace;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class FlamegraphPlotComponent extends PlotComponent<FunctionTrace> implements CefLoadHandler {

    private static final String CHART_ID = "/static/flamegraph_chart.html";

    // TODO: replace with real data
    public static FunctionTrace getExampleFunctionTrace () {
        FunctionTrace parent = new FunctionTrace("func1", "com.example.start", 4, 1, 6);

        FunctionTrace child1 = new FunctionTrace("func11", "com.example.start", 6, 6, 4);
        FunctionTrace child2 = new FunctionTrace("func12", "com.example.start", 9, 8, 2);
        FunctionTrace child3 = new FunctionTrace("func13", "com.example.start", 2, 6, 5);

        parent.appendSubtrace(child1);
        parent.appendSubtrace(child2);
        parent.appendSubtrace(child3);

        return parent;
    }

    @Override
    protected void initBrowser() {
        // navigate to url
        // execute javascript
        //String file = Files.readString(Path.of(this.getClass().getResource(CHART_ID).getFile()).toUri());
        try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream( CHART_ID ))) ) {
            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));
            this.browser.loadHTML( content, "mana-fg-plot" );

            // initiate load handler
            this.browser.getJBCefClient().addLoadHandler(this, browser.getCefBrowser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        browser.getCefBrowser().executeJavaScript( "updatePlot('" + modelToJson() + "');", browser.getCefBrowser().getURL(), 0 );
    }

    private String modelToJson() {
        return model.toJson();
    }

    @Override
    public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {

    }

    @Override
    public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) {

    }

    @Override
    public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
        update();
    }

    @Override
    public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) {

    }
}
