package at.mana.idea.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AnalysisComponent {

    @Id
    @GeneratedValue( generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

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
