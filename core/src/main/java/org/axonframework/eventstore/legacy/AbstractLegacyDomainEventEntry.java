/*
 * Copyright (c) 2010-2016. Axon Framework
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.axonframework.eventstore.legacy;

import org.axonframework.eventsourcing.DomainEventMessage;
import org.axonframework.eventstore.AbstractEventEntry;
import org.axonframework.eventstore.SerializedDomainEventData;
import org.axonframework.serializer.Serializer;

import javax.persistence.Basic;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;

/**
 * @author Rene de Waele
 */
@MappedSuperclass
@IdClass(AbstractLegacyDomainEventEntry.PK.class)
public abstract class AbstractLegacyDomainEventEntry<T> extends AbstractEventEntry<T> implements SerializedDomainEventData<T> {

    @Id
    private String type;
    @Id
    private String aggregateIdentifier;
    @Id
    private long sequenceNumber;
    @Basic(optional = false)
    private String timeStamp;

    public AbstractLegacyDomainEventEntry(DomainEventMessage<?> eventMessage, Serializer serializer,
                                          Class<T> contentType) {
        super(eventMessage, serializer, contentType);
        timeStamp = eventMessage.getTimestamp().toString();
        type = eventMessage.getType();
        aggregateIdentifier = eventMessage.getAggregateIdentifier();
        sequenceNumber = eventMessage.getSequenceNumber();
    }

    public AbstractLegacyDomainEventEntry(String type, String aggregateIdentifier, long sequenceNumber,
                                          String eventIdentifier, Object timestamp, String payloadType,
                                          String payloadRevision, T payload, T metaData) {
        super(eventIdentifier, payloadType, payloadRevision, payload, metaData);
        if (timestamp instanceof TemporalAccessor) {
            this.timeStamp = Instant.from((TemporalAccessor) timestamp).toString();
        } else {
            this.timeStamp = (String) timestamp;
        }
        this.type = type;
        this.aggregateIdentifier = aggregateIdentifier;
        this.sequenceNumber = sequenceNumber;
    }

    protected AbstractLegacyDomainEventEntry() {
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getAggregateIdentifier() {
        return aggregateIdentifier;
    }

    @Override
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public Instant getTimestamp() {
        return Instant.parse(timeStamp);
    }

    /**
     * Primary key definition of the AbstractEventEntry class. Is used by JPA to support composite primary keys.
     */
    @SuppressWarnings("UnusedDeclaration")
    public static class PK implements Serializable {

        private static final long serialVersionUID = 9182347799552520594L;

        private String aggregateIdentifier;
        private String type;
        private long sequenceNumber;

        /**
         * Constructor for JPA. Not to be used directly
         */
        public PK() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            PK pk = (PK) o;
            return sequenceNumber == pk.sequenceNumber &&
                    Objects.equals(aggregateIdentifier, pk.aggregateIdentifier) &&
                    Objects.equals(type, pk.type);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aggregateIdentifier, type, sequenceNumber);
        }

        @Override
        public String toString() {
            return "PK{type='" + type + '\'' + ", aggregateIdentifier='" + aggregateIdentifier + '\'' +
                    ", sequenceNumber=" + sequenceNumber + '}';
        }
    }

}