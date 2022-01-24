package com.example.invoice.persistance.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;

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
@SequenceGenerator( name = "default", sequenceName = "item_seq", allocationSize = 1)
public class Item extends BaseEntity
{
	@NotNull
	@Column(name = "product_name")
	private String productName;
	
	@NotNull
	@Column(name = "unit_price")
	private Double unitPrice;
	
	@NotNull
	private Integer quantity;
	
	@Column(name = "total_item_price")
	private Double itemTotalPrice;
	
	@Column(name = "total_euro_price")
	private Double itemTotalEuroPrice;
	
	@ManyToOne
	@JoinColumn(name = "invoice_id", referencedColumnName = "id")
	private Invoice invoice;
	
}
