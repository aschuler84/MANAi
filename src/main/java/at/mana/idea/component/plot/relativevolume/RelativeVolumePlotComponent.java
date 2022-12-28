package at.mana.idea.component.plot.relativevolume;

import at.mana.idea.component.plot.FunctionTrace;
import at.mana.idea.component.plot.PlotComponent;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.network.CefRequest;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class RelativeVolumePlotComponent extends PlotComponent<FunctionTrace> implements CefLoadHandler {
    private static final String CHART_ID = "/static/relativeVolume_chart.html";
    private static final String relativeFilePath = "./src/main/resources/static/relativeVolume_chart.html";

    @Override
    protected void initBrowser() {
        /*try {
            System.out.println(getClass().getResource(CHART_ID).getFile());
            File relativeVolumeChart = new File(getClass().getResource(CHART_ID).getFile());
            Desktop.getDesktop().open(relativeVolumeChart);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*try (BufferedReader br = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream( CHART_ID ))) ) {
            String content = br.lines().collect(Collectors.joining(System.lineSeparator()));

            // load html template
            this.browser.loadHTML(content, "mana-rvp-plot");

            // initiate load handler
            this.browser.getJBCefClient().addLoadHandler(this, browser.getCefBrowser());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void update() {
        System.out.println("Update called!");
        browser.getCefBrowser().executeJavaScript( "setup();", browser.getCefBrowser().getURL(), 0 );
        System.out.println("Update called end!");
    }

    @Override
    public void onLoadingStateChange(CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
        //update();
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
        System.err.println("ERROR in Browser: "+errorText);
    }
}
