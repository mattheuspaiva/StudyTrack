package br.com.studytrack.model;

import java.time.LocalDate;

public class SessaoEstudo {

    private int id;
    private Estudante estudante;
    private Disciplina disciplina;
    private Conteudo conteudo;
    private LocalDate data;
    private int duracaoMinutos;
    private int exerciciosResolvidos;
    private String status;

    public SessaoEstudo(int id, Estudante estudante,
                        Disciplina disciplina, Conteudo conteudo,
                        LocalDate data, int duracaoMinutos,
                        int exerciciosResolvidos, String status) {

        this.id = id;
        this.estudante = estudante;
        this.disciplina = disciplina;
        this.conteudo = conteudo;
        this.data = data;
        this.duracaoMinutos = duracaoMinutos;
        this.exerciciosResolvidos = exerciciosResolvidos;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Estudante getEstudante() {
        return estudante;
    }

    public void setEstudante(Estudante estudante) {
        this.estudante = estudante;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Conteudo getConteudo() {
        return conteudo;
    }

    public void setConteudo(Conteudo conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(int duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public int getExerciciosResolvidos() {
        return exerciciosResolvidos;
    }

    public void setExerciciosResolvidos(int exerciciosResolvidos) {
        this.exerciciosResolvidos = exerciciosResolvidos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
