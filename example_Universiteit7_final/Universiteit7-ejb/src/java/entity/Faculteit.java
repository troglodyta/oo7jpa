package entity;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.Serializable;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author Pieter
 */
@Entity
public class Faculteit implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "faculteit_id")
    private Long id;
    private String naam;
    private String adres;
    private String hoofd;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "faculteit")
    private Collection <Vak> vakken;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "faculteit")
    private Collection <Student> studenten;

    public String getHoofd() {
        return hoofd;
    }

    public void setHoofd(String hoofd) {
        this.hoofd = hoofd;
    }

   

    public Faculteit() {
    }

    public Faculteit(Long id, String naam) {
        this.id = id;
        this.naam = naam;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAdres() {
        return adres;
    }

    public void setAdres(String adres) {
        this.adres = adres;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public Collection<Vak> getVakken() {
        return vakken;
    }

    public void setVakken(Collection<Vak> vakken) {
        this.vakken = vakken;
    }

    public Collection<Student> getStudenten() {
        return studenten;
    }

    public void setStudenten(Collection<Student> studenten) {
        this.studenten = studenten;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Faculteit)) {
            return false;
        }
        Faculteit other = (Faculteit) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Faculteit[id=" + id + "]";
    }

}
