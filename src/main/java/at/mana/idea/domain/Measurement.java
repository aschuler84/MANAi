package at.mana.idea.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Measurement  {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime recorded;

    private Long duration;

    @ManyToOne( fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private MemberDescriptor descriptor;

    @ElementCollection
    private List<Double> powerCore;
    @ElementCollection
    private List<Double> powerGpu;
    @ElementCollection
    private List<Double> powerRam;
    @ElementCollection
    private List<Double> powerOther;


}
