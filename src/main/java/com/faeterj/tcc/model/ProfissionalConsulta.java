package com.faeterj.tcc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_profissional_consulta")
public class ProfissionalConsulta extends ProfissionalSaude
{
    @Column(name = "consulta_id")
    private Long consultaId;

    public Long getConsultaId() {
        return consultaId;
    }

    public void setConsultaId(Long consultaId) {
        this.consultaId = consultaId;
    }
}
