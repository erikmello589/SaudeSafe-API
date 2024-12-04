package com.faeterj.tcc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_status")
public class StatusProfissional
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusId;

    private String name;

    public Long getStatusId() {
        return statusId;
    }



    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }



    public String getName() {
        return name;
    }



    public void setName(String name) {
        this.name = name;
    }



    public enum Values {

        REGULAR(1L),
        CANCELADO(2L),
        FALECIDO(3L),
        TRANSFERIDO(4L),
        SOB_ANALISE(5L);

        long statusId;

        private Values(long statusId) {
            this.statusId = statusId;
        }

        public long getStatusId() {
            return statusId;
        }
        
    }
}
