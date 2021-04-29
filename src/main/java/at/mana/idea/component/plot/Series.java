package at.mana.idea.component.plot;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Series {

    private String name;
    private Double[] x;
    private Double[] y;

    public Series( Double[] x, Double[] y, String name ) {
        this.x = x;
        this.y = y;
        this.name = name;
    }


    String toJson() {

        String xData = "[" + Arrays.stream( x ).map( Object::toString ).collect( Collectors.joining(",") )  + "]";
        String yData = "[" + Arrays.stream( y ).map( Object::toString ).collect(Collectors.joining(",") )+ "]";

        return new StringBuilder( "{" )
                .append( "\"x\":" ).append( xData ).append(",")
                .append( "\"y\":" ).append( yData ).append(",")
                .append("\"type\": \"scatter\",\"mode\": \"lines\",\"fill\": \"tozeroy\",\"name\": \"test\"}").toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
