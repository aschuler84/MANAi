package at.mana.idea.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Descriptor extends BaseEntity {

    private String hash;
    private String methodName;
    private String className;

    @OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.ALL )
    private Set<Measurement> measurements = new HashSet<>();



}
