package br.com.studytrack.service;

import br.com.studytrack.dao.SessaoEstudoDAO;
import br.com.studytrack.model.SessaoEstudo;

import java.util.List;
import java.util.Optional;

public class SessaoEstudoService {

    private SessaoEstudoDAO dao;

    public SessaoEstudoService(SessaoEstudoDAO dao) {
        this.dao = dao;
    }

    public int cadastrar(SessaoEstudo sessao) {

        if (sessao == null) {
            throw new IllegalArgumentException("A sessão não pode ser nula.");
        }

        if (sessao.getEstudante() == null || sessao.getDisciplina() == null || sessao.getConteudo() == null) {
            throw new IllegalArgumentException("Estudante, disciplina e conteúdo são obrigatórios.");
        }

        if (sessao.getEstudante().getNome() == null || sessao.getEstudante().getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do estudante é obrigatório.");
        }

        if (sessao.getDisciplina().getNome() == null || sessao.getDisciplina().getNome().isBlank()) {
            throw new IllegalArgumentException("O nome da disciplina é obrigatório.");
        }

        if (sessao.getConteudo().getNome() == null || sessao.getConteudo().getNome().isBlank()) {
            throw new IllegalArgumentException("O nome do conteúdo é obrigatório.");
        }

        if (sessao.getData() == null) {
            throw new IllegalArgumentException("A data da sessão é obrigatória.");
        }

        if (sessao.getDuracaoMinutos() <= 0) {
            throw new IllegalArgumentException("A duração deve ser maior que zero.");
        }

        if (sessao.getExerciciosResolvidos() < 0) {
            throw new IllegalArgumentException("A quantidade de exercícios não pode ser negativa.");
        }

        if (sessao.getStatus() == null || sessao.getStatus().isBlank()) {
            throw new IllegalArgumentException("O status da sessão é obrigatório.");
        }

        return dao.cadastrar(sessao);
    }

    public List<SessaoEstudo> listarTodos() {
        return dao.listarTodos();
    }

    public Optional<SessaoEstudo> buscarPorId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser maior que zero.");
        }

        return dao.buscarPorId(id);
    }

    public boolean atualizar(SessaoEstudo sessao) {

        if (sessao == null) {
            throw new IllegalArgumentException("A sessão não pode ser nula.");
        }

        if (sessao.getId() <= 0) {
            throw new IllegalArgumentException("O ID deve ser maior que zero.");
        }

        if (sessao.getData() == null) {
            throw new IllegalArgumentException("A data da sessão é obrigatória.");
        }

        if (sessao.getDuracaoMinutos() <= 0) {
            throw new IllegalArgumentException("A duração deve ser maior que zero.");
        }

        if (sessao.getExerciciosResolvidos() < 0) {
            throw new IllegalArgumentException("A quantidade de exercícios não pode ser negativa.");
        }

        if (sessao.getStatus() == null || sessao.getStatus().isBlank()) {
            throw new IllegalArgumentException("O status da sessão é obrigatório.");
        }

        return dao.atualizar(sessao);
    }

    public boolean excluir(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser maior que zero.");
        }

        return dao.excluir(id);
    }
}
