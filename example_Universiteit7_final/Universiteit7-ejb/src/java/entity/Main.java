/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Admin
 */
public class Main {

    public static void main(String[] arg) {
        System.out.println("Initialisatie...");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("Universiteit7");
        EntityManager em = emf.createEntityManager();


        System.out.println("Uitvoeren operaties...");

        em.getTransaction().begin();

        Faculteit fac = new Faculteit();
        fac.setNaam("Faculteit Economie en Bedrijfswetenschappen");
        em.persist(fac);
        Vak vak = new Vak();
        vak.setNaam("Systeemsoftware");
        vak.setFaculteit(fac);
        em.persist(vak);
        Student stu = new Student();
        stu.setNaam("Jan");
        stu.setSnummer("009857");
        em.persist(stu);

        em.getTransaction().commit();


    }
}
