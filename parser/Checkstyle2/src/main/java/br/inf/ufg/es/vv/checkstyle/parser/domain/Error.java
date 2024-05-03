package br.inf.ufg.es.vv.checkstyle.parser.domain;

import java.io.Serializable;

@Entity
public class Error implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column
    private String line;
    @Column
    private String coluna;
    @Column
    private String severity;
    @Column
    private String message;
    @Column
    private String source;

    public Error() {
    }

    public Error(String line, String coluna, String severity, String message, String source) {
        this.line = line;
        this.coluna = coluna;
        this.severity = severity;
        this.message = message;
        this.source = source;
    }

     public Long getId() {
        return id;
    }
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getColuna() {
        return coluna;
    }

    public void setColuna(String coluna) {
        this.coluna = coluna;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
