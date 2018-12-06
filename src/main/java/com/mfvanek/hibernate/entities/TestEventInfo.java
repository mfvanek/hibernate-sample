package com.mfvanek.hibernate.entities;

import com.mfvanek.hibernate.consts.Const;
import com.mfvanek.hibernate.enums.TestEventType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
//@ToString NO!
@Entity
@Table(name = "event_info", schema = Const.SCHEMA_NAME)
public class TestEventInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "info_body", nullable = false)
    private String info;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "info_type", columnDefinition = "int", nullable = false)
    private TestEventType infoType;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private TestEvent eventId;

    public TestEventInfo() {}

    public TestEventInfo(TestEvent eventId, TestEventType infoType, String info) {
        this.eventId = eventId;
        this.infoType = infoType;
        this.info = info;
    }

    // TODO hashCode() have to be overridden here!!!

    @Override
    public String toString() {
        return String.format("TestEventInfo={id=%d, infoType=%s, info='%s'}", id, infoType, info);
    }
}
