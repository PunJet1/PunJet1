package com.poly.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "Orders")
public class Order implements Serializable {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String address;

    @Temporal(TemporalType.DATE)
    @Column(name = "Createdate")
    Date createDate = new Date();

    @ManyToOne
    @JoinColumn(name = "Username")
    Account account;

    public enum OrderStatus {
        PROCESSING("Đang xử lý"),
        CONFIRMED("Đã xử lý"),
        CANCELED("Hủy đơn");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "statuss")
    OrderStatus status = OrderStatus.PROCESSING;

    @JsonIgnore
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails = new ArrayList<>();
	
}