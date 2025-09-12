/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ai.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "user_account")
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "user_packages_id")
    private UserPackage userPackage;
    @Column(name = "debit")
    private Integer debit;
    @Column(name = "credit")
    private Integer credit;
    @Column(name = "balance")
    private Integer balance;
    @Column(name = "start_date")
    private Timestamp startDate;
    @Column(name = "end_date")
    private Timestamp endDate;
    @Column(name = "debit_direct")
    private Integer debitDirect;
    @Column(name = "credit_direct")
    private Integer creditDirect;
    @Column(name = "balance_direct")
    private Integer balanceDirect;
    @Column(name = "total_debit")
    private Integer TotalDebit;
    @Column(name = "active")
    private boolean active;
    @Column(name = "email_sent")
    private boolean emailSent;
    @Column(name = "trial_email_sent")
    private boolean trialEmailSent;
    @Column(name = "created_datetime")
    private Timestamp createdDateTime;
    @Column(name = "updated_datetime")
    private Timestamp updatedDateTime;
    @Column(name = "ai_credit")
    private Integer aiCredit;
    @Column(name = "ai_balance")
    private Integer aiBalance;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserPackage getUserPackage() {
        return userPackage;
    }

    public void setUserPackage(UserPackage userPackage) {
        this.userPackage = userPackage;
    }

    public Integer getDebit() {
        return debit;
    }

    public void setDebit(Integer debit) {
        this.debit = debit;
    }

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getDebitDirect() {
        return debitDirect;
    }

    public void setDebitDirect(Integer debitDirect) {
        this.debitDirect = debitDirect;
    }

    public Integer getCreditDirect() {
        return creditDirect;
    }

    public void setCreditDirect(Integer creditDirect) {
        this.creditDirect = creditDirect;
    }

    public Integer getBalanceDirect() {
        return balanceDirect;
    }

    public void setBalanceDirect(Integer balanceDirect) {
        this.balanceDirect = balanceDirect;
    }

    public Integer getTotalDebit() {
        return TotalDebit;
    }

    public void setTotalDebit(Integer totalDebit) {
        TotalDebit = totalDebit;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmailSent() {
        return emailSent;
    }

    public void setEmailSent(boolean emailSent) {
        this.emailSent = emailSent;
    }

    public boolean isTrialEmailSent() {
        return trialEmailSent;
    }

    public void setTrialEmailSent(boolean trialEmailSent) {
        this.trialEmailSent = trialEmailSent;
    }

    public Timestamp getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Timestamp createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public Timestamp getUpdatedDateTime() {
        return updatedDateTime;
    }

    public void setUpdatedDateTime(Timestamp updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }

    public Integer getAiCredit() {
        return aiCredit;
    }

    public void setAiCredit(Integer aiCredit) {
        this.aiCredit = aiCredit;
    }

    public Integer getAiBalance() {
        return aiBalance;
    }

    public void setAiBalance(Integer aiBalance) {
        this.aiBalance = aiBalance;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserAccount)) {
            return false;
        }
        UserAccount other = (UserAccount) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "UserAccount[ id=" + id + " ]";
    }
}
