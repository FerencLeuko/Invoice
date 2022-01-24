package com.example.invoice.persistance.entity;

import java.time.OffsetDateTime;

import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@MappedSuperclass
@EntityListeners( AuditingEntityListener.class )
public class BaseEntity
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default")
	private Integer id;
	
	@CreationTimestamp
	private OffsetDateTime created;
	
	@UpdateTimestamp
	private OffsetDateTime modified;
}
