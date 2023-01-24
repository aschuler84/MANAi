package at.mana.idea.component.plot.relativevolume;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URLEncoder;

public class RelativeVolumeVisualizationPanel extends JPanel {
    private final JLabel noDataLabel;
    private String currentTracesJSON;

    public RelativeVolumeVisualizationPanel() {
        this.currentTracesJSON = "[]";
        noDataLabel = new JLabel("No data has been added to the visualization.");

        JButton button = new JButton("Open external Browser");
        button.addActionListener(e -> {
            File chartFile = new File("C:/tmp/relativeVolumePlot.html");
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("static/relativeVolume_chart.html");

            File redirectFile = createRedirectFile(chartFile.getAbsolutePath().replace("\\", "\\\\") + "?data=" + currentTracesJSON);

            try (FileOutputStream outputStream = new FileOutputStream(chartFile, false)) {
                int read;
                byte[] bytes = new byte[8192];
                while ((read = stream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try {
                Desktop.getDesktop().open(redirectFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        this.add(button);
    }

    private File createRedirectFile (String url) {
        File redirectFile = new File("C:/tmp/redirect.html");

        BufferedWriter writer = null;
        try {
            // writes redirect page content to file
            writer = new BufferedWriter(new FileWriter(redirectFile));
            writer.write("<!DOCTYPE HTML>" +
                    "<meta charset=\"UTF-8\">" +
                    "<meta http-equiv=\"refresh\" content=\"1; url=" + url + "\">" +
                    "<script>" +
                    "window.location.href = \"" + url + "\"" +
                    "</script>" +
                    "<title>Page Redirection</title>" +
                    "<!-- Note: don't tell people to `click` the link, just tell them that it is a link. -->" +
                    "If you are not redirected automatically, follow the <a href='" + url + "'>link</a>");
            writer.close();
        }
        catch (IOException e) {
            return null;
        }

        return redirectFile;
    }

    public void refresh(String currentTracesJSON) {
        System.out.println(currentTracesJSON);
        try {
            this.currentTracesJSON = URLEncoder.encode(currentTracesJSON, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(currentTracesJSON);    }
}
