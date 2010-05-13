package entity;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 *
 * @author Pieter
 */
@Entity
public class Vak implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "vak_id")
    private Long id;
    private String nummer;
    private String naam;
    private int studiepunten;
    private String taal;
    private String lokaal;
    @JoinColumn(name = "faculteit_id", referencedColumnName = "faculteit_id")
    @ManyToOne
    private Faculteit faculteit;
    @ManyToMany(mappedBy = "vakken")
    private Collection<Student> studenten;

    public Vak() {
    }

    public Vak(Long id, String nummer, String naam, int studiepunten, String taal) {
        this.id = id;
        this.nummer = nummer;
        this.naam = naam;
        this.studiepunten = studiepunten;
        this.taal = taal;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getNummer() {
        return nummer;
    }

    public void setNummer(String nummer) {
        this.nummer = nummer;
    }

    public int getStudiepunten() {
        return studiepunten;
    }

    public void setStudiepunten(int studiepunten) {
        this.studiepunten = studiepunten;
    }

    public String getTaal() {
        return taal;
    }

    public void setTaal(String taal) {
        this.taal = taal;
    }

    public String getLokaal() {
        return lokaal;
    }

    public void setLokaal(String lokaal) {
        this.lokaal = lokaal;
    }

    public Faculteit getFaculteit() {
        return faculteit;
    }

    public void setFaculteit(Faculteit faculteit) {
        this.faculteit = faculteit;
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
        if (!(object instanceof Vak)) {
            return false;
        }
        Vak other = (Vak) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Vak[id=" + id + "]";
    }
}
