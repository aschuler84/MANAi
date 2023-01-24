package at.mana.idea.component.plot.flamegraph;

import at.mana.idea.component.plot.PlotComponent;
import at.mana.idea.component.plot.FunctionTrace;
import com.intellij.ui.jcef.JBCefClient;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FlamegraphPlotComponent extends PlotComponent<FunctionTrace> implements CefLoadHandler {

    private static final String CHART_ID = "/static/flamegraph_chart.html";

    // TODO: replace with real data
    // insert some test data
    public static FunctionTrace getExampleFunctionTrace () {
        FunctionTrace parent = new FunctionTrace("func1", "com.exa", 4, 1, 6);

        FunctionTrace child1 = new FunctionTrace("func11", "com.exa", 6, 6, 4);
        FunctionTrace child2 = new FunctionTrace("func12", "com.exa", 9, 8, 2);
        FunctionTrace child3 = new FunctionTrace("func13", "com.exa", 2, 6, 5);

        FunctionTrace child11 = new FunctionTrace("func111", "com.exa", 6, 6, 4);
        FunctionTrace child12 = new FunctionTrace("func112", "com.exa", 6, 6, 4);
        FunctionTrace child13 = new FunctionTrace("func113", "com.exa", 6, 6, 4);

        child1.appendSubtrace(child11);
        child1.appendSubtrace(child12);
        child1.appendSubtrace(child13);
        parent.appendSubtrace(child1);
        parent.appendSubtrace(child2);
        parent.appendSubtrace(child3);

        return parent;
    }

    @Override
    protected void initBrowser() {
        InputStream stream = this.getClass().getResourceAsStream(CHART_ID);
        InputStreamReader inputReader = new InputStreamReader(stream);

        try (BufferedReader br = new BufferedReader(inputReader)) {
            // load html template into jcef browser
            Collector<CharSequence, ?, String> lineSeparator;
            lineSeparator = Collectors.joining(System.lineSeparator());
            String content = br.lines().collect(lineSeparator);
            this.browser.loadHTML( content, "mana-fg-plot" );

            // initiate load handler
            JBCefClient client = this.browser.getJBCefClient();
            client.addLoadHandler(this, browser.getCefBrowser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // sends a js update command to the browser
    public void update() {
        String param = "updatePlot('" + modelToJson() + "');";
        String url = browser.getCefBrowser().getURL();

        browser.getCefBrowser().executeJavaScript( param, url, 0 );
    }

    private String modelToJson() {
        return model.toJson(true);
    }

    @Override
    public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) { }

    @Override
    public void onLoadStart(CefBrowser browser, CefFrame frame, CefRequest.TransitionType transitionType) { }

    @Override
    public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
        update();
    }

    @Override
    public void onLoadError(CefBrowser browser, CefFrame frame, ErrorCode errorCode, String errorText, String failedUrl) { }
}
