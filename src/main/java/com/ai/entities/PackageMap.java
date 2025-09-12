/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ai.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "package_map")
public class PackageMap implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "package_id")
    private Integer packageId;
    @Column(name = "package_minute")
    private Byte packageMinute;
    @Column(name = "package_day")
    private Byte packageDay;
    @Column(name = "package_month")
    private Byte packageMonth;
    @Column(name = "active")
    private boolean active;
    @Column(name = "app")
    private boolean app;
    @Column(name = "course_lang")
    private String courseLang;

    @Transient
    private String marketPlaceId;

    @Transient
    private String revenueCatId;

    @Transient
    private String revenueCatAppId;

    @Transient
    private String revenueCatTitle;

    @Transient
    private String revenueCatDescription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPackageId() {
        return packageId;
    }

    public void setPackageId(Integer packageId) {
        this.packageId = packageId;
    }

    public Byte getPackageMinute() {
        return packageMinute;
    }

    public void setPackageMinute(Byte packageMinute) {
        this.packageMinute = packageMinute;
    }

    public Byte getPackageDay() {
        return packageDay;
    }

    public void setPackageDay(Byte packageDay) {
        this.packageDay = packageDay;
    }

    public Byte getPackageMonth() {
        return packageMonth;
    }

    public void setPackageMonth(Byte packageMonth) {
        this.packageMonth = packageMonth;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    public boolean isApp() {
        return app;
    }

    public void setApp(boolean app) {
        this.app = app;
    }

    public String getCourseLang() {
        return courseLang;
    }

    public void setCourseLang(String courseLang) {
        this.courseLang = courseLang;
    }

    public String getMarketPlaceId() {
        return String.format("%s_%d", 
            this.courseLang,
            this.packageId);
    }

    public void setMarketPlaceId(String marketPlaceId) {
        this.marketPlaceId = marketPlaceId;
    }

    public String getRevenueCatId() {
        return revenueCatId;
    }

    public void setRevenueCatId(String revenueCatId) {
        this.revenueCatId = revenueCatId;
    }

    public String getRevenueCatAppId() {
        return revenueCatAppId;
    }

    public void setRevenueCatAppId(String revenueCatAppId) {
        this.revenueCatAppId = revenueCatAppId;
    }

    public String getRevenueCatTitle() {
        return revenueCatTitle;
    }

    public void setRevenueCatTitle(String revenueCatTitle) {
        this.revenueCatTitle = revenueCatTitle;
    }

    public String getRevenueCatDescription() {
        return revenueCatDescription;
    }

    public void setRevenueCatDescription(String revenueCatDescription) {
        this.revenueCatDescription = revenueCatDescription;
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
        if (!(object instanceof PackageMap)) {
            return false;
        }
        PackageMap other = (PackageMap) object;
        return (this.id != null || other.id == null) && (this.id == null || this.id.equals(other.id));
    }

    @Override
    public String toString() {
        return "PackageMap[ id=" + id + " ]";
    }
}
