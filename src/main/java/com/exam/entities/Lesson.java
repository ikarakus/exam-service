package com.exam.entities;


import javax.persistence.*;

@Entity
@Table(name = "lesson")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "level_id")
    private Long levelId;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "order_no")
    private Integer orderNo;
    @Column(name = "active")
    private boolean active;
    @Column(name = "adult")
    private boolean adult;
    @Column(name = "kids_46")
    private boolean kids_46;
    @Column(name = "kids_710")
    private boolean kids_710;
    @Column(name = "kids_1113")
    private boolean kids_1113;
    @Column(name = "open_ai")
    private boolean openAI;
    @Column(name = "llama")
    private boolean llama;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public boolean isKids_46() {
        return kids_46;
    }

    public void setKids_46(boolean kids_46) {
        this.kids_46 = kids_46;
    }

    public boolean isKids_710() {
        return kids_710;
    }

    public void setKids_710(boolean kids_710) {
        this.kids_710 = kids_710;
    }

    public boolean isKids_1113() {
        return kids_1113;
    }

    public void setKids_1113(boolean kids_1113) {
        this.kids_1113 = kids_1113;
    }

    public boolean isOpenAI() {
        return openAI;
    }

    public void setOpenAI(boolean openAI) {
        this.openAI = openAI;
    }

    public boolean isLlama() {
        return llama;
    }

    public void setLlama(boolean llama) {
        this.llama = llama;
    }
}
