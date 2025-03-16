package br.projeto.repository;

import br.projeto.repository.abstr.IPerfilProjetoDeEstimativaRepository;
import br.projeto.db.DB;
import br.projeto.db.DbException;
import br.projeto.model.PerfilProjetoDeEstimativaModel;
import br.projeto.model.Projeto;
import br.projeto.model.ProjetoDeEstimativaModel;
import br.projeto.model.Subject;
import br.projeto.model.UsuarioModel;
import br.projeto.presenter.Observer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilProjetoDeEstimativaRepository implements Subject, IPerfilProjetoDeEstimativaRepository{
    private Connection conn;
    private List<Projeto> projetos;
    private List<Observer> observers;
    private List<PerfilProjetoDeEstimativaModel> perfisProjetoDeEstimativaModel;

    public PerfilProjetoDeEstimativaRepository(Connection conn) {
        this.conn = conn;
        observers = new ArrayList<>();
        projetos = new ArrayList<>();
        perfisProjetoDeEstimativaModel = new ArrayList<>();
    }

    
    @Override
    public List<PerfilProjetoDeEstimativaModel> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT perfil_projeto_estimativa.*, usuario.nome, usuario.senha, usuario.email, usuario.formato_log " +
                    "FROM perfil_projeto_estimativa " +
                    "INNER JOIN usuario ON perfil_projeto_estimativa.user_id = usuario.id"
            );

            Map<Integer, UsuarioModel> usuarioModelMap = new HashMap<>();
            List<PerfilProjetoDeEstimativaModel> perfilProjetoDeEstimativaModelList = new ArrayList<>();
            rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioModel usuario = usuarioModelMap.get(rs.getInt("user_id"));
                if (usuario == null) {
                    usuario = instantiateUsuarioModel(rs);
                    usuarioModelMap.put(usuario.getId(), usuario);
                }
                PerfilProjetoDeEstimativaModel perfilProjetoDeEstimativaModel = instantiatePerfilProjetoDeEstimativaModel(rs, usuario);
                perfilProjetoDeEstimativaModelList.add(perfilProjetoDeEstimativaModel);
            }
            return perfilProjetoDeEstimativaModelList;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    
    @Override
    public List<PerfilProjetoDeEstimativaModel> findByUser(UsuarioModel usuarioModel) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT perfil_projeto_estimativa.*, usuario.nome, usuario.senha, usuario.email, usuario.formato_log " +
                    "FROM perfil_projeto_estimativa " +
                    "INNER JOIN usuario ON perfil_projeto_estimativa.user_id = usuario.id " +
                    "WHERE usuario.id=?");
            ps.setInt(1, usuarioModel.getId());

            Map<Integer, UsuarioModel> usuarioModelMap = new HashMap<>();
            List<PerfilProjetoDeEstimativaModel> perfilProjetoDeEstimativaModelList = new ArrayList<>();
            rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioModel usuario = usuarioModelMap.get(rs.getInt("user_id"));
                if (usuario == null) {
                    usuario = instantiateUsuarioModel(rs);
                    usuarioModelMap.put(usuario.getId(), usuario);
                }
                PerfilProjetoDeEstimativaModel perfilProjetoDeEstimativaModel = instantiatePerfilProjetoDeEstimativaModel(rs, usuario);
                perfilProjetoDeEstimativaModelList.add(perfilProjetoDeEstimativaModel);
            }
            return perfilProjetoDeEstimativaModelList;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    
    @Override
    public List<PerfilProjetoDeEstimativaModel> findByProjetoEstimativa(ProjetoDeEstimativaModel projetoDeEstimativaModel) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            ps = conn.prepareStatement("SELECT perfil_projeto_estimativa.*, usuario.nome, usuario.senha, usuario.email, usuario.formato_log FROM perfil_projeto_estimativa "+
                    "INNER JOIN usuario ON perfil_projeto_estimativa.user_id = usuario.id "+
                    "INNER JOIN perfil_projeto_intermediaria ON perfil_projeto_estimativa.id = perfil_projeto_intermediaria.perfil_id "+
                    "WHERE perfil_projeto_intermediaria.projeto_id = ?");
            ps.setInt(1, projetoDeEstimativaModel.getId());

            Map<Integer, UsuarioModel> usuarioModelMap = new HashMap<>();
            List<PerfilProjetoDeEstimativaModel> perfilProjetoDeEstimativaModelList = new ArrayList<>();
            rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioModel usuario = usuarioModelMap.get(rs.getInt("user_id"));
                if (usuario == null) {
                    usuario = instantiateUsuarioModel(rs);
                    usuarioModelMap.put(usuario.getId(), usuario);
                }
                PerfilProjetoDeEstimativaModel perfilProjetoDeEstimativaModel = instantiatePerfilProjetoDeEstimativaModel(rs, usuario);
                perfilProjetoDeEstimativaModelList.add(perfilProjetoDeEstimativaModel);
            }
            //perfisProjetoDeEstimativaModel.addAll(perfisProjetoDeEstimativaModel);
            //notifyObservers();//VERIFICAR SE FUNCIONA SEM
            return perfilProjetoDeEstimativaModelList;
        }catch(SQLException e){
            throw new DbException(e.getMessage());
        }
    }

    
    @Override
    public PerfilProjetoDeEstimativaModel findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT perfil_projeto_estimativa.*, usuario.nome, usuario.senha, usuario.email, usuario.formato_log FROM perfil_projeto_estimativa " +
                    "INNER JOIN usuario ON perfil_projeto_estimativa.user_id = usuario.id " +
                    "WHERE perfil_projeto_estimativa.id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                UsuarioModel usuarioModel = instantiateUsuarioModel(rs);
                PerfilProjetoDeEstimativaModel perfilProjetoDeEstimativaModel = instantiatePerfilProjetoDeEstimativaModel(rs, usuarioModel);
                return perfilProjetoDeEstimativaModel;
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
        return null;
    }

    
    @Override
    public Integer insert(PerfilProjetoDeEstimativaModel perfilProjetoDeEstimativaModel) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("INSERT INTO perfil_projeto_estimativa(user_id, nome_perfil, pequeno, medio, grande, " +
                    "mvp, basico, profissional, cadastro_por_email_senha, " +
                    "cadastro_por_facebook, cadastro_por_twitter, cadastro_por_google, " +
                    "cadastro_por_linkedin, cadastro_por_github, cadastro_por_convite_usuario, " +
                    "cadastro_por_contas_multitenant, cadastro_por_subdominios, " +
                    "cadastro_por_dominios_personalizados, painel, feed_de_atividades, " +
                    "upload_de_arquivos, upload_de_midia, perfis_de_usuario, " +
                    "emails_transacionais, tags, avaliacoes_ou_comentarios, " +
                    "processamento_audio_video, pesquisa_texto_livre, pesquisa, " +
                    "calendario, exibicao_dados_mapa_geolocalizacao, " +
                    "exibicao_marcadores_regioes_mapa_personalizados, reservas, " +
                    "mensagens, foruns_ou_comentarios, compartilhamento_social, " +
                    "integracao_facebook_open_graph, notificacao_push, planos_de_assinatura, " +
                    "processamento_de_pagamento, carrinho_de_compras, marketplace_de_usuarios, " +
                    "gerenciamento_de_produtos, compras_dentro_do_aplicativo, " +
                    "coleta_informacao_pagamento, integracao_cms, paginas_administracao_usuarios, " +
                    "moderacao_aprovacao_conteudo, intercom, analises_uso, " +
                    "relatorios_erro, monitoramento_performance, suporte_multilingue, " +
                    "conectar_servicos_de_terceiros, api_para_terceiros, envio_sms, " +
                    "mascaramento_numero_telefone, seguranca_baseada_certificado_ssl, " +
                    "protecao_contra_dos, autenticacao_duas_etapas, desenvolvimento_especifico_app, " +
                    "design_icone_app, sincronizacao_nuvem, dados_sensores_dispositivo, " +
                    "codigo_barra_qr_code, dados_saude, apple_watch, gerente_de_projetos, " +
                    "taxa_diaria_design, taxa_diaria_gerencia_projeto, taxa_diaria_desenvolvimento, data_criacao) " +
                    "VALUES( ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?,  " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, perfilProjetoDeEstimativaModel.getUsuarioModel().getId());
            ps.setString(2, perfilProjetoDeEstimativaModel.getNomePerfil());
            setIntOrNull(ps, 3, perfilProjetoDeEstimativaModel.getPequeno());
            setIntOrNull(ps, 4, perfilProjetoDeEstimativaModel.getMedio());
            setIntOrNull(ps, 5, perfilProjetoDeEstimativaModel.getGrande());
            setIntOrNull(ps, 6, perfilProjetoDeEstimativaModel.getMvp());
            setIntOrNull(ps, 7, perfilProjetoDeEstimativaModel.getBasico());
            setIntOrNull(ps, 8, perfilProjetoDeEstimativaModel.getProfissional());
            setIntOrNull(ps, 9, perfilProjetoDeEstimativaModel.getCadastroPorEmailSenha());
            setIntOrNull(ps, 10, perfilProjetoDeEstimativaModel.getCadastroPorFacebook());
            setIntOrNull(ps, 11, perfilProjetoDeEstimativaModel.getCadastroPorTwitter());
            setIntOrNull(ps, 12, perfilProjetoDeEstimativaModel.getCadastroPorGoogle());
            setIntOrNull(ps, 13, perfilProjetoDeEstimativaModel.getCadastroPorLinkedin());
            setIntOrNull(ps, 14, perfilProjetoDeEstimativaModel.getCadastroPorGithub());
            setIntOrNull(ps, 15, perfilProjetoDeEstimativaModel.getCadastroPorConviteUsuario());
            setIntOrNull(ps, 16, perfilProjetoDeEstimativaModel.getCadastroPorContasMultitenant());
            setIntOrNull(ps, 17, perfilProjetoDeEstimativaModel.getCadastroPorSubdominios());
            setIntOrNull(ps, 18, perfilProjetoDeEstimativaModel.getCadastroPorDominiosPersonalizados());
            setIntOrNull(ps, 19, perfilProjetoDeEstimativaModel.getPainel());
            setIntOrNull(ps, 20, perfilProjetoDeEstimativaModel.getFeedDeAtividades());
            setIntOrNull(ps, 21, perfilProjetoDeEstimativaModel.getUploadDeArquivos());
            setIntOrNull(ps, 22, perfilProjetoDeEstimativaModel.getUploadDeMidia());
            setIntOrNull(ps, 23, perfilProjetoDeEstimativaModel.getPerfisDeUsuario());
            setIntOrNull(ps, 24, perfilProjetoDeEstimativaModel.getEmailsTransacionais());
            setIntOrNull(ps, 25, perfilProjetoDeEstimativaModel.getTags());
            setIntOrNull(ps, 26, perfilProjetoDeEstimativaModel.getAvaliacoesOuComentarios());
            setIntOrNull(ps, 27, perfilProjetoDeEstimativaModel.getProcessamentoAudioVideo());
            setIntOrNull(ps, 28, perfilProjetoDeEstimativaModel.getPesquisaTextoLivre());
            setIntOrNull(ps, 29, perfilProjetoDeEstimativaModel.getPesquisa());
            setIntOrNull(ps, 30, perfilProjetoDeEstimativaModel.getCalendario());
            setIntOrNull(ps, 31, perfilProjetoDeEstimativaModel.getExibicaoDadosMapaGeolocalizacao());
            setIntOrNull(ps, 32, perfilProjetoDeEstimativaModel.getExibicaoMarcadoresRegioesMapaPersonalizados());
            setIntOrNull(ps, 33, perfilProjetoDeEstimativaModel.getReservas());
            setIntOrNull(ps, 34, perfilProjetoDeEstimativaModel.getMensagens());
            setIntOrNull(ps, 35, perfilProjetoDeEstimativaModel.getForunsOuComentarios());
            setIntOrNull(ps, 36, perfilProjetoDeEstimativaModel.getCompartilhamentoSocial());
            setIntOrNull(ps, 37, perfilProjetoDeEstimativaModel.getIntegracaoFacebookOpenGraph());
            setIntOrNull(ps, 38, perfilProjetoDeEstimativaModel.getNotificacaoPush());
            setIntOrNull(ps, 39, perfilProjetoDeEstimativaModel.getPlanosDeAssinatura());
            setIntOrNull(ps, 40, perfilProjetoDeEstimativaModel.getProcessamentoDePagamento());
            setIntOrNull(ps, 41, perfilProjetoDeEstimativaModel.getCarrinhoDeCompras());
            setIntOrNull(ps, 42, perfilProjetoDeEstimativaModel.getMarketplaceDeUsuarios());
            setIntOrNull(ps, 43, perfilProjetoDeEstimativaModel.getGerenciamentoDeProdutos());
            setIntOrNull(ps, 44, perfilProjetoDeEstimativaModel.getComprasDentroDoAplicativo());
            setIntOrNull(ps, 45, perfilProjetoDeEstimativaModel.getColetaInformacaoPagamento());
            setIntOrNull(ps, 46, perfilProjetoDeEstimativaModel.getIntegracaoCms());
            setIntOrNull(ps, 47, perfilProjetoDeEstimativaModel.getPaginasAdministracaoUsuarios());
            setIntOrNull(ps, 48, perfilProjetoDeEstimativaModel.getModeracaoAprovacaoConteudo());
            setIntOrNull(ps, 49, perfilProjetoDeEstimativaModel.getIntercom());
            setIntOrNull(ps, 50, perfilProjetoDeEstimativaModel.getAnalisesUso());
            setIntOrNull(ps, 51, perfilProjetoDeEstimativaModel.getRelatoriosErro());
            setIntOrNull(ps, 52, perfilProjetoDeEstimativaModel.getMonitoramentoPerformance());
            setIntOrNull(ps, 53, perfilProjetoDeEstimativaModel.getSuporteMultilingue());
            setIntOrNull(ps, 54, perfilProjetoDeEstimativaModel.getConectarServicosDeTerceiros());
            setIntOrNull(ps, 55, perfilProjetoDeEstimativaModel.getApiParaTerceiros());
            setIntOrNull(ps, 56, perfilProjetoDeEstimativaModel.getEnvioSms());
            setIntOrNull(ps, 57, perfilProjetoDeEstimativaModel.getMascaramentoNumeroTelefone());
            setIntOrNull(ps, 58, perfilProjetoDeEstimativaModel.getSegurancaBaseadaCertificadoSsl());
            setIntOrNull(ps, 59, perfilProjetoDeEstimativaModel.getProtecaoContraDos());
            setIntOrNull(ps, 60, perfilProjetoDeEstimativaModel.getAutenticacaoDuasEtapas());
            setIntOrNull(ps, 61, perfilProjetoDeEstimativaModel.getDesenvolvimentoEspecificoApp());
            setIntOrNull(ps, 62, perfilProjetoDeEstimativaModel.getDesignIconeApp());
            setIntOrNull(ps, 63, perfilProjetoDeEstimativaModel.getSincronizacaoNuvem());
            setIntOrNull(ps, 64, perfilProjetoDeEstimativaModel.getDadosSensoresDispositivo());
            setIntOrNull(ps, 65, perfilProjetoDeEstimativaModel.getCodigoBarraQrCode());
            setIntOrNull(ps, 66, perfilProjetoDeEstimativaModel.getDadosSaude());
            setIntOrNull(ps, 67, perfilProjetoDeEstimativaModel.getAppleWatch());
            setIntOrNull(ps, 68, perfilProjetoDeEstimativaModel.getGerenteDeProjetos());
            setDoubleOrNull(ps,69, perfilProjetoDeEstimativaModel.getTaxaDiariaDesign());
            setDoubleOrNull(ps, 70, perfilProjetoDeEstimativaModel.getTaxaDiariaGerenciaProjeto());
            ps.setDouble(71, perfilProjetoDeEstimativaModel.getTaxaDiariaDesenvolvimento());
            ps.setDate(72, perfilProjetoDeEstimativaModel.getDataCriacao());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    perfilProjetoDeEstimativaModel.setId(rs.getInt(1));
                    perfisProjetoDeEstimativaModel.add(perfilProjetoDeEstimativaModel);
                    notifyObservers();
                    return rs.getInt(1);
                } else {
                    throw new DbException("Unexpected error! No rows affected!");
                }
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
        return null;
    }

    
    @Override
    public void update(PerfilProjetoDeEstimativaModel perfilProjetoDeEstimativaModel) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("UPDATE perfil_projeto_estimativa SET user_id = ?, nome_perfil = ?, pequeno = ?, medio = ?, grande = ?, " +
                            "mvp = ?, basico = ?, profissional = ?, cadastro_por_email_senha = ?, " +
                            "cadastro_por_facebook = ?, cadastro_por_twitter = ?, cadastro_por_google = ?, " +
                            "cadastro_por_linkedin = ?, cadastro_por_github = ?, cadastro_por_convite_usuario = ?, " +
                            "cadastro_por_contas_multitenant = ?, cadastro_por_subdominios = ?, " +
                            "cadastro_por_dominios_personalizados = ?, painel = ?, feed_de_atividades = ?, " +
                            "upload_de_arquivos = ?, upload_de_midia = ?, perfis_de_usuario = ?, " +
                            "emails_transacionais = ?, tags = ?, avaliacoes_ou_comentarios = ?, " +
                            "processamento_audio_video = ?, pesquisa_texto_livre = ?, pesquisa = ?, " +
                            "calendario = ?, exibicao_dados_mapa_geolocalizacao = ?, " +
                            "exibicao_marcadores_regioes_mapa_personalizados = ?, reservas = ?, " +
                            "mensagens = ?, foruns_ou_comentarios = ?, compartilhamento_social = ?, " +
                            "integracao_facebook_open_graph = ?, notificacao_push = ?, planos_de_assinatura = ?, " +
                            "processamento_de_pagamento = ?, carrinho_de_compras = ?, marketplace_de_usuarios = ?, " +
                            "gerenciamento_de_produtos = ?, compras_dentro_do_aplicativo = ?, " +
                            "coleta_informacao_pagamento = ?, integracao_cms = ?, paginas_administracao_usuarios = ?, " +
                            "moderacao_aprovacao_conteudo = ?, intercom = ?, analises_uso = ?, " +
                            "relatorios_erro = ?, monitoramento_performance = ?, suporte_multilingue = ?, " +
                            "conectar_servicos_de_terceiros = ?, api_para_terceiros = ?, envio_sms = ?, " +
                            "mascaramento_numero_telefone = ?, seguranca_baseada_certificado_ssl = ?, " +
                            "protecao_contra_dos = ?, autenticacao_duas_etapas = ?, desenvolvimento_especifico_app = ?, " +
                            "design_icone_app = ?, sincronizacao_nuvem = ?, dados_sensores_dispositivo = ?, " +
                            "codigo_barra_qr_code = ?, dados_saude = ?, apple_watch = ?, gerente_de_projetos = ?, " +
                            "taxa_diaria_design = ?, taxa_diaria_gerencia_projeto = ?, taxa_diaria_desenvolvimento = ?, data_criacao = ? " +
                            "WHERE id = ?"
                    , PreparedStatement.RETURN_GENERATED_KEYS);
            setIntOrNull(ps, 1, perfilProjetoDeEstimativaModel.getUsuarioModel().getId());
            ps.setString(2, perfilProjetoDeEstimativaModel.getNomePerfil());
            setIntOrNull(ps, 3, perfilProjetoDeEstimativaModel.getPequeno());
            setIntOrNull(ps, 4, perfilProjetoDeEstimativaModel.getMedio());
            setIntOrNull(ps, 5, perfilProjetoDeEstimativaModel.getGrande());
            setIntOrNull(ps, 6, perfilProjetoDeEstimativaModel.getMvp());
            setIntOrNull(ps, 7, perfilProjetoDeEstimativaModel.getBasico());
            setIntOrNull(ps, 8, perfilProjetoDeEstimativaModel.getProfissional());
            setIntOrNull(ps, 9, perfilProjetoDeEstimativaModel.getCadastroPorEmailSenha());
            setIntOrNull(ps, 10, perfilProjetoDeEstimativaModel.getCadastroPorFacebook());
            setIntOrNull(ps, 11, perfilProjetoDeEstimativaModel.getCadastroPorTwitter());
            setIntOrNull(ps, 12, perfilProjetoDeEstimativaModel.getCadastroPorGoogle());
            setIntOrNull(ps, 13, perfilProjetoDeEstimativaModel.getCadastroPorLinkedin());
            setIntOrNull(ps, 14, perfilProjetoDeEstimativaModel.getCadastroPorGithub());
            setIntOrNull(ps, 15, perfilProjetoDeEstimativaModel.getCadastroPorConviteUsuario());
            setIntOrNull(ps, 16, perfilProjetoDeEstimativaModel.getCadastroPorContasMultitenant());
            setIntOrNull(ps, 17, perfilProjetoDeEstimativaModel.getCadastroPorSubdominios());
            setIntOrNull(ps, 18, perfilProjetoDeEstimativaModel.getCadastroPorDominiosPersonalizados());
            setIntOrNull(ps, 19, perfilProjetoDeEstimativaModel.getPainel());
            setIntOrNull(ps, 20, perfilProjetoDeEstimativaModel.getFeedDeAtividades());
            setIntOrNull(ps, 21, perfilProjetoDeEstimativaModel.getUploadDeArquivos());
            setIntOrNull(ps, 22, perfilProjetoDeEstimativaModel.getUploadDeMidia());
            setIntOrNull(ps, 23, perfilProjetoDeEstimativaModel.getPerfisDeUsuario());
            setIntOrNull(ps, 24, perfilProjetoDeEstimativaModel.getEmailsTransacionais());
            setIntOrNull(ps, 25, perfilProjetoDeEstimativaModel.getTags());
            setIntOrNull(ps, 26, perfilProjetoDeEstimativaModel.getAvaliacoesOuComentarios());
            setIntOrNull(ps, 27, perfilProjetoDeEstimativaModel.getProcessamentoAudioVideo());
            setIntOrNull(ps, 28, perfilProjetoDeEstimativaModel.getPesquisaTextoLivre());
            setIntOrNull(ps, 29, perfilProjetoDeEstimativaModel.getPesquisa());
            setIntOrNull(ps, 30, perfilProjetoDeEstimativaModel.getCalendario());
            setIntOrNull(ps, 31, perfilProjetoDeEstimativaModel.getExibicaoDadosMapaGeolocalizacao());
            setIntOrNull(ps, 32, perfilProjetoDeEstimativaModel.getExibicaoMarcadoresRegioesMapaPersonalizados());
            setIntOrNull(ps, 33, perfilProjetoDeEstimativaModel.getReservas());
            setIntOrNull(ps, 34, perfilProjetoDeEstimativaModel.getMensagens());
            setIntOrNull(ps, 35, perfilProjetoDeEstimativaModel.getForunsOuComentarios());
            setIntOrNull(ps, 36, perfilProjetoDeEstimativaModel.getCompartilhamentoSocial());
            setIntOrNull(ps, 37, perfilProjetoDeEstimativaModel.getIntegracaoFacebookOpenGraph());
            setIntOrNull(ps, 38, perfilProjetoDeEstimativaModel.getNotificacaoPush());
            setIntOrNull(ps, 39, perfilProjetoDeEstimativaModel.getPlanosDeAssinatura());
            setIntOrNull(ps, 40, perfilProjetoDeEstimativaModel.getProcessamentoDePagamento());
            setIntOrNull(ps, 41, perfilProjetoDeEstimativaModel.getCarrinhoDeCompras());
            setIntOrNull(ps, 42, perfilProjetoDeEstimativaModel.getMarketplaceDeUsuarios());
            setIntOrNull(ps, 43, perfilProjetoDeEstimativaModel.getGerenciamentoDeProdutos());
            setIntOrNull(ps, 44, perfilProjetoDeEstimativaModel.getComprasDentroDoAplicativo());
            setIntOrNull(ps, 45, perfilProjetoDeEstimativaModel.getColetaInformacaoPagamento());
            setIntOrNull(ps, 46, perfilProjetoDeEstimativaModel.getIntegracaoCms());
            setIntOrNull(ps, 47, perfilProjetoDeEstimativaModel.getPaginasAdministracaoUsuarios());
            setIntOrNull(ps, 48, perfilProjetoDeEstimativaModel.getModeracaoAprovacaoConteudo());
            setIntOrNull(ps, 49, perfilProjetoDeEstimativaModel.getIntercom());
            setIntOrNull(ps, 50, perfilProjetoDeEstimativaModel.getAnalisesUso());
            setIntOrNull(ps, 51, perfilProjetoDeEstimativaModel.getRelatoriosErro());
            setIntOrNull(ps, 52, perfilProjetoDeEstimativaModel.getMonitoramentoPerformance());
            setIntOrNull(ps, 53, perfilProjetoDeEstimativaModel.getSuporteMultilingue());
            setIntOrNull(ps, 54, perfilProjetoDeEstimativaModel.getConectarServicosDeTerceiros());
            setIntOrNull(ps, 55, perfilProjetoDeEstimativaModel.getApiParaTerceiros());
            setIntOrNull(ps, 56, perfilProjetoDeEstimativaModel.getEnvioSms());
            setIntOrNull(ps, 57, perfilProjetoDeEstimativaModel.getMascaramentoNumeroTelefone());
            setIntOrNull(ps, 58, perfilProjetoDeEstimativaModel.getSegurancaBaseadaCertificadoSsl());
            setIntOrNull(ps, 59, perfilProjetoDeEstimativaModel.getProtecaoContraDos());
            setIntOrNull(ps, 60, perfilProjetoDeEstimativaModel.getAutenticacaoDuasEtapas());
            setIntOrNull(ps, 61, perfilProjetoDeEstimativaModel.getDesenvolvimentoEspecificoApp());
            setIntOrNull(ps, 62, perfilProjetoDeEstimativaModel.getDesignIconeApp());
            setIntOrNull(ps, 63, perfilProjetoDeEstimativaModel.getSincronizacaoNuvem());
            setIntOrNull(ps, 64, perfilProjetoDeEstimativaModel.getDadosSensoresDispositivo());
            setIntOrNull(ps, 65, perfilProjetoDeEstimativaModel.getCodigoBarraQrCode());
            setIntOrNull(ps, 66, perfilProjetoDeEstimativaModel.getDadosSaude());
            setIntOrNull(ps, 67, perfilProjetoDeEstimativaModel.getAppleWatch());
            setIntOrNull(ps, 68, perfilProjetoDeEstimativaModel.getGerenteDeProjetos());
            setDoubleOrNull(ps, 69, perfilProjetoDeEstimativaModel.getTaxaDiariaDesign());
            setDoubleOrNull(ps, 70, perfilProjetoDeEstimativaModel.getTaxaDiariaGerenciaProjeto());
            setDoubleOrNull(ps, 71, perfilProjetoDeEstimativaModel.getTaxaDiariaDesenvolvimento());
            ps.setDate(72, perfilProjetoDeEstimativaModel.getDataCriacao());
            ps.setInt(73, perfilProjetoDeEstimativaModel.getId());

            ps.executeUpdate();
            
            perfisProjetoDeEstimativaModel.add(perfilProjetoDeEstimativaModel);
            notifyObservers();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
        }
    }

    
    @Override
    public boolean deleteById(Integer id) {
        PreparedStatement ps = null;
        PreparedStatement stfkON = null;
        try {
            stfkON = conn.prepareStatement("PRAGMA foreign_keys = ON;");
            stfkON.execute();
            ps = conn.prepareStatement("DELETE FROM perfil_projeto_estimativa WHERE id=?");
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            
            if(rowsAffected > 0){ 
                perfisProjetoDeEstimativaModel.removeIf(item -> item.getId().equals(id));
                notifyObservers();
                return true;
            }else{
                throw new DbException("Unexpected error! No rows affected!");
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeStatement(stfkON);            
        }
    }

    private UsuarioModel instantiateUsuarioModel(ResultSet rs) throws SQLException {
        UsuarioModel usuarioModel = new UsuarioModel(rs.getInt("user_id"), rs.getString("nome"), rs.getString("senha"), rs.getString("email"), rs.getString("formato_log"));
        return usuarioModel;
    }

    private PerfilProjetoDeEstimativaModel instantiatePerfilProjetoDeEstimativaModel(ResultSet rs, UsuarioModel usuarioModel) throws SQLException {
        PerfilProjetoDeEstimativaModel perfilProjetoDeEstimativaModel = new PerfilProjetoDeEstimativaModel();
        perfilProjetoDeEstimativaModel.setId(rs.getInt("id"));
        perfilProjetoDeEstimativaModel.setNomePerfil(rs.getString("nome_perfil"));
        perfilProjetoDeEstimativaModel.setUsuarioModel(usuarioModel);
        perfilProjetoDeEstimativaModel.setPequeno(rs.getInt("pequeno"));
        perfilProjetoDeEstimativaModel.setMedio(rs.getInt("medio"));
        perfilProjetoDeEstimativaModel.setGrande(rs.getInt("grande"));
        perfilProjetoDeEstimativaModel.setMvp(rs.getInt("mvp"));
        perfilProjetoDeEstimativaModel.setBasico(rs.getInt("basico"));
        perfilProjetoDeEstimativaModel.setProfissional(rs.getInt("profissional"));
        perfilProjetoDeEstimativaModel.setCadastroPorEmailSenha(rs.getInt("cadastro_por_email_senha"));
        perfilProjetoDeEstimativaModel.setCadastroPorFacebook(rs.getInt("cadastro_por_facebook"));
        perfilProjetoDeEstimativaModel.setCadastroPorTwitter(rs.getInt("cadastro_por_twitter"));
        perfilProjetoDeEstimativaModel.setCadastroPorGoogle(rs.getInt("cadastro_por_google"));
        perfilProjetoDeEstimativaModel.setCadastroPorLinkedin(rs.getInt("cadastro_por_linkedin"));
        perfilProjetoDeEstimativaModel.setCadastroPorGithub(rs.getInt("cadastro_por_github"));
        perfilProjetoDeEstimativaModel.setCadastroPorConviteUsuario(rs.getInt("cadastro_por_convite_usuario"));
        perfilProjetoDeEstimativaModel.setCadastroPorContasMultitenant(rs.getInt("cadastro_por_contas_multitenant"));
        perfilProjetoDeEstimativaModel.setCadastroPorSubdominios(rs.getInt("cadastro_por_subdominios"));
        perfilProjetoDeEstimativaModel.setCadastroPorDominiosPersonalizados(rs.getInt("cadastro_por_dominios_personalizados"));
        perfilProjetoDeEstimativaModel.setPainel(rs.getInt("painel"));
        perfilProjetoDeEstimativaModel.setFeedDeAtividades(rs.getInt("feed_de_atividades"));
        perfilProjetoDeEstimativaModel.setUploadDeArquivos(rs.getInt("upload_de_arquivos"));
        perfilProjetoDeEstimativaModel.setUploadDeMidia(rs.getInt("upload_de_midia"));
        perfilProjetoDeEstimativaModel.setPerfisDeUsuario(rs.getInt("perfis_de_usuario"));
        perfilProjetoDeEstimativaModel.setEmailsTransacionais(rs.getInt("emails_transacionais"));
        perfilProjetoDeEstimativaModel.setTags(rs.getInt("tags"));
        perfilProjetoDeEstimativaModel.setAvaliacoesOuComentarios(rs.getInt("avaliacoes_ou_comentarios"));
        perfilProjetoDeEstimativaModel.setProcessamentoAudioVideo(rs.getInt("processamento_audio_video"));
        perfilProjetoDeEstimativaModel.setPesquisaTextoLivre(rs.getInt("pesquisa_texto_livre"));
        perfilProjetoDeEstimativaModel.setPesquisa(rs.getInt("pesquisa"));
        perfilProjetoDeEstimativaModel.setCalendario(rs.getInt("calendario"));
        perfilProjetoDeEstimativaModel.setExibicaoDadosMapaGeolocalizacao(rs.getInt("exibicao_dados_mapa_geolocalizacao"));
        perfilProjetoDeEstimativaModel.setExibicaoMarcadoresRegioesMapaPersonalizados(rs.getInt("exibicao_marcadores_regioes_mapa_personalizados"));
        perfilProjetoDeEstimativaModel.setReservas(rs.getInt("reservas"));
        perfilProjetoDeEstimativaModel.setMensagens(rs.getInt("mensagens"));
        perfilProjetoDeEstimativaModel.setForunsOuComentarios(rs.getInt("foruns_ou_comentarios"));
        perfilProjetoDeEstimativaModel.setCompartilhamentoSocial(rs.getInt("compartilhamento_social"));
        perfilProjetoDeEstimativaModel.setIntegracaoFacebookOpenGraph(rs.getInt("integracao_facebook_open_graph"));
        perfilProjetoDeEstimativaModel.setNotificacaoPush(rs.getInt("notificacao_push"));
        perfilProjetoDeEstimativaModel.setPlanosDeAssinatura(rs.getInt("planos_de_assinatura"));
        perfilProjetoDeEstimativaModel.setProcessamentoDePagamento(rs.getInt("processamento_de_pagamento"));
        perfilProjetoDeEstimativaModel.setCarrinhoDeCompras(rs.getInt("carrinho_de_compras"));
        perfilProjetoDeEstimativaModel.setMarketplaceDeUsuarios(rs.getInt("marketplace_de_usuarios"));
        perfilProjetoDeEstimativaModel.setGerenciamentoDeProdutos(rs.getInt("gerenciamento_de_produtos"));
        perfilProjetoDeEstimativaModel.setComprasDentroDoAplicativo(rs.getInt("compras_dentro_do_aplicativo"));
        perfilProjetoDeEstimativaModel.setColetaInformacaoPagamento(rs.getInt("coleta_informacao_pagamento"));
        perfilProjetoDeEstimativaModel.setIntegracaoCms((rs.getInt("integracao_cms")));
        perfilProjetoDeEstimativaModel.setPaginasAdministracaoUsuarios(rs.getInt("paginas_administracao_usuarios"));
        perfilProjetoDeEstimativaModel.setModeracaoAprovacaoConteudo(rs.getInt("moderacao_aprovacao_conteudo"));
        perfilProjetoDeEstimativaModel.setIntercom(rs.getInt("intercom"));
        perfilProjetoDeEstimativaModel.setAnalisesUso(rs.getInt("analises_uso"));
        perfilProjetoDeEstimativaModel.setRelatoriosErro(rs.getInt("relatorios_erro"));
        perfilProjetoDeEstimativaModel.setMonitoramentoPerformance(rs.getInt("monitoramento_performance"));
        perfilProjetoDeEstimativaModel.setSuporteMultilingue(rs.getInt("suporte_multilingue"));
        perfilProjetoDeEstimativaModel.setConectarServicosDeTerceiros(rs.getInt("conectar_servicos_de_terceiros"));
        perfilProjetoDeEstimativaModel.setApiParaTerceiros(rs.getInt("api_para_terceiros"));
        perfilProjetoDeEstimativaModel.setEnvioSms(rs.getInt("envio_sms"));
        perfilProjetoDeEstimativaModel.setMascaramentoNumeroTelefone(rs.getInt("mascaramento_numero_telefone"));
        perfilProjetoDeEstimativaModel.setSegurancaBaseadaCertificadoSsl(rs.getInt("seguranca_baseada_certificado_ssl"));
        perfilProjetoDeEstimativaModel.setProtecaoContraDos(rs.getInt("protecao_contra_dos"));
        perfilProjetoDeEstimativaModel.setAutenticacaoDuasEtapas(rs.getInt("autenticacao_duas_etapas"));
        perfilProjetoDeEstimativaModel.setDesenvolvimentoEspecificoApp(rs.getInt("desenvolvimento_especifico_app"));
        perfilProjetoDeEstimativaModel.setDesignIconeApp(rs.getInt("design_icone_app"));
        perfilProjetoDeEstimativaModel.setSincronizacaoNuvem(rs.getInt("sincronizacao_nuvem"));
        perfilProjetoDeEstimativaModel.setDadosSensoresDispositivo(rs.getInt("dados_sensores_dispositivo"));
        perfilProjetoDeEstimativaModel.setCodigoBarraQrCode(rs.getInt("codigo_barra_qr_code"));
        perfilProjetoDeEstimativaModel.setDadosSaude(rs.getInt("dados_saude"));
        perfilProjetoDeEstimativaModel.setAppleWatch(rs.getInt("apple_watch"));
        perfilProjetoDeEstimativaModel.setGerenteDeProjetos(rs.getInt("gerente_de_projetos"));
        perfilProjetoDeEstimativaModel.setTaxaDiariaDesign(rs.getDouble("taxa_diaria_design"));
        perfilProjetoDeEstimativaModel.setTaxaDiariaGerenciaProjeto(rs.getDouble("taxa_diaria_gerencia_projeto"));
        perfilProjetoDeEstimativaModel.setTaxaDiariaDesenvolvimento(rs.getDouble("taxa_diaria_desenvolvimento"));
        long timestamp = rs.getLong("data_criacao");
        Date data = new Date(timestamp);
        perfilProjetoDeEstimativaModel.setDataCriacao(data);

        return perfilProjetoDeEstimativaModel;

    }
    
    private void setIntOrNull(PreparedStatement ps, int index, Integer value) throws SQLException{
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }
    
    private void setDoubleOrNull(PreparedStatement ps, int index, Double value) throws SQLException{
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setDouble(index, value);
        }
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            //observer.update(projetos);
            observer.updatePerfilModel(perfisProjetoDeEstimativaModel);
        }
    }
}
