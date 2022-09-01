package at.mana.idea.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Trace {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private MemberDescriptor descriptor;

    private LocalDateTime start;
    private LocalDateTime end;

    //@ElementCollection
    @Lob
    private Double[] cpuPower;
    //@ElementCollection
    @Lob
    private Double[] ramPower;
    //@ElementCollection
    @Lob
    private Double[] otherPower;
    //@ElementCollection
    @Lob
    private Double[] gpuPower;

    @ManyToOne( fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Sample sample;
    // 1 Trace associated with 1 sample attribute energy data to sample

}
