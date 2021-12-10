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

    @ManyToOne( fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private MemberDescriptor descriptor;

    @OneToMany( fetch = FetchType.LAZY, cascade =  CascadeType.ALL, mappedBy = "measurement")
    private Set<Sample> samples = new HashSet<>();


}
