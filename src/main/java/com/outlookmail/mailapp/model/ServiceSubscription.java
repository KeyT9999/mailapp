package com.outlookmail.mailapp.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "service_subscriptions")
public class ServiceSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "contact_zalo")
    private String contactZalo;

    @Column(name = "contact_instagram")
    private String contactInstagram;

    @NotBlank
    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "pre_expiry_notified", nullable = false)
    private boolean preExpiryNotified = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail == null ? null : customerEmail.trim().toLowerCase();
    }

    public String getContactZalo() {
        return contactZalo;
    }

    public void setContactZalo(String contactZalo) {
        this.contactZalo = contactZalo;
    }

    public String getContactInstagram() {
        return contactInstagram;
    }

    public void setContactInstagram(String contactInstagram) {
        this.contactInstagram = contactInstagram;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isPreExpiryNotified() {
        return preExpiryNotified;
    }

    public void setPreExpiryNotified(boolean preExpiryNotified) {
        this.preExpiryNotified = preExpiryNotified;
    }
} 