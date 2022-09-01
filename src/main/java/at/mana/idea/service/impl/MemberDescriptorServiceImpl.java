package at.mana.idea.service.impl;

import at.mana.idea.domain.MemberDescriptor;
import at.mana.idea.service.MemberDescriptorService;
import at.mana.idea.util.HibernateUtil;
import com.intellij.openapi.components.Service;
import org.jetbrains.annotations.Nullable;

@Service
public class MemberDescriptorServiceImpl implements MemberDescriptorService {

    @Override
    public MemberDescriptor findOrDefault(String hash, @Nullable MemberDescriptor memberDescriptor) {
        return HibernateUtil.executeInTransaction(session -> {
            var builder = session.getCriteriaBuilder();
            var query = builder.createQuery(MemberDescriptor.class);
            var root = query.from(MemberDescriptor.class);
            query = query.select(root).where(root.get("hash").in(hash));
            var result = session.createQuery(query).getResultList();
            return result.size() == 1 ? result.get(0) : memberDescriptor;
        });
    }
}
