package at.mana.idea.service;

import at.mana.idea.domain.MemberDescriptor;
import org.jetbrains.annotations.Nullable;

public interface MemberDescriptorService {

    MemberDescriptor findOrDefault(String hash, @Nullable MemberDescriptor memberDescriptor);

}
