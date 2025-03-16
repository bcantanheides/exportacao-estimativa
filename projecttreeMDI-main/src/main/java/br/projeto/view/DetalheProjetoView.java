package br.projeto.view;

import br.projeto.export.ExportacaoApp;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.sql.Date;

public class DetalheProjetoView extends JInternalFrame {
    private JLabel lblNome, lblCriador, lblData, lblTipoProjeto, lblValorTotal;
    private JTable tabelaDetalhes;
    private DefaultTableModel modeloTabela;
    private JButton btnExportar;
    private ExportacaoApp exportacaoApp;
    private double valorTotal;

    public DetalheProjetoView() {
        setTitle("Detalhes do Projeto");
        setClosable(true);
        setMaximizable(true);
        setIconifiable(false);
        setResizable(true);
        setSize(1000, 700);

        JPanel painelPrincipal = new JPanel(new BorderLayout());
        add(painelPrincipal);

        JPanel painelCabecalho = new JPanel();
        painelCabecalho.setLayout(new GridLayout(3, 2, 10, 15));
        painelCabecalho.setBorder(BorderFactory.createTitledBorder("Informações do Projeto"));

        lblNome = new JLabel("Nome: ");
        lblCriador = new JLabel("Criador: ");
        lblData = new JLabel("Data de Criação: ");
        lblTipoProjeto = new JLabel("Tipo de Projeto: ");

        painelCabecalho.add(lblNome);
        painelCabecalho.add(lblCriador);
        painelCabecalho.add(lblData);
        painelCabecalho.add(lblTipoProjeto);

        painelPrincipal.add(painelCabecalho, BorderLayout.NORTH);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder("Funcionalidades do Projeto"));

        modeloTabela = new DefaultTableModel(new Object[]{"Nome", "Dias ou %", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaDetalhes = new JTable(modeloTabela);
        tabelaDetalhes.setFillsViewportHeight(true);

        JScrollPane scrollTabela = new JScrollPane(tabelaDetalhes);
        painelTabela.add(scrollTabela, BorderLayout.CENTER);

        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblValorTotal = new JLabel("Valor Total: R$ 0,00");
        lblValorTotal.setFont(new Font("Arial", Font.BOLD, 14));
        painelInferior.add(lblValorTotal);
        
        exportacaoApp = new ExportacaoApp(tabelaDetalhes);

        
        btnExportar = new JButton("Exportar");
        btnExportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportarDados();
            }
        });
        painelInferior.add(btnExportar);
        
        painelTabela.add(painelInferior, BorderLayout.SOUTH);
        painelPrincipal.add(painelTabela, BorderLayout.CENTER);
    }

    public void atualizarCabecalho(String nome, String criador, Date data, String tipoProjeto) {
        lblNome.setText("Nome: " + nome);
        lblCriador.setText("Criador: " + criador);
        lblData.setText("Data de Criação: " + data);
        lblTipoProjeto.setText("Tipo de Projeto: " + tipoProjeto);
    }

    public void atualizarTabela(Object[][] dados, double valorTotal) {
        modeloTabela.setRowCount(0);
        for (Object[] linha : dados) {
            modeloTabela.addRow(linha);
        }
        DecimalFormat df = new DecimalFormat("R$ #,##0.00");
        lblValorTotal.setText("Valor Total: " + df.format(valorTotal));
        this.valorTotal = valorTotal;
    }
          
    private void exportarDados() {
        String[] opcoes = {"CSV", "PDF"};
        int escolha = JOptionPane.showOptionDialog(this, "Escolha o formato de exportação:", "Exportar",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);

        if (escolha == 0) {
            exportacaoApp.exportarParaCSV(valorTotal);
        } else if (escolha == 1) {
            exportacaoApp.exportarParaPDF(valorTotal);
        }
    }
}