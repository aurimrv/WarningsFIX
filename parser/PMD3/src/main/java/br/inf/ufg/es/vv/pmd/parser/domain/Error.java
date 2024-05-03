package br.inf.ufg.es.vv.pmd.parser.domain;

import java.io.Serializable;

@Entity
public class Error implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column
    private String ruleset;
    @Column
    private String rules;
    @Column
    private String priority;
    @Column
    private String classe;
    @Column
    private String externalinfourl;
    @Column
    private String begincolumn;
    @Column
    private String endcolumn;
    @Column
    private String endline;
    @Column
    private String beginline;

    public Error() {
    }

    public Error(String beginline,String endline, String begincolumn, String endcolumn,  
           String classe, String externalinfourl,  String priority, String ruleset, String rules) {
        this.endline = endline;
        this.beginline = beginline;
        this.endcolumn = endcolumn;
        this.begincolumn= begincolumn;
        this.classe = classe;
        this.externalinfourl = externalinfourl;
        this.rules = rules;
        this.ruleset = ruleset;
        this.priority = priority;
    }

    public Long getId() {
        return id;
    }
    
    
     public String getClasse() {
     return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;

     }
    
    
    
     public String getBegincolumn() {
     return begincolumn;
    }

    public void setBegincolumn(String begincolumn) {
        this.begincolumn = begincolumn;
     }
    
       public String getEndcolumn() {
     return endcolumn;
    }

    public void setEndcolumn(String endcolumn) {
        this.endcolumn = endcolumn;
     
    }
    
  public String getExternalinfourl() {
     return externalinfourl;
    }

    public void setExternalinfourl(String externalinfourl) {
        this.externalinfourl = externalinfourl;
} 
    
public String getPriority() {
     return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
}
    
            public String getRuleset() {
        return ruleset;
    }

    public void setRuleset(String ruleset) {
        this.ruleset = ruleset;
}

    
        public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
}


    
    public String getBeginline() {
        return beginline;
    }

    public void setBeginline(String beginline) {
        this.beginline = beginline;
}   
        
    public String getEndline() {
        return endline;
    }

    public void setEndline(String endline) {
        this.endline = endline;
}
    }
