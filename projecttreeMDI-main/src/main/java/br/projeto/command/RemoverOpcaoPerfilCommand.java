/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.projeto.command;

import br.projeto.presenter.EscolhaFuncionalidadesPerfilPresenter;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author USER
 */
public class RemoverOpcaoPerfilCommand implements Command{
    private EscolhaFuncionalidadesPerfilPresenter perfilProjetoDeEstimativaPresenter;

    public RemoverOpcaoPerfilCommand(EscolhaFuncionalidadesPerfilPresenter perfilProjetoDeEstimativaPresenter) {
        this.perfilProjetoDeEstimativaPresenter = perfilProjetoDeEstimativaPresenter;
    }

    @Override
    public void execute() {
        JTable tabela = perfilProjetoDeEstimativaPresenter.getView().getTable();
        DefaultTableModel model = (DefaultTableModel) tabela.getModel();
        
        int linha = tabela.getSelectedRow();
        int coluna = tabela.getSelectedColumn();
        
        if(linha == -1){
            JOptionPane.showMessageDialog(null, "Selecione uma linha para excluir!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirmacao = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir esta linha?", 
                                                    "Confirmação", JOptionPane.YES_NO_OPTION);
    
        if (confirmacao == JOptionPane.YES_OPTION) {
            model.removeRow(linha);
        }
        
    }
    
}
