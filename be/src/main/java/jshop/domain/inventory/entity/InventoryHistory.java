package jshop.domain.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@RevisionEntity
@Table(name = "inventory_history_timestamp")
public class InventoryHistory extends DefaultRevisionEntity {}
