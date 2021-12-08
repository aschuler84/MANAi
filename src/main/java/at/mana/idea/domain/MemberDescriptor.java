package at.mana.idea.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class MemberDescriptor {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String hash;
    private String methodName;
    private String methodDesc;
    private String className;

    @OneToMany( fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "descriptor")
    private Set<Measurement> measurements = new HashSet<>();

    public MemberDescriptor() {

    }

    public MemberDescriptor( String hash, String methodName, String methodDesc, String className ) {
        this.hash = hash;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.className = className;
    }



}
