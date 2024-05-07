package org.tcelor.quarkus.api.entity;

import java.time.LocalDate;
import java.util.List;

import org.tcelor.quarkus.api.model.dao.Status;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Person")
public class Person extends PanacheEntity {

    @Column(length = 255)
    public String lastname;

    @Column(length = 255)
    public String firstname;

    public LocalDate birth;
    
    @Column(length = 10)
    public Status status;

    public Uni<PanacheEntityBase> findByLastnameAndFirstname(String lastname, String firstname){
        return find("lastname", lastname, "firstname", firstname).firstResult();
    }

    public Uni<List<PanacheEntityBase>> findAlive(){
        return list("status", Status.Alive);
    }
}