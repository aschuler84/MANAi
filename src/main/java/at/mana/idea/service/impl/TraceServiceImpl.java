package at.mana.idea.service.impl;

import at.mana.idea.domain.MemberDescriptor;
import at.mana.idea.domain.Sample;
import at.mana.idea.domain.Trace;
import at.mana.idea.service.MemberDescriptorService;
import at.mana.idea.service.TraceService;
import at.mana.idea.util.DateUtil;
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
                    long startMicros = trace.get("start").getAsLong();
                    long endMicros = trace.get("end").getAsLong();
                    String className = trace.get("className").getAsString();
                    String methodName = trace.get("methodName").getAsString();
                    String methodDescriptor = trace.get("methodDescriptor").getAsString();
                    String hash = trace.get("hash").getAsString();

                    AtomicInteger startIndex = new AtomicInteger(-1);
                    AtomicInteger endIndex = new AtomicInteger(-1);
                    int indexOfStartEstimate = Arrays.stream(powerCpu)
                            .peek(aDouble -> startIndex.incrementAndGet())
                            .filter(aDouble ->
                                    DateUtil.getMicrosecondsSinceEpochFrom( sample.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant() )
                                            + ((sample.getSamplingPeriod() * 1000) * startIndex.get()) >= startMicros)
                            .mapToInt(value -> startIndex.get()).findFirst().orElse(0);

                    int indexOfEndEstimate = Arrays.stream(powerCpu)
                            .peek(aDouble -> endIndex.incrementAndGet())
                            .filter(aDouble -> DateUtil.getMicrosecondsSinceEpochFrom( sample.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant() )
                                    + ((sample.getSamplingPeriod()*1000) * endIndex.get()) > endMicros)
                            .mapToInt(value -> endIndex.get()).findFirst()
                            .orElseGet(() -> powerCpu.length-1 );  // if overflow return last or start index
                    indexOfEndEstimate = Math.min( indexOfEndEstimate, powerCpu.length -1 );
                    Trace methodTrace = new Trace();
                    // This query should find member descriptors which have already been inserted
                    MemberDescriptor descriptor = memberDescriptorService.findOrDefault(hash, new MemberDescriptor(
                            hash, methodName, methodDescriptor, className
                    ));

                    session.saveOrUpdate( descriptor );

                    methodTrace.setDescriptor(descriptor);
                    methodTrace.setStart(DateUtil.getInstantFromMicros(startMicros).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    methodTrace.setEnd(DateUtil.getInstantFromMicros(endMicros).atZone(ZoneId.systemDefault()).toLocalDateTime());
                    methodTrace.setCpuPower(IntStream.range(indexOfStartEstimate, Math.min(indexOfEndEstimate+1, powerCpu.length))
                            .mapToObj(i -> powerCpu[i]).toArray(Double[]::new));
                    methodTrace.setRamPower(IntStream.range(indexOfStartEstimate, Math.min(indexOfEndEstimate+1, powerRam.length))
                            .mapToObj(i -> powerRam[i]).toArray(Double[]::new));
                    methodTrace.setOtherPower(IntStream.range(indexOfStartEstimate, Math.min(indexOfEndEstimate+1, powerOther.length))
                            .mapToObj(i -> powerOther[i]).toArray(Double[]::new));
                    methodTrace.setGpuPower(IntStream.range(indexOfStartEstimate, Math.min(indexOfEndEstimate+1, powerGpu.length))
                            .mapToObj(i -> powerGpu[i]).toArray(Double[]::new));
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
