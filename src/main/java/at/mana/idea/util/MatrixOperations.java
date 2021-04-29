package at.mana.idea.util;

import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class MatrixOperations {

    public static UnaryOperator<Double[][]> transpose() {
        return energyData -> IntStream.range(0, energyData[0].length).mapToObj(r ->
                Arrays.stream(energyData).mapToDouble(energyDatum -> energyDatum[r]).boxed().toArray(Double[]::new)
        ).toArray(Double[][]::new);
    }

}
