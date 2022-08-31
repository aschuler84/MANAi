package at.mana.idea.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AnalysisComponent {

    @Id
    @GeneratedValue
    private Long id;

    public AnalysisComponent(MemberDescriptor descriptor, Analysis analysis, List<Double> components) {
        this.descriptor = descriptor;
        this.analysis = analysis;
        this.componentValues = components;
    }

    @ManyToOne( fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private MemberDescriptor descriptor;

    @ManyToOne( fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Analysis analysis;

    @ElementCollection
    private List<Double> componentValues;

}
