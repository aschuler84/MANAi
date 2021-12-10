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
public class Sample {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Measurement measurement;
    private Long duration;

    @ElementCollection
    private List<Double> powerCore;
    @ElementCollection
    private List<Double> powerGpu;
    @ElementCollection
    private List<Double> powerRam;
    @ElementCollection
    private List<Double> powerOther;


}
