package com.example.mefitness.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Treino implements Serializable {

    private int nome;
    private String descricao;
    private Date timestramp;
    private Map<String, Object> exercicioMap = new HashMap<>();


    public Treino(){}

    public Treino(int nome, String descricao, Date timestramp) {
        this.nome = nome;
        this.descricao = descricao;
        this.timestramp = timestramp;
    }
    public Treino(int nome, String descricao, Date timestramp, Map<String, Object> exercicioMap) {
        this.nome = nome;
        this.descricao = descricao;
        this.timestramp = timestramp;
        this.exercicioMap = exercicioMap;
    }

    public int getNome() {
        return nome;
    }

    public void setNome(int nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getTimestramp() {
        return timestramp;
    }

    public void setTimestramp(Date timestramp) {
        this.timestramp = timestramp;
    }

    public Map<String, Object> getExercicioMap() {
        return exercicioMap;
    }

    public void setExercicioMap(String key, Exercicio value) {
        Map<String, Object> exercicio = new HashMap<>();
        exercicio.put("nome", value.getNumber());
        exercicio.put("image", value.getImage());
        exercicio.put("observacoes", value.getObservacoes());
        exercicioMap = new HashMap<>();

        exercicioMap.put(key, exercicio);
    }
    public void setExercicioMap(Map<String, Object> value) {
        this.exercicioMap = value;
    }
}
