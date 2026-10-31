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

    // Cadastrar uma sessão de estudo
    public void cadastrar(SessaoEstudo sessao) {

        if (sessao == null) {
            throw new IllegalArgumentException(
                    "A sessão não pode ser nula."
            );
        }

        if (sessao.getId() <= 0) {
            throw new IllegalArgumentException(
                    "O ID da sessão deve ser maior que zero."
            );
        }

        if (dao.buscarPorId(sessao.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "Já existe uma sessão com esse ID."
            );
        }

        if (sessao.getEstudante() == null ||
                sessao.getDisciplina() == null ||
                sessao.getConteudo() == null) {

            throw new IllegalArgumentException(
                    "Estudante, disciplina e conteúdo são obrigatórios."
            );
        }

        if (sessao.getEstudante().getId() <= 0 ||
                sessao.getDisciplina().getId() <= 0 ||
                sessao.getConteudo().getId() <= 0) {

            throw new IllegalArgumentException(
                    "Os IDs dos dados relacionados devem ser maiores que zero."
            );
        }

        if (sessao.getData() == null) {
            throw new IllegalArgumentException(
                    "A data da sessão é obrigatória."
            );
        }

        if (sessao.getDuracaoMinutos() <= 0) {
            throw new IllegalArgumentException(
                    "A duração deve ser maior que zero."
            );
        }

        if (sessao.getExerciciosResolvidos() < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de exercícios não pode ser negativa."
            );
        }

        if (sessao.getStatus() == null ||
                sessao.getStatus().isBlank()) {

            throw new IllegalArgumentException(
                    "O status da sessão é obrigatório."
            );
        }

        dao.cadastrar(sessao);
    }

    // Listar todas as sessões
    public List<SessaoEstudo> listarTodos() {
        return dao.listarTodos();
    }

    // Buscar sessão pelo ID
    public Optional<SessaoEstudo> buscarPorId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser maior que zero."
            );
        }

        return dao.buscarPorId(id);
    }

    // Atualizar uma sessão
    public boolean atualizar(SessaoEstudo sessao) {

        if (sessao == null) {
            throw new IllegalArgumentException(
                    "A sessão não pode ser nula."
            );
        }

        if (sessao.getId() <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser maior que zero."
            );
        }

        if (sessao.getData() == null) {
            throw new IllegalArgumentException(
                    "A data da sessão é obrigatória."
            );
        }

        if (sessao.getDuracaoMinutos() <= 0) {
            throw new IllegalArgumentException(
                    "A duração deve ser maior que zero."
            );
        }

        if (sessao.getExerciciosResolvidos() < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de exercícios não pode ser negativa."
            );
        }

        if (sessao.getStatus() == null ||
                sessao.getStatus().isBlank()) {

            throw new IllegalArgumentException(
                    "O status da sessão é obrigatório."
            );
        }

        return dao.atualizar(sessao);
    }

    // Excluir uma sessão
    public boolean excluir(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser maior que zero."
            );
        }

        return dao.excluir(id);
    }
}
