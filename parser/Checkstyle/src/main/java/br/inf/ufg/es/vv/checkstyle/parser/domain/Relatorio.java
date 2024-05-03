package br.inf.ufg.es.vv.checkstyle.parser.domain;

import java.io.Serializable;
import java.util.List;

@Entity
public class Relatorio implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String version;
    @OneToMany(cascade = CascadeType.ALL)
    private List<File> files;

    
     public Long getId() {
        return id;
    }
     
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}
