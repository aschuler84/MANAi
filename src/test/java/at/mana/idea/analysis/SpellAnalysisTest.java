package at.mana.idea.analysis;

import at.mana.idea.domain.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SpellAnalysisTest {

    @Test
    public void testSpellComputation() {
        // Given
        List<Measurement> measurements = new ArrayList<>();
        Measurement m = new Measurement();
        MemberDescriptor foo = new MemberDescriptor();
        foo.setHash("foo");
        MemberDescriptor bar = new MemberDescriptor();
        foo.setHash("bar");

        Sample s = new Sample();
        Trace t = new Trace();
        t.setDescriptor( foo );
        t.setCpuPower( List.of( 1.0,1.0,1.0 ) );
        t.setOtherPower( List.of( 1.0,1.0,1.0 ) );
        t.setRamPower( List.of( 1.0,1.0,1.0 ) );
        t.setGpuPower( List.of( 1.0,1.0,1.0 ) );
        t.setStart(LocalDateTime.now());
        t.setEnd( t.getStart().plusMinutes(1) );
        s.getTrace().add( t );

        t = new Trace();
        t.setDescriptor( bar );
        t.setCpuPower( List.of( 1.0,1.0,1.0 ) );
        t.setOtherPower( List.of( 1.0,1.0,1.0 ) );
        t.setRamPower( List.of( 1.0,1.0,1.0 ) );
        t.setGpuPower( List.of( 1.0,1.0,1.0 ) );
        t.setStart(LocalDateTime.now());
        t.setEnd( t.getStart().plusMinutes(1) );
        s.getTrace().add( t );

        m.getSamples().add( s );
        measurements.add( m );
        SpellAnalysis analysis = new SpellAnalysis();
        // When
        Analysis result = analysis.compute( measurements );
        // Then
        Assert.assertNotNull(result);
        Assert.assertEquals( 2, result.getComponents().size() );
        Assert.assertEquals( 0.5, result.getComponents().stream().findFirst().get().getComponentValues().get(0), 0.00000001 );
        Assert.assertEquals( 0.5, result.getComponents().stream().findFirst().get().getComponentValues().get(1), 0.00000001 );
        Assert.assertEquals( 0.5, result.getComponents().stream().findFirst().get().getComponentValues().get(2), 0.00000001 );

    }
}
