package com.example.mefitness.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Treino implements Serializable {

    private String nome;
    private String descricao;
    private Date timestamp;
    private Map<String, Object> exercicios = new HashMap<>();


    public Treino(){}

    public Treino(String nome, String descricao, Date timestamp) {
        this.nome = nome;
        this.descricao = descricao;
        this.timestamp = timestamp;
    }
    public Treino(String nome, String descricao, Date timestamp, Map<String, Object> exercicios) {
        this.nome = nome;
        this.descricao = descricao;
        this.timestamp = timestamp;
        this.exercicios = exercicios;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getExercicios() {
        return exercicios;
    }

    public void setExercicios(String key, Exercicio value) {
        Map<String, Object> exercicio = new HashMap<>();
        exercicio.put("nome", value.getNumber());
        exercicio.put("image", value.getImage());
        exercicio.put("observacoes", value.getObservacoes());
        exercicios = new HashMap<>();

        exercicios.put(key, exercicio);
    }
    public void setExercicios(Map<String, Object> value) {
        this.exercicios = value;
    }
}
