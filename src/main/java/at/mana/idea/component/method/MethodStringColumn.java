package at.mana.idea.component.method;

import at.mana.idea.domain.ClassEnergyStatistics;
import at.mana.idea.domain.MethodEnergyStatistics;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.ui.ColumnInfo;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MethodStringColumn<T> extends ColumnInfo<MethodEnergyStatistics, T> {

    private final Function<MethodEnergyStatistics, T> mapper;
    private final boolean numberDecorated;

    public MethodStringColumn(@NlsContexts.ColumnName String name, Function<MethodEnergyStatistics, T> mapper, boolean numberDecorated) {
        super(name);
        this.numberDecorated = numberDecorated;
        this.mapper = mapper;
    }

    @Override
    public @Nullable T valueOf(MethodEnergyStatistics classEnergyStatistics) {
        return mapper.apply( classEnergyStatistics );
    }

}
