package br.inf.ufg.es.vv.pmd.parser.domain;

import java.io.Serializable;
import java.util.List;

@Entity
public class Arquivo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String name;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Error> errors;

    public Arquivo() {
    }

    public Arquivo(String name, List<Error> errors) {
        this.name = name;
        this.errors = errors;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}
