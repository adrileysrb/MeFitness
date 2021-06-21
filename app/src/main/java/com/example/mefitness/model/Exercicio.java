package com.example.mefitness.model;

import java.io.Serializable;

public class Exercicio implements Serializable {

    int nome;
    String image;
    String observacoes;

    public Exercicio(){}

    public Exercicio(int nome, String image, String observacoes) {
        this.nome = nome;
        this.image = image;
        this.observacoes = observacoes;
    }

    public int getNumber() {
        return nome;
    }

    public void setNome(int nome) {
        this.nome = nome;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
