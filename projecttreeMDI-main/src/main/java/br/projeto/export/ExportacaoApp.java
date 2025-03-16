package br.projeto.export;

import com.opencsv.CSVWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class ExportacaoApp {
    private JTable tabelaDetalhes;

    public ExportacaoApp(JTable tabelaDetalhes) {
        this.tabelaDetalhes = tabelaDetalhes;
    }

    public void exportarParaCSV(double valorTotal) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar arquivo CSV");
        fileChooser.setSelectedFile(new File("funcionalidades.csv"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (CSVWriter writer = new CSVWriter(new FileWriter(fileToSave))) {
                // Cabeçalho da planilha
                String[] header = new String[tabelaDetalhes.getColumnCount()];
                for (int i = 0; i < tabelaDetalhes.getColumnCount(); i++) {
                    header[i] = tabelaDetalhes.getColumnName(i);
                }
                writer.writeNext(header);

                // Dados da tabela
                for (int row = 0; row < tabelaDetalhes.getRowCount(); row++) {
                    String[] rowData = new String[tabelaDetalhes.getColumnCount()];
                    for (int col = 0; col < tabelaDetalhes.getColumnCount(); col++) {
                        rowData[col] = String.valueOf(tabelaDetalhes.getValueAt(row, col));
                    }
                    writer.writeNext(rowData);
                }

                // Adiciona a linha "Valor Total"
                String[] totalRow = new String[tabelaDetalhes.getColumnCount()];
                totalRow[0] = "Valor Total"; // Nome da linha nas duas primeiras colunas
                totalRow[1] = "";
                totalRow[totalRow.length - 1] = String.format("R$ %.2f", valorTotal); // Usa o valor total passado
                writer.writeNext(totalRow);

                JOptionPane.showMessageDialog(null, "CSV exportado com sucesso: " + fileToSave.getAbsolutePath());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erro ao exportar CSV.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void exportarParaPDF(double valorTotal) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar arquivo PDF");
        fileChooser.setSelectedFile(new File("funcionalidades.pdf"));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            Document document = new Document(PageSize.A4.rotate()); // Usar orientação paisagem
            try {
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Título do documento
                Paragraph title = new Paragraph("Relatório de Funcionalidades", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph("\n"));

                // Criar a tabela com o número de colunas da JTable
                PdfPTable table = new PdfPTable(tabelaDetalhes.getColumnCount());
                table.setWidthPercentage(100);
                table.setSpacingBefore(10f);
                table.setSpacingAfter(10f);

                // Definir larguras proporcionais para cada coluna
                float[] columnWidths = new float[tabelaDetalhes.getColumnCount()];
                for (int i = 0; i < columnWidths.length; i++) {
                    columnWidths[i] = 1f;
                }
                table.setWidths(columnWidths);

                // Cabeçalhos
                for (int i = 0; i < tabelaDetalhes.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(tabelaDetalhes.getColumnName(i), FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                // Dados
                for (int row = 0; row < tabelaDetalhes.getRowCount(); row++) {
                    for (int col = 0; col < tabelaDetalhes.getColumnCount(); col++) {
                        String cellValue = String.valueOf(tabelaDetalhes.getValueAt(row, col));
                        PdfPCell cell = new PdfPCell(new Phrase(cellValue));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    }
                }

                // Adiciona a linha "Valor Total"
                for (int col = 0; col < tabelaDetalhes.getColumnCount(); col++) {
                    PdfPCell cell;
                    if (col == 0) {
                        cell = new PdfPCell(new Phrase("Valor Total"));
                    } else if (col == 1) {
                        cell = new PdfPCell(new Phrase(""));
                    } else if (col == tabelaDetalhes.getColumnCount() - 1) {
                        cell = new PdfPCell(new Phrase(String.format("R$ %.2f", valorTotal))); // Usa o valor total passado
                    } else {
                        cell = new PdfPCell(new Phrase(""));
                    }
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    table.addCell(cell);
                }

                document.add(table);
                JOptionPane.showMessageDialog(null, "PDF exportado com sucesso: " + fileToSave.getAbsolutePath());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erro ao exportar PDF: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                document.close();
            }
        }
    }
}