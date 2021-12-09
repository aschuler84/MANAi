package at.mana.idea.service;

import at.mana.core.util.KeyValuePair;
import at.mana.idea.domain.Measurement;
import at.mana.idea.domain.MemberDescriptor;
import at.mana.idea.model.ManaEnergyExperimentModel;
import at.mana.idea.model.MethodEnergyModel;
import at.mana.idea.model.MethodEnergySampleModel;
import at.mana.idea.util.HibernateUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.codeInspection.bytecodeAnalysis.Member;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.ClassUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static at.mana.core.util.MatrixHelper.transpose;
import static at.mana.core.util.MatrixHelper.transposeDbl;

@Service
public class StorageServiceImpl implements StorageService {

    @Override
    public ManaEnergyExperimentModel findDataFor(PsiJavaFile file) {
        ManaEnergyExperimentModel model = new ManaEnergyExperimentModel();
        model.setExperimentFile(file);
        PsiClass[] classes = file.getClasses();
        List<KeyValuePair<PsiMethod, String>> keys = Arrays.stream(classes).flatMap(c -> Arrays.stream(c.getMethods()))
                .map(m -> new KeyValuePair<>(m,
                        new Member(
                                ClassUtil.getJVMClassName(m.getContainingClass()),
                                m.getName(),
                                ClassUtil.getAsmMethodSignature(m)).hashed().toString()))
                .collect(Collectors.toList());

        HibernateUtil.executeInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<MemberDescriptor> query = builder.createQuery(MemberDescriptor.class);
            Root<MemberDescriptor> root = query.from(MemberDescriptor.class);
            query = query.select(root).where(root.get("hash").in(keys.stream().map(KeyValuePair::getValue).collect(Collectors.toList())));
            List<MemberDescriptor> result = session.createQuery(query).getResultList();

            for (MemberDescriptor memberDescriptor : result) {
                Optional<KeyValuePair<PsiMethod, String>> matchOpt = keys.stream().filter(k -> k.getValue().equals(memberDescriptor.getHash())).findFirst();
                if (matchOpt.isPresent()) {
                    PsiMethod matchingMethod = matchOpt.get().getKey();
                    model.getMethodEnergyStatistics().computeIfAbsent(matchingMethod, m -> new ArrayList<>());
                    var mModels = memberDescriptor.getMeasurements().stream()
                            .map(this::fromMeasurement).collect(Collectors.toList());
                    model.getMethodEnergyStatistics().get(matchingMethod).addAll(mModels);
                }
            }
            return model;
        });
        return model;
    }

    @Override
    public MethodEnergyModel findDataFor(@NotNull PsiMethod method, VirtualFile file) {
        var hash = new Member(
                ClassUtil.getJVMClassName(method.getContainingClass()),
                method.getName(),
                ClassUtil.getAsmMethodSignature(method)).hashed().toString();
        MemberDescriptor descriptor = findOrDefault(hash, null);
        if( descriptor != null  ) {
            var mModels = descriptor.getMeasurements().stream()
                    .map(this::fromMeasurement).sorted(Comparator.comparing(MethodEnergyModel::getRecorded)).collect(Collectors.toList());
            return mModels.size() > 0 ? mModels.get(0) : null;
        } else {
            return null;
        }
    }

    @Override
    public void processAndStore(List<String> measurements) {
        if (measurements != null && !measurements.isEmpty()) {
            HibernateUtil.executeInTransaction(session -> {
                for (var measurement : measurements) {
                    if (measurement.length() > 0) {
                        JsonObject jsonTree = (JsonObject) JsonParser.parseString(measurement);
                        JsonArray dataArray = jsonTree.get("data").getAsJsonArray();
                        String hash = jsonTree.get("hash").getAsString();
                        String method = jsonTree.get("methodName").getAsString();
                        String clazz = jsonTree.get("className").getAsString();
                        String methodDescriptor = jsonTree.get("methodDescriptor").getAsString();
                        long duration = jsonTree.get("duration").getAsLong();
                        long samplingRate = jsonTree.get("samplingRate").getAsLong();

                        // try to find method descriptor in database
                        MemberDescriptor descriptor = findOrDefault(hash, new MemberDescriptor(
                                hash, method, methodDescriptor, clazz
                        ));

                        Double[][] energyData = StreamSupport.stream(
                                dataArray.spliterator(), true).map(data -> {
                            JsonObject entry = data.getAsJsonObject();
                            return
                                    new Double[]{
                                            entry.get("powerCore").getAsDouble(),
                                            entry.get("powerGpu").getAsDouble(),
                                            entry.get("powerOther").getAsDouble(),
                                            entry.get("powerRam").getAsDouble(),
                                            entry.get("powerCore").getAsDouble()
                                                    + entry.get("powerGpu").getAsDouble()
                                                    + entry.get("powerOther").getAsDouble()
                                                    + entry.get("powerRam").getAsDouble()
                                    };
                        }).toArray(Double[][]::new);
                        energyData = transposeDbl().apply(energyData);

                        Measurement m = new Measurement();
                        //m.setRecorded();  // TODO set proper recorded date
                        m.setDuration( duration );
                        m.setPowerCore( Arrays.asList( energyData[0] ) );
                        m.setPowerGpu( Arrays.asList( energyData[1] ) );
                        m.setPowerRam( Arrays.asList( energyData[2] ) );
                        m.setPowerOther( Arrays.asList( energyData[3] ) );

                        // store measurements in database
                        m.setDescriptor( descriptor );
                        descriptor.getMeasurements().add( m );
                        session.save( descriptor );
                        return descriptor;
                    }
                }
                return null;
            });
        }
    }

    private MethodEnergyModel fromMeasurement(Measurement measurement) {
        var model = new MethodEnergyModel(measurement.getRecorded());
        var mModel =  new MethodEnergySampleModel(
                        measurement.getDuration(),
               measurement.getPowerCore().toArray(new Double[0]),
               measurement.getPowerGpu().toArray(new Double[0]),
               measurement.getPowerRam().toArray(new Double[0]),
               measurement.getPowerOther().toArray(new Double[0]));
        model.getSamples().add( mModel );
        return model;
    }

    private MemberDescriptor findOrDefault(String hash, @Nullable  MemberDescriptor memberDescriptor) {
        return HibernateUtil.executeInTransaction(session -> {
            var builder = session.getCriteriaBuilder();
            var query = builder.createQuery(MemberDescriptor.class);
            var root = query.from(MemberDescriptor.class);
            query = query.select(root).where(root.get("hash").in(hash));
            var result = session.createQuery(query).getResultList();
            return result.size() == 1 ? result.get(0) : null;
        });
    }

}

