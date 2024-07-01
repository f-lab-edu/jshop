package jshop.domain.wallet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@RevisionEntity
@Table(name = "wallet_history_timestamp")
public class WalletHistory extends DefaultRevisionEntity {}
