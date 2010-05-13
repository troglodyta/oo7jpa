package entity;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author Pieter
 */
@Entity
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "student_id")
    private Long id;
    @Basic(optional = false)
    private String snummer;
    private String naam;
    private String geslacht;
    private String voornaam;
    @JoinColumn(name = "faculteit_id", referencedColumnName = "faculteit_id")
    @ManyToOne
    private Faculteit faculteit;
    @JoinTable(name = "vak_student",
    joinColumns = {
        @JoinColumn(name = "student_id", referencedColumnName = "student_id")},
    inverseJoinColumns = {
        @JoinColumn(name = "vak_id", referencedColumnName = "vak_id")})
    @ManyToMany
    private Collection<Vak> vakken;

    public Student() {
    }

    public Student(Long id, String snummer, String naam, String voornaam) {
        this.id = id;
        this.snummer = snummer;
        this.naam = naam;
        this.voornaam = voornaam;
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

    public String getSnummer() {
        return snummer;
    }

    public void setSnummer(String snummer) {
        this.snummer = snummer;
    }

    public String getVoornaam() {
        return voornaam;
    }

    public void setVoornaam(String voornaam) {
        this.voornaam = voornaam;
    }

    public String getGeslacht() {
        return geslacht;
    }

    public void setGeslacht(String geslacht) {
        this.geslacht = geslacht;
    }

    public Faculteit getFaculteit() {
        return faculteit;
    }

    public void setFaculteit(Faculteit faculteit) {
        this.faculteit = faculteit;
    }

    public Collection<Vak> getVakken() {
        return vakken;
    }

    public void setVakken(Collection<Vak> vakken) {
        this.vakken = vakken;
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
        if (!(object instanceof Student)) {
            return false;
        }
        Student other = (Student) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Student[id=" + id + "]";
    }
}
