
package br.com.studytrack.view;

import br.com.studytrack.model.*;
import br.com.studytrack.service.SessaoEstudoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuView {

    private Scanner scanner;
    private SessaoEstudoService service;

    public MenuView(Scanner scanner, SessaoEstudoService service) {
        this.scanner = scanner;
        this.service = service;
    }

    // Exibir o menu principal
    public void exibirMenu() {

        int opcao;

        do {
            System.out.println("\n========== STUDYTRACK ==========");
            System.out.println("1 - Cadastrar sessão de estudo");
            System.out.println("2 - Listar sessões de estudo");
            System.out.println("3 - Buscar sessão por ID");
            System.out.println("4 - Atualizar sessão");
            System.out.println("5 - Excluir sessão");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = lerInteiro();

            switch (opcao) {
                case 1:
                    cadastrar();
                    break;

                case 2:
                    listar();
                    break;

                case 3:
                    buscar();
                    break;

                case 4:
                    atualizar();
                    break;

                case 5:
                    excluir();
                    break;

                case 0:
                    System.out.println("Programa encerrado.");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }

        } while (opcao != 0);
    }

    // Cadastrar sessão
    private void cadastrar() {

        try {
            System.out.println("\n--- CADASTRAR SESSÃO ---");

            System.out.print("ID da sessão: ");
            int id = lerInteiro();

            System.out.print("ID do estudante: ");
            int estudanteId = lerInteiro();

            System.out.print("Nome do estudante: ");
            String estudanteNome = scanner.nextLine();

            System.out.print("ID da disciplina: ");
            int disciplinaId = lerInteiro();

            System.out.print("Nome da disciplina: ");
            String disciplinaNome = scanner.nextLine();

            System.out.print("ID do conteúdo: ");
            int conteudoId = lerInteiro();

            System.out.print("Nome do conteúdo: ");
            String conteudoNome = scanner.nextLine();

            System.out.print("Data (AAAA-MM-DD): ");
            LocalDate data = LocalDate.parse(scanner.nextLine());

            System.out.print("Duração em minutos: ");
            int duracao = lerInteiro();

            System.out.print("Exercícios resolvidos: ");
            int exercicios = lerInteiro();

            System.out.print("Status: ");
            String status = scanner.nextLine();

            Estudante estudante = new Estudante(
                    estudanteId, estudanteNome
            );

            Disciplina disciplina = new Disciplina(
                    disciplinaId, disciplinaNome
            );

            Conteudo conteudo = new Conteudo(
                    conteudoId, conteudoNome
            );

            SessaoEstudo sessao = new SessaoEstudo(
                    id,
                    estudante,
                    disciplina,
                    conteudo,
                    data,
                    duracao,
                    exercicios,
                    status
            );

            service.cadastrar(sessao);

            System.out.println("Cadastro realizado!");

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar: " + e.getMessage());
        }
    }

    // Listar sessões
    private void listar() {

        List<SessaoEstudo> sessoes = service.listarTodos();

        System.out.println("\n--- SESSÕES CADASTRADAS ---");

        if (sessoes.isEmpty()) {
            System.out.println("Nenhuma sessão cadastrada.");
            return;
        }

        for (SessaoEstudo sessao : sessoes) {
            mostrarSessao(sessao);
        }
    }

    // Buscar sessão por ID
    private void buscar() {

        try {
            System.out.print("\nDigite o ID da sessão: ");
            int id = lerInteiro();

            Optional<SessaoEstudo> resultado = service.buscarPorId(id);

            if (resultado.isPresent()) {
                mostrarSessao(resultado.get());
            } else {
                System.out.println("Sessão não encontrada.");
            }

        } catch (Exception e) {
            System.out.println("Erro na busca: " + e.getMessage());
        }
    }

    // Atualizar sessão
    private void atualizar() {

        try {
            System.out.print("\nID da sessão que deseja atualizar: ");
            int id = lerInteiro();

            Optional<SessaoEstudo> resultado = service.buscarPorId(id);

            if (resultado.isEmpty()) {
                System.out.println("Sessão não encontrada.");
                return;
            }

            SessaoEstudo sessao = resultado.get();

            System.out.print("Nova data (AAAA-MM-DD): ");
            sessao.setData(LocalDate.parse(scanner.nextLine()));

            System.out.print("Nova duração em minutos: ");
            sessao.setDuracaoMinutos(lerInteiro());

            System.out.print("Nova quantidade de exercícios: ");
            sessao.setExerciciosResolvidos(lerInteiro());

            System.out.print("Novo status: ");
            sessao.setStatus(scanner.nextLine());

            if (service.atualizar(sessao)) {
                System.out.println("Sessão atualizada com sucesso!");
            } else {
                System.out.println("Não foi possível atualizar a sessão.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }
    }

    // Excluir sessão
    private void excluir() {

        try {
            System.out.print("\nID da sessão que deseja excluir: ");
            int id = lerInteiro();

            if (service.excluir(id)) {
                System.out.println("Sessão excluída com sucesso!");
            } else {
                System.out.println("Sessão não encontrada.");
            }

        } catch (Exception e) {
            System.out.println("Erro ao excluir: " + e.getMessage());
        }
    }

    // Mostrar os dados de uma sessão
    private void mostrarSessao(SessaoEstudo sessao) {

        System.out.println("\n------------------------------");
        System.out.println("ID: " + sessao.getId());
        System.out.println("Estudante: " +
                sessao.getEstudante().getNome());
        System.out.println("Disciplina: " +
                sessao.getDisciplina().getNome());
        System.out.println("Conteúdo: " +
                sessao.getConteudo().getNome());
        System.out.println("Data: " + sessao.getData());
        System.out.println("Duração: " +
                sessao.getDuracaoMinutos() + " minutos");
        System.out.println("Exercícios resolvidos: " +
                sessao.getExerciciosResolvidos());
        System.out.println("Status: " + sessao.getStatus());
        System.out.println("------------------------------");
    }

    // Ler números inteiros
    private int lerInteiro() {

        while (!scanner.hasNextInt()) {
            System.out.print("Digite um número inteiro válido: ");
            scanner.nextLine();
        }

        int numero = scanner.nextInt();
        scanner.nextLine();

        return numero;
    }
}
