package com.mfvanek.hibernate.models;

import com.mfvanek.hibernate.enums.TestEventType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "event_info", schema = "alien")
public class TestEventInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "info_body", nullable = false)
    private String info;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "info_type", columnDefinition = "smallint", nullable = false)
    private TestEventType infoType;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private TestEvent eventId;

    public TestEventInfo(TestEvent eventId, TestEventType infoType, String info) {
        this.eventId = eventId;
        this.infoType = infoType;
        this.info = info;
    }
}
