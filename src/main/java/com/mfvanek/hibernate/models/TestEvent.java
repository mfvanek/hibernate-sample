package com.mfvanek.hibernate.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "event", schema = "alien")
public class TestEvent {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "mess_body", nullable = false)
    private String message;

    @Getter
    @Setter
    @Column(name = "time_mark", nullable = false)
    private Date timeMark;

    public TestEvent(String message, Date timeMark) {
        this.message = message;
        this.timeMark = timeMark;
    }
}
