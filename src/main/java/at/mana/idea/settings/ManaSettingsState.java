package at.mana.idea.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "at.mana.idea.setting.ManaAppSettingsState",
        storages = @Storage("ManaSettingsPlugin.xml")
)
public class ManaSettingsState implements PersistentStateComponent<ManaSettingsState> {

    // TODO: add configurtion settings

    public static ManaSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(ManaSettingsState.class);
    }

    @Override
    public @Nullable ManaSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ManaSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
