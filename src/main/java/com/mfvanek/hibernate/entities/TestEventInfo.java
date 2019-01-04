package com.mfvanek.hibernate.entities;

import com.mfvanek.hibernate.consts.Const;
import com.mfvanek.hibernate.enums.TestEventType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.bson.types.ObjectId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
//@Setter NO SETTER HERE!
@NoArgsConstructor
//@ToString NO TOSTRING HERE!
@Entity
@Table(name = "event_info", schema = Const.SCHEMA_NAME)
public class TestEventInfo {

    @Id
    @NotNull
    @Size(min = 24, max = 24)
    @Column(name = "id", columnDefinition = "varchar", length = 24)
    private String id;

    @Setter
    @NotNull
    @Column(name = "info_body", nullable = false)
    private String info;

    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "info_type", columnDefinition = "varchar", length = 50, nullable = false)
    private TestEventType infoType;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private TestEvent eventId;

    public TestEventInfo(TestEventType infoType, String info) {
        this(ObjectId.get(), infoType, info);
    }

    private TestEventInfo(ObjectId id, TestEventType infoType, String info) {
        this.id = id.toHexString();
        this.infoType = infoType;
        this.info = info;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 41)
                .append(id)
                .append(info)
                .append(infoType)
                .toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (!(o instanceof TestEventInfo)) {
            return false;
        }

        TestEventInfo other = (TestEventInfo) o;
        return new EqualsBuilder()
                .append(this.id, other.id)
                .append(this.info, other.info)
                .append(this.infoType, other.infoType)
                .append(this.eventId, other.eventId)
                .isEquals();
    }

    @Override
    public String toString() {
        return String.format("TestEventInfo={id=%s, infoType=%s, info='%s'}", id, infoType, info);
    }

    public void setEventId(TestEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("event can't be null");
        }

        if (this.eventId != null && this.eventId != event) {
            throw new IllegalStateException("eventId is already set");
        }

        this.eventId = event;
    }
}
