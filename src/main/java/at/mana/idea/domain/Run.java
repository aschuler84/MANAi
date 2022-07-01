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
public class Run {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime date;

    @OneToMany( fetch = FetchType.EAGER, cascade = CascadeType.ALL,mappedBy = "run")
    private Set<Measurement> measurements = new HashSet();

}
