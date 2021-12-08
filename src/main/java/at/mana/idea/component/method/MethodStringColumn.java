package at.mana.idea.component.method;

import at.mana.idea.model.MethodEnergyModel;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MethodStringColumn<T> extends ColumnInfo<MethodEnergyModel, T> {

    private final Function<MethodEnergyModel, T> mapper;
    private final boolean numberDecorated;

    public MethodStringColumn(@NlsContexts.ColumnName String name, Function<MethodEnergyModel, T> mapper, boolean numberDecorated) {
        super(name);
        this.numberDecorated = numberDecorated;
        this.mapper = mapper;
    }

    @Override
    public @Nullable T valueOf(MethodEnergyModel classEnergyStatistics) {
        return mapper.apply( classEnergyStatistics );
    }

}
