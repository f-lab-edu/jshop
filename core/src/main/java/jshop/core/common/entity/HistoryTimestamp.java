package jshop.core.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@RevisionEntity
@Table(name = "history_timestamp")
public class HistoryTimestamp extends DefaultRevisionEntity {}
