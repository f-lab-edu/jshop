package jshop.domain.delivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@RevisionEntity
@Table(name = "delivery_history_timestamp")
public class DeliveryHistory extends DefaultRevisionEntity {}
