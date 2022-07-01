package at.mana.idea.service.impl;

import at.mana.idea.domain.MemberDescriptor;
import at.mana.idea.domain.Sample;
import at.mana.idea.domain.Trace;
import at.mana.idea.service.MemberDescriptorService;
import at.mana.idea.service.TraceService;
import at.mana.idea.util.HibernateUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class TraceServiceImpl implements TraceService {

    private Project project;
    private MemberDescriptorService memberDescriptorService;

    private TraceServiceImpl( Project project ) {
        this.project = project;
        this.memberDescriptorService = project.getService( MemberDescriptorService.class );
    }

    @Override
    public Sample attributeTraces(Sample sample, JsonObject rootEntry, JsonArray childEntries) {
        return HibernateUtil.executeInTransaction( session -> {
            if (!childEntries.isEmpty()) {
                // get data from samples
                Double[] powerCpu = sample.getPowerCore().toArray(new Double[]{});
                Double[] powerOther = sample.getPowerOther().toArray(new Double[]{});
                Double[] powerRam = sample.getPowerRam().toArray(new Double[]{});
                Double[] powerGpu = sample.getPowerGpu().toArray(new Double[]{});

                childEntries.forEach(jsonElement -> {
                    JsonObject trace = jsonElement.getAsJsonObject();
                    long startMillis = trace.get("start").getAsLong();
                    long endMillis = trace.get("end").getAsLong();
                    String className = trace.get("className").getAsString();
                    String methodName = trace.get("methodName").getAsString();
                    String methodDescriptor = trace.get("methodDescriptor").getAsString();
                    String hash = trace.get("hash").getAsString();

                    AtomicInteger startIndex = new AtomicInteger(-1);
                    AtomicInteger endIndex = new AtomicInteger(-1);
                    int indexOfStartEstimate = Arrays.stream(powerCpu)
                            .peek(aDouble -> startIndex.incrementAndGet())
                            .filter(aDouble ->
                                    sample.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                            + (sample.getSamplingPeriod() * startIndex.get()) >= startMillis)
                            .mapToInt(value -> startIndex.get()).findFirst().orElse(0);

                    int indexOfEndEstimate = Arrays.stream(powerCpu)
                            .peek(aDouble -> endIndex.incrementAndGet())
                            .filter(aDouble -> sample.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                    + (sample.getSamplingPeriod() * endIndex.get()) > endMillis)
                            .mapToInt(value -> endIndex.get()).findFirst()
                            .orElseGet(() -> powerCpu.length-1 );  // if overflow return last or start index

                    Trace methodTrace = new Trace();
                    // This query should find member descriptors which have already been inserted
                    MemberDescriptor descriptor = memberDescriptorService.findOrDefault(hash, new MemberDescriptor(
                            hash, methodName, methodDescriptor, className
                    ));

                    session.saveOrUpdate( descriptor );

                    methodTrace.setDescriptor(descriptor);
                    methodTrace.setStart(Instant.ofEpochMilli(startMillis).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    methodTrace.setEnd(Instant.ofEpochMilli(endMillis).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    methodTrace.setCpuPower(IntStream.range(indexOfStartEstimate, indexOfEndEstimate + 1)
                            .mapToObj(i -> powerCpu[i]).collect(Collectors.toList()));
                    methodTrace.setRamPower(IntStream.range(indexOfStartEstimate, indexOfEndEstimate + 1)
                            .mapToObj(i -> powerRam[i]).collect(Collectors.toList()));
                    methodTrace.setOtherPower(IntStream.range(indexOfStartEstimate, indexOfEndEstimate + 1)
                            .mapToObj(i -> powerOther[i]).collect(Collectors.toList()));
                    methodTrace.setGpuPower(IntStream.range(indexOfStartEstimate, indexOfEndEstimate + 1)
                            .mapToObj(i -> powerGpu[i]).collect(Collectors.toList()));
                    sample.getTrace().add(methodTrace);
                    methodTrace.setSample(sample);
                    session.save(methodTrace);
                    session.save(sample);
                });
            } else {
                // No trace data available...
            }
        return sample;
        });
    }
}
