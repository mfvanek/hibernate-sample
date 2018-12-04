package com.mfvanek.hibernate.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
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

    @Fetch(FetchMode.SUBSELECT)
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventId")
    private Set<TestEventInfo> info = new HashSet<>();

    public TestEvent() {}

    public TestEvent(String message, Date timeMark) {
        this.message = message;
        this.timeMark = timeMark;
    }
}
