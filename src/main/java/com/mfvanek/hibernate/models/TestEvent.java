package com.mfvanek.hibernate.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "event", schema = "alien")
public class TestEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mess_body", nullable = false)
    private String message;

    @Column(name = "time_mark", nullable = false)
    private Date timeMark;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventId")
    private Set<TestEventInfo> info;

    public TestEvent(String message, Date timeMark) {
        this.message = message;
        this.timeMark = timeMark;
    }
}
