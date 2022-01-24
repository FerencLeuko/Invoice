package com.example.invoice.persistance.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@SequenceGenerator( name = "default", sequenceName = "invoice_seq", allocationSize = 1)
public class Invoice extends BaseEntity
{
	@NotNull
	@Column(name = "customer_name")
	private String customerName;
	
	@NotNull
	@Column(name = "issue_date")
	private LocalDate issueDate;
	
	@NotNull
	@Column(name = "due_date")
	private LocalDate dueDate;
	
	@Column(name = "total_price")
	private Double totalPrice;
	
	@Column(name = "total_euro_price")
	private Double totalEuroPrice;
	
	private String comment;
	
	@NotNull
	@OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	private List<Item> items;
}
