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
import java.util.Random;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class FlamegraphPlotComponent extends PlotComponent<FunctionTrace> implements CefLoadHandler {

    private static final String CHART_ID = "/static/flamegraph_chart.html";

    // TODO: replace with real data
    // insert some test data
    public static FunctionTrace getExampleFunctionTrace () {
        //FunctionTrace parent = generateRandomTree(12);
        FunctionTrace parent = new FunctionTrace("refresh", "example.project.GUI", 1, 1, 1);

        FunctionTrace child1 = new FunctionTrace("authorize", "com.example.project.logic", 1, 8, 3, parent);
        FunctionTrace child2 = new FunctionTrace("loadPersonData", "com.example.project.logic", 2, 1, 2, parent);
        FunctionTrace child3 = new FunctionTrace("loadLocationData", "com.example.project.logic", 14, 16, 1, parent);

        FunctionTrace child11 = new FunctionTrace("login", "com.example.project.auth", 3, 5, 1, child1);
        FunctionTrace child12 = new FunctionTrace("getToken", "com.example.project.auth", 5, 3, 1, child1);

        FunctionTrace child21 = new FunctionTrace("readPersonFromDB", "com.example.project.model", 5, 7, 1, child2);
        FunctionTrace child22 = new FunctionTrace("processPersonData", "com.example.project.model", 7, 4, 2, child2);

        FunctionTrace child31 = new FunctionTrace("readLocationFromDB", "com.example.project.model", 3, 5, 2, child3);
        FunctionTrace child32 = new FunctionTrace("processLocationData", "com.example.project.model", 6, 3, 3, child3);

        return parent;
    }

    private static FunctionTrace generateRandomTree(int depth) {
        Random rn = new Random();

        FunctionTrace root = new FunctionTrace("main", "com.example.project", rn.nextInt(90)+10, rn.nextInt(90)+10, rn.nextInt(90)+10);
        generateRandomTreeGeneration(rn, depth-1, rn.nextInt(10)+1, "func", root);

        return root;
    }

    private static void generateRandomTreeGeneration (Random rn, int depth, int num, String namePrefix, FunctionTrace parent) {
        if (depth <= 0) { return; }

        for (int i = 0; i < num; i++) {
            String newNamePrefix = namePrefix + i;

            FunctionTrace trace = new FunctionTrace(newNamePrefix, "com.example.project", rn.nextInt(90)+10, rn.nextInt(90)+10, rn.nextInt(90)+10, parent);
            generateRandomTreeGeneration(rn, depth-1, rn.nextInt(4)+1, newNamePrefix, trace);
        }
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
