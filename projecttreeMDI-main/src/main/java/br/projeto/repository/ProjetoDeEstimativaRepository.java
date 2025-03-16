package br.projeto.repository;

import br.projeto.repository.abstr.IProjetoDeEstimativaRepository;
import br.projeto.db.DB;
import br.projeto.db.DbException;
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

public class ProjetoDeEstimativaRepository implements Subject, IProjetoDeEstimativaRepository {//TESTAR MUDANÇA NA TIPAGEM DE OUTROS CUSTOS
    //CONSIDERAR COLOCAR TODOS OS METODOS DE INSTANTIATE EM UMA SERVICE
    private Connection conn;
    private List<Projeto> projetos;//VERIFICAR RETIRADA
    private List<Observer> observers;
    private List<ProjetoDeEstimativaModel> projetosDeEstimativaModel;
    
    public ProjetoDeEstimativaRepository(Connection conn) {    
        this.conn = conn;
        observers = new ArrayList<>();
        projetos = new ArrayList<>();//VERIFICAR RETIRADA
        projetosDeEstimativaModel = new ArrayList<>();
    }

    @Override
    public List<ProjetoDeEstimativaModel> findAll() {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT projetos_estimativa.*, usuario.nome, usuario.senha, usuario.email, usuario.formato_log " +
                                           "FROM projetos_estimativa " +
                                           "INNER JOIN usuario ON projetos_estimativa.user_id = usuario.id"
                                      );

            Map<Integer, UsuarioModel> usuarioModelMap = new HashMap<>();
            List<ProjetoDeEstimativaModel> projetoDeEstimativaModelList = new ArrayList<>();
            rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioModel usuario = usuarioModelMap.get(rs.getInt("user_id"));
                if (usuario == null) {
                    usuario = instantiateUsuarioModel(rs);
                    usuarioModelMap.put(usuario.getId(), usuario);
                }
                ProjetoDeEstimativaModel projetoDeEstimativaModel = instantiateProjetoDeEstimativaModel(rs, usuario);
                projetoDeEstimativaModelList.add(projetoDeEstimativaModel);
            }
            return projetoDeEstimativaModelList;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    
    @Override
    public List<ProjetoDeEstimativaModel> findByUser(UsuarioModel usuarioModel) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT projetos_estimativa.*, usuario.nome, usuario.senha, usuario.email, usuario.formato_log "+
                                           "FROM projetos_estimativa " +
                                           "INNER JOIN usuario ON projetos_estimativa.user_id = usuario.id " +
                                           "WHERE usuario.id=?");
            ps.setInt(1, usuarioModel.getId());
            Map<Integer, UsuarioModel> usuarioModelMap = new HashMap<>();
            List<ProjetoDeEstimativaModel> projetoDeEstimativaModelList = new ArrayList<>();
            rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioModel usuario = usuarioModelMap.get(rs.getInt("user_id"));
                if (usuario == null) {
                    usuario = instantiateUsuarioModel(rs);
                    usuarioModelMap.put(usuario.getId(), usuario);
                }
                ProjetoDeEstimativaModel projetoDeEstimativaModel = instantiateProjetoDeEstimativaModel(rs, usuario);
                projetoDeEstimativaModelList.add(projetoDeEstimativaModel);
            }
            return projetoDeEstimativaModelList;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }

    
    @Override
    public ProjetoDeEstimativaModel findById(Integer id) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT projetos_estimativa.*, usuario.nome, usuario.senha, usuario.email, usuario.formato_log FROM projetos_estimativa " +
                                           "INNER JOIN usuario ON projetos_estimativa.user_id = usuario.id " +
                                           "WHERE projetos_estimativa.id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                UsuarioModel usuarioModel = instantiateUsuarioModel(rs);
                ProjetoDeEstimativaModel projetoDeEstimativaModel = instantiateProjetoDeEstimativaModel(rs, usuarioModel);
                //projetosDeEstimativaModel.add(projetoDeEstimativaModel);
                //notifyObservers();//VERIFICAR SEM FUNCIONA SEM
                return projetoDeEstimativaModel;
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
    public void insert(ProjetoDeEstimativaModel projetoDeEstimativaModel) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("INSERT INTO projetos_estimativa(user_id, compartilhado, compartilhado_por, data_criacao, "+
                    "nome_projeto_estimativa, pequeno, medio, grande, " +
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
                    "custo_hardware, custo_software, custo_riscos, custo_garantia, " +
                    "fundo_de_reserva, outros_custos, percentual_com_impostos, " +
                    "percentual_lucro_desejado)" +
                    "VALUES( ?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, " +
                            "?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, " +
                            "?, ?, ?, ?, " +
                            "?, ?, ?, ?, " +
                            "?, ?, ?, ?, " +
                            "?, ?, ?, ?, " +
                            "?, ?, ?, ?, " +
                            "?, ?, ?, ?, "+
                            "?, ?, ?, ?, "+
                            "?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, projetoDeEstimativaModel.getUsuarioModel().getId());
            ps.setInt(2, projetoDeEstimativaModel.getCompartilhadoValor());
//            if (projetoDeEstimativaModel.getCompartilhadoPor() == null) {
//                ps.setNull(3, java.sql.Types.INTEGER); 
//            } else {
//                ps.setInt(3, projetoDeEstimativaModel.getCompartilhadoPor());
//            }
            setIntOrNull(ps, 3, projetoDeEstimativaModel.getCompartilhadoPor());
            ps.setDate(4, projetoDeEstimativaModel.getDataCriacao());
            ps.setString(5, projetoDeEstimativaModel.getNomeProjetoDeEstimativa());
            setIntOrNull(ps, 6, projetoDeEstimativaModel.getPequenoValor());
            setIntOrNull(ps, 7, projetoDeEstimativaModel.getMedioValor());
            setIntOrNull(ps, 8, projetoDeEstimativaModel.getGrandeValor());
            setIntOrNull(ps, 9, projetoDeEstimativaModel.getMvpValor());
            setIntOrNull(ps, 10, projetoDeEstimativaModel.getBasicoValor());
            setIntOrNull(ps, 11, projetoDeEstimativaModel.getProfissionalValor());
            setIntOrNull(ps, 12, projetoDeEstimativaModel.getCadastroPorEmailSenhaValor());
            setIntOrNull(ps, 13, projetoDeEstimativaModel.getCadastroPorFacebookValor());
            setIntOrNull(ps, 14, projetoDeEstimativaModel.getCadastroPorTwitterValor());
            setIntOrNull(ps, 15, projetoDeEstimativaModel.getCadastroPorGoogleValor());
            setIntOrNull(ps, 16, projetoDeEstimativaModel.getCadastroPorLinkedinValor());
            setIntOrNull(ps, 17, projetoDeEstimativaModel.getCadastroPorGithubValor());
            setIntOrNull(ps, 18, projetoDeEstimativaModel.getCadastroPorConviteUsuarioValor());
            setIntOrNull(ps, 19, projetoDeEstimativaModel.getCadastroPorContasMultitenantValor());
            setIntOrNull(ps, 20, projetoDeEstimativaModel.getCadastroPorSubdominiosValor());
            setIntOrNull(ps, 21, projetoDeEstimativaModel.getCadastroPorDominiosPersonalizadosValor());
            setIntOrNull(ps, 22, projetoDeEstimativaModel.getPainelValor());
            setIntOrNull(ps, 23, projetoDeEstimativaModel.getFeedDeAtividadesValor());
            setIntOrNull(ps, 24, projetoDeEstimativaModel.getUploadDeArquivosValor());
            setIntOrNull(ps, 25, projetoDeEstimativaModel.getUploadDeMidiaValor());
            setIntOrNull(ps, 26, projetoDeEstimativaModel.getPerfisDeUsuarioValor());
            setIntOrNull(ps, 27, projetoDeEstimativaModel.getEmailsTransacionaisValor());
            setIntOrNull(ps, 28, projetoDeEstimativaModel.getTagsValor());
            setIntOrNull(ps, 29, projetoDeEstimativaModel.getAvaliacoesOuComentariosValor());
            setIntOrNull(ps, 30, projetoDeEstimativaModel.getProcessamentoAudioVideoValor());
            setIntOrNull(ps, 31, projetoDeEstimativaModel.getPesquisaTextoLivreValor());
            setIntOrNull(ps, 32, projetoDeEstimativaModel.getPesquisaValor());
            setIntOrNull(ps, 33, projetoDeEstimativaModel.getCalendarioValor());
            setIntOrNull(ps, 34, projetoDeEstimativaModel.getExibicaoDadosMapaGeolocalizacaoValor());
            setIntOrNull(ps, 35, projetoDeEstimativaModel.getExibicaoMarcadoresRegioesMapaPersonalizadosValor());
            setIntOrNull(ps, 36, projetoDeEstimativaModel.getReservasValor());
            setIntOrNull(ps, 37, projetoDeEstimativaModel.getMensagensValor());
            setIntOrNull(ps, 38, projetoDeEstimativaModel.getForunsOuComentariosValor());
            setIntOrNull(ps, 39, projetoDeEstimativaModel.getCompartilhamentoSocialValor());
            setIntOrNull(ps, 40, projetoDeEstimativaModel.getIntegracaoFacebookOpenGraphValor());
            setIntOrNull(ps, 41, projetoDeEstimativaModel.getNotificacaoPushValor());
            setIntOrNull(ps, 42, projetoDeEstimativaModel.getPlanosDeAssinaturaValor());
            setIntOrNull(ps, 43, projetoDeEstimativaModel.getProcessamentoDePagamentoValor());
            setIntOrNull(ps, 44, projetoDeEstimativaModel.getCarrinhoDeComprasValor());
            setIntOrNull(ps, 45, projetoDeEstimativaModel.getMarketplaceDeUsuariosValor());
            setIntOrNull(ps, 46, projetoDeEstimativaModel.getGerenciamentoDeProdutosValor());
            setIntOrNull(ps, 47, projetoDeEstimativaModel.getComprasDentroDoAplicativoValor());
            setIntOrNull(ps, 48, projetoDeEstimativaModel.getColetaInformacaoPagamentoValor());
            setIntOrNull(ps, 49, projetoDeEstimativaModel.getIntegracaoCmsValor());
            setIntOrNull(ps, 50, projetoDeEstimativaModel.getPaginasAdministracaoUsuariosValor());
            setIntOrNull(ps, 51, projetoDeEstimativaModel.getModeracaoAprovacaoConteudoValor());
            setIntOrNull(ps, 52, projetoDeEstimativaModel.getIntercomValor());
            setIntOrNull(ps, 53, projetoDeEstimativaModel.getAnalisesUsoValor());
            setIntOrNull(ps, 54, projetoDeEstimativaModel.getRelatoriosErroValor());
            setIntOrNull(ps, 55, projetoDeEstimativaModel.getMonitoramentoPerformanceValor());
            setIntOrNull(ps, 56, projetoDeEstimativaModel.getSuporteMultilingueValor());
            setIntOrNull(ps, 57, projetoDeEstimativaModel.getConectarServicosDeTerceirosValor());
            setIntOrNull(ps, 58, projetoDeEstimativaModel.getApiParaTerceirosValor());
            setIntOrNull(ps, 59, projetoDeEstimativaModel.getEnvioSmsValor());
            setIntOrNull(ps, 60, projetoDeEstimativaModel.getMascaramentoNumeroTelefoneValor());
            setIntOrNull(ps, 61, projetoDeEstimativaModel.getSegurancaBaseadaCertificadoSslValor());
            setIntOrNull(ps, 62, projetoDeEstimativaModel.getProtecaoContraDosValor());
            setIntOrNull(ps, 63, projetoDeEstimativaModel.getAutenticacaoDuasEtapasValor());
            setIntOrNull(ps, 64, projetoDeEstimativaModel.getDesenvolvimentoEspecificoAppValor());
            setIntOrNull(ps, 65, projetoDeEstimativaModel.getDesignIconeAppValor());
            setIntOrNull(ps, 66, projetoDeEstimativaModel.getSincronizacaoNuvemValor());
            setIntOrNull(ps, 67, projetoDeEstimativaModel.getDadosSensoresDispositivoValor());
            setIntOrNull(ps, 68, projetoDeEstimativaModel.getCodigoBarraQrCodeValor());
            setIntOrNull(ps, 69, projetoDeEstimativaModel.getDadosSaudeValor());
            setIntOrNull(ps, 70, projetoDeEstimativaModel.getAppleWatchValor());
            setIntOrNull(ps, 71, projetoDeEstimativaModel.getGerenteDeProjetosValor());
            setDoubleOrNull(ps, 72, projetoDeEstimativaModel.getCustoHardware());
            setDoubleOrNull(ps, 73, projetoDeEstimativaModel.getCustoSoftware());
            setDoubleOrNull(ps, 74, projetoDeEstimativaModel.getCustoRiscos());
            setDoubleOrNull(ps, 75, projetoDeEstimativaModel.getCustoGarantia());
            setDoubleOrNull(ps, 76, projetoDeEstimativaModel.getFundoDeReserva());
            setDoubleOrNull(ps, 77, projetoDeEstimativaModel.getOutrosCustos());
            /*ps.setDouble(79, projetoDeEstimativaModel.getSubTotal());*/
            ps.setDouble(78, projetoDeEstimativaModel.getPercentualComImpostos());
            /*ps.setDouble(81, projetoDeEstimativaModel.getTotalComImposto());*/
            ps.setDouble(79, projetoDeEstimativaModel.getPercentualLucroDesejado());
            /*ps.setDouble(83, projetoDeEstimativaModel.getLucroCalculado());
            ps.setInt(84, projetoDeEstimativaModel.getDias());
            ps.setDouble(85, projetoDeEstimativaModel.getMeses());
            ps.setDouble(86, projetoDeEstimativaModel.getPrecoFinalCliente());
            ps.setDouble(87, projetoDeEstimativaModel.getMediaPorMes());*/


            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    projetoDeEstimativaModel.setId(rs.getInt(1));
                    projetosDeEstimativaModel.add(projetoDeEstimativaModel);
                    notifyObservers();
                } else {
                    throw new DbException("Unexpected error! No rows affected!");
                }
            }
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(ps);
            DB.closeResultSet(rs);
        }
    }
    
    

    @Override
    public void update(ProjetoDeEstimativaModel projetoDeEstimativaModel) {
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(
                    "UPDATE projetos_estimativa SET " +
                            "user_id = ?, compartilhado = ?, compartilhado_por = ?, data_criacao = ?, nome_projeto_estimativa = ?, " +
                            "pequeno = ?, medio = ?, grande = ?, mvp = ?, basico = ?, profissional = ?, cadastro_por_email_senha = ?, " +
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
                            "custo_hardware = ?, custo_software = ?, custo_riscos = ?, custo_garantia = ?, " +
                            "fundo_de_reserva = ?, outros_custos = ?, percentual_com_impostos = ?, " +
                            "percentual_lucro_desejado = ? " +
                            "WHERE id = ?"
            );
            ps.setInt(1, projetoDeEstimativaModel.getUsuarioModel().getId());
            ps.setInt(2, projetoDeEstimativaModel.getCompartilhadoValor());
            setIntOrNull(ps, 3, projetoDeEstimativaModel.getCompartilhadoPor());
            ps.setDate(4, projetoDeEstimativaModel.getDataCriacao());
            ps.setString(5, projetoDeEstimativaModel.getNomeProjetoDeEstimativa());
            setIntOrNull(ps,6, projetoDeEstimativaModel.getPequenoValor());
            setIntOrNull(ps,7, projetoDeEstimativaModel.getMedioValor());
            setIntOrNull(ps,8, projetoDeEstimativaModel.getGrandeValor());
            setIntOrNull(ps,9, projetoDeEstimativaModel.getMvpValor());
            setIntOrNull(ps,10, projetoDeEstimativaModel.getBasicoValor());
            setIntOrNull(ps,11, projetoDeEstimativaModel.getProfissionalValor());
            setIntOrNull(ps,12, projetoDeEstimativaModel.getCadastroPorEmailSenhaValor());
            setIntOrNull(ps,13, projetoDeEstimativaModel.getCadastroPorFacebookValor());
            setIntOrNull(ps,14, projetoDeEstimativaModel.getCadastroPorTwitterValor());
            setIntOrNull(ps,15, projetoDeEstimativaModel.getCadastroPorGoogleValor());
            setIntOrNull(ps, 16, projetoDeEstimativaModel.getCadastroPorLinkedinValor());
            setIntOrNull(ps,17, projetoDeEstimativaModel.getCadastroPorGithubValor());
            setIntOrNull(ps, 18, projetoDeEstimativaModel.getCadastroPorConviteUsuarioValor());
            setIntOrNull(ps,19, projetoDeEstimativaModel.getCadastroPorContasMultitenantValor());
            setIntOrNull(ps,20, projetoDeEstimativaModel.getCadastroPorSubdominiosValor());
            setIntOrNull(ps,21, projetoDeEstimativaModel.getCadastroPorDominiosPersonalizadosValor());
            setIntOrNull(ps,22, projetoDeEstimativaModel.getPainelValor());
            setIntOrNull(ps,23, projetoDeEstimativaModel.getFeedDeAtividadesValor());
            setIntOrNull(ps,24, projetoDeEstimativaModel.getUploadDeArquivosValor());
            setIntOrNull(ps,25, projetoDeEstimativaModel.getUploadDeMidiaValor());
            setIntOrNull(ps,26, projetoDeEstimativaModel.getPerfisDeUsuarioValor());
            setIntOrNull(ps,27, projetoDeEstimativaModel.getEmailsTransacionaisValor());
            setIntOrNull(ps,28, projetoDeEstimativaModel.getTagsValor());
            setIntOrNull(ps,29, projetoDeEstimativaModel.getAvaliacoesOuComentariosValor());
            setIntOrNull(ps,30, projetoDeEstimativaModel.getProcessamentoAudioVideoValor());
            setIntOrNull(ps,31, projetoDeEstimativaModel.getPesquisaTextoLivreValor());
            setIntOrNull(ps,32, projetoDeEstimativaModel.getPesquisaValor());
            setIntOrNull(ps,33, projetoDeEstimativaModel.getCalendarioValor());
            setIntOrNull(ps,34, projetoDeEstimativaModel.getExibicaoDadosMapaGeolocalizacaoValor());
            setIntOrNull(ps,35, projetoDeEstimativaModel.getExibicaoMarcadoresRegioesMapaPersonalizadosValor());
            setIntOrNull(ps,36, projetoDeEstimativaModel.getReservasValor());
            setIntOrNull(ps,37, projetoDeEstimativaModel.getMensagensValor());
            setIntOrNull(ps,38, projetoDeEstimativaModel.getForunsOuComentariosValor());
            setIntOrNull(ps,39, projetoDeEstimativaModel.getCompartilhamentoSocialValor());
            setIntOrNull(ps,40, projetoDeEstimativaModel.getIntegracaoFacebookOpenGraphValor());
            setIntOrNull(ps,41, projetoDeEstimativaModel.getNotificacaoPushValor());
            setIntOrNull(ps,42, projetoDeEstimativaModel.getPlanosDeAssinaturaValor());
            setIntOrNull(ps,43, projetoDeEstimativaModel.getProcessamentoDePagamentoValor());
            setIntOrNull(ps,44, projetoDeEstimativaModel.getCarrinhoDeComprasValor());
            setIntOrNull(ps,45, projetoDeEstimativaModel.getMarketplaceDeUsuariosValor());
            setIntOrNull(ps,46, projetoDeEstimativaModel.getGerenciamentoDeProdutosValor());
            setIntOrNull(ps,47, projetoDeEstimativaModel.getComprasDentroDoAplicativoValor());
            setIntOrNull(ps,48, projetoDeEstimativaModel.getColetaInformacaoPagamentoValor());
            setIntOrNull(ps,49, projetoDeEstimativaModel.getIntegracaoCmsValor());
            setIntOrNull(ps,50, projetoDeEstimativaModel.getPaginasAdministracaoUsuariosValor());
            setIntOrNull(ps,51, projetoDeEstimativaModel.getModeracaoAprovacaoConteudoValor());
            setIntOrNull(ps,52, projetoDeEstimativaModel.getIntercomValor());
            setIntOrNull(ps,53, projetoDeEstimativaModel.getAnalisesUsoValor());
            setIntOrNull(ps,54, projetoDeEstimativaModel.getRelatoriosErroValor());
            setIntOrNull(ps,55, projetoDeEstimativaModel.getMonitoramentoPerformanceValor());
            setIntOrNull(ps,56, projetoDeEstimativaModel.getSuporteMultilingueValor());
            setIntOrNull(ps,57, projetoDeEstimativaModel.getConectarServicosDeTerceirosValor());
            setIntOrNull(ps,58, projetoDeEstimativaModel.getApiParaTerceirosValor());
            setIntOrNull(ps,59, projetoDeEstimativaModel.getEnvioSmsValor());
            setIntOrNull(ps,60, projetoDeEstimativaModel.getMascaramentoNumeroTelefoneValor());
            setIntOrNull(ps,61, projetoDeEstimativaModel.getSegurancaBaseadaCertificadoSslValor());
            setIntOrNull(ps,62, projetoDeEstimativaModel.getProtecaoContraDosValor());
            setIntOrNull(ps,63, projetoDeEstimativaModel.getAutenticacaoDuasEtapasValor());
            setIntOrNull(ps,64, projetoDeEstimativaModel.getDesenvolvimentoEspecificoAppValor());
            setIntOrNull(ps,65, projetoDeEstimativaModel.getDesignIconeAppValor());
            setIntOrNull(ps,66, projetoDeEstimativaModel.getSincronizacaoNuvemValor());
            setIntOrNull(ps,67, projetoDeEstimativaModel.getDadosSensoresDispositivoValor());
            setIntOrNull(ps,68, projetoDeEstimativaModel.getCodigoBarraQrCodeValor());
            setIntOrNull(ps,69, projetoDeEstimativaModel.getDadosSaudeValor());
            setIntOrNull(ps,70, projetoDeEstimativaModel.getAppleWatchValor());
            setIntOrNull(ps,71, projetoDeEstimativaModel.getGerenteDeProjetosValor());
            setDoubleOrNull(ps,72, projetoDeEstimativaModel.getCustoHardware());
            setDoubleOrNull(ps,73, projetoDeEstimativaModel.getCustoSoftware());
            setDoubleOrNull(ps,74, projetoDeEstimativaModel.getCustoRiscos());
            setDoubleOrNull(ps,75, projetoDeEstimativaModel.getCustoGarantia());
            setDoubleOrNull(ps,76, projetoDeEstimativaModel.getFundoDeReserva());
            setDoubleOrNull(ps,77, projetoDeEstimativaModel.getOutrosCustos());
            setDoubleOrNull(ps,78, projetoDeEstimativaModel.getPercentualComImpostos());
            setDoubleOrNull(ps,79, projetoDeEstimativaModel.getPercentualLucroDesejado());
            ps.setInt(80, projetoDeEstimativaModel.getId());

            projetosDeEstimativaModel.add(projetoDeEstimativaModel);
            ps.executeUpdate();
            
            notifyObservers();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(ps);
        }
    }
    

    
    @Override
    public boolean deleteById(Integer id) {
        PreparedStatement stfkON = null;
        PreparedStatement ps = null;
        try {
            stfkON = conn.prepareStatement("PRAGMA foreign_keys = ON;");
            stfkON.execute();
            ps = conn.prepareStatement("DELETE FROM projetos_estimativa WHERE id=?");
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            
            if(rowsAffected > 0){ 
                projetosDeEstimativaModel.removeIf(item -> item.getId().equals(id));
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
        UsuarioModel usuarioModel = new UsuarioModel(rs.getInt("user_id"), rs.getString("nome"), rs.getString("senha"), rs.getString("email"),rs.getString("formato_log"));
        return usuarioModel;
    }

    private ProjetoDeEstimativaModel instantiateProjetoDeEstimativaModel(ResultSet rs, UsuarioModel usuarioModel) throws SQLException {
        ProjetoDeEstimativaModel projetoDeEstimativaModel = new ProjetoDeEstimativaModel();
        projetoDeEstimativaModel.setId(rs.getInt("id"));
        projetoDeEstimativaModel.setUsuarioModel(usuarioModel);
        projetoDeEstimativaModel.setCompartilhado(rs.getInt("compartilhado"));
        projetoDeEstimativaModel.setCompartilhadoPor(rs.getInt("compartilhado_por"));
        long timestamp = rs.getLong("data_criacao");
        Date data = new Date(timestamp);
        projetoDeEstimativaModel.setDataCriacao(data);
        projetoDeEstimativaModel.setNomeProjetoDeEstimativa(rs.getString("nome_projeto_estimativa"));
        projetoDeEstimativaModel.setPequeno(rs.getInt("pequeno"));
        projetoDeEstimativaModel.setMedio(rs.getInt("medio"));
        projetoDeEstimativaModel.setGrande(rs.getInt("grande"));
        projetoDeEstimativaModel.setMvp(rs.getInt("mvp"));
        projetoDeEstimativaModel.setBasico(rs.getInt("basico"));
        projetoDeEstimativaModel.setProfissional(rs.getInt("profissional"));
        projetoDeEstimativaModel.setCadastroPorEmailSenha(rs.getInt("cadastro_por_email_senha"));
        projetoDeEstimativaModel.setCadastroPorFacebook(rs.getInt("cadastro_por_facebook"));
        projetoDeEstimativaModel.setCadastroPorTwitter(rs.getInt("cadastro_por_twitter"));
        projetoDeEstimativaModel.setCadastroPorGoogle(rs.getInt("cadastro_por_google"));
        projetoDeEstimativaModel.setCadastroPorLinkedin(rs.getInt("cadastro_por_linkedin"));
        projetoDeEstimativaModel.setCadastroPorGithub(rs.getInt("cadastro_por_github"));
        projetoDeEstimativaModel.setCadastroPorConviteUsuario(rs.getInt("cadastro_por_convite_usuario"));
        projetoDeEstimativaModel.setCadastroPorContasMultitenant(rs.getInt("cadastro_por_contas_multitenant"));
        projetoDeEstimativaModel.setCadastroPorSubdominios(rs.getInt("cadastro_por_subdominios"));
        projetoDeEstimativaModel.setCadastroPorDominiosPersonalizados(rs.getInt("cadastro_por_dominios_personalizados"));
        projetoDeEstimativaModel.setPainel(rs.getInt("painel"));
        projetoDeEstimativaModel.setFeedDeAtividades(rs.getInt("feed_de_atividades"));
        projetoDeEstimativaModel.setUploadDeArquivos(rs.getInt("upload_de_arquivos"));
        projetoDeEstimativaModel.setUploadDeMidia(rs.getInt("upload_de_midia"));
        projetoDeEstimativaModel.setPerfisDeUsuario(rs.getInt("perfis_de_usuario"));
        projetoDeEstimativaModel.setEmailsTransacionais(rs.getInt("emails_transacionais"));
        projetoDeEstimativaModel.setTags(rs.getInt("tags"));
        projetoDeEstimativaModel.setAvaliacoesOuComentarios(rs.getInt("avaliacoes_ou_comentarios"));
        projetoDeEstimativaModel.setProcessamentoAudioVideo(rs.getInt("processamento_audio_video"));
        projetoDeEstimativaModel.setPesquisaTextoLivre(rs.getInt("pesquisa_texto_livre"));
        projetoDeEstimativaModel.setPesquisa(rs.getInt("pesquisa"));
        projetoDeEstimativaModel.setCalendario(rs.getInt("calendario"));
        projetoDeEstimativaModel.setExibicaoDadosMapaGeolocalizacao(rs.getInt("exibicao_dados_mapa_geolocalizacao"));
        projetoDeEstimativaModel.setExibicaoMarcadoresRegioesMapaPersonalizados(rs.getInt("exibicao_marcadores_regioes_mapa_personalizados"));
        projetoDeEstimativaModel.setReservas(rs.getInt("reservas"));
        projetoDeEstimativaModel.setMensagens(rs.getInt("mensagens"));
        projetoDeEstimativaModel.setForunsOuComentarios(rs.getInt("foruns_ou_comentarios"));
        projetoDeEstimativaModel.setCompartilhamentoSocial(rs.getInt("compartilhamento_social"));
        projetoDeEstimativaModel.setIntegracaoFacebookOpenGraph(rs.getInt("integracao_facebook_open_graph"));
        projetoDeEstimativaModel.setNotificacaoPush(rs.getInt("notificacao_push"));
        projetoDeEstimativaModel.setPlanosDeAssinatura(rs.getInt("planos_de_assinatura"));
        projetoDeEstimativaModel.setProcessamentoDePagamento(rs.getInt("processamento_de_pagamento"));
        projetoDeEstimativaModel.setCarrinhoDeCompras(rs.getInt("carrinho_de_compras"));
        projetoDeEstimativaModel.setMarketplaceDeUsuarios(rs.getInt("marketplace_de_usuarios"));
        projetoDeEstimativaModel.setGerenciamentoDeProdutos(rs.getInt("gerenciamento_de_produtos"));
        projetoDeEstimativaModel.setComprasDentroDoAplicativo(rs.getInt("compras_dentro_do_aplicativo"));
        projetoDeEstimativaModel.setColetaInformacaoPagamento(rs.getInt("coleta_informacao_pagamento"));
        projetoDeEstimativaModel.setIntegracaoCms((rs.getInt("integracao_cms")));
        projetoDeEstimativaModel.setPaginasAdministracaoUsuarios(rs.getInt("paginas_administracao_usuarios"));
        projetoDeEstimativaModel.setModeracaoAprovacaoConteudo(rs.getInt("moderacao_aprovacao_conteudo"));
        projetoDeEstimativaModel.setIntercom(rs.getInt("intercom"));
        projetoDeEstimativaModel.setAnalisesUso(rs.getInt("analises_uso"));
        projetoDeEstimativaModel.setRelatoriosErro(rs.getInt("relatorios_erro"));
        projetoDeEstimativaModel.setMonitoramentoPerformance(rs.getInt("monitoramento_performance"));
        projetoDeEstimativaModel.setSuporteMultilingue(rs.getInt("suporte_multilingue"));
        projetoDeEstimativaModel.setConectarServicosDeTerceiros(rs.getInt("conectar_servicos_de_terceiros"));
        projetoDeEstimativaModel.setApiParaTerceiros(rs.getInt("api_para_terceiros"));
        projetoDeEstimativaModel.setEnvioSms(rs.getInt("envio_sms"));
        projetoDeEstimativaModel.setMascaramentoNumeroTelefone(rs.getInt("mascaramento_numero_telefone"));
        projetoDeEstimativaModel.setSegurancaBaseadaCertificadoSsl(rs.getInt("seguranca_baseada_certificado_ssl"));
        projetoDeEstimativaModel.setProtecaoContraDos(rs.getInt("protecao_contra_dos"));
        projetoDeEstimativaModel.setAutenticacaoDuasEtapas(rs.getInt("autenticacao_duas_etapas"));
        projetoDeEstimativaModel.setDesenvolvimentoEspecificoApp(rs.getInt("desenvolvimento_especifico_app"));
        projetoDeEstimativaModel.setDesignIconeApp(rs.getInt("design_icone_app"));
        projetoDeEstimativaModel.setSincronizacaoNuvem(rs.getInt("sincronizacao_nuvem"));
        projetoDeEstimativaModel.setDadosSensoresDispositivo(rs.getInt("dados_sensores_dispositivo"));
        projetoDeEstimativaModel.setCodigoBarraQrCode(rs.getInt("codigo_barra_qr_code"));
        projetoDeEstimativaModel.setDadosSaude(rs.getInt("dados_saude"));
        projetoDeEstimativaModel.setAppleWatch(rs.getInt("apple_watch"));
        projetoDeEstimativaModel.setGerenteDeProjetos(rs.getInt("gerente_de_projetos"));
        projetoDeEstimativaModel.setCustoHardware(rs.getDouble("custo_hardware"));
        projetoDeEstimativaModel.setCustoSoftware(rs.getDouble("custo_software"));
        projetoDeEstimativaModel.setCustoRiscos(rs.getDouble("custo_riscos"));
        projetoDeEstimativaModel.setCustoGarantia(rs.getDouble("custo_garantia"));
        projetoDeEstimativaModel.setFundoDeReserva(rs.getDouble("fundo_de_reserva"));
        projetoDeEstimativaModel.setOutrosCustos(rs.getDouble("outros_custos"));
        /*projetoDeEstimativaModel.setSubTotal(rs.getDouble("sub_total"));*/
        projetoDeEstimativaModel.setPercentualComImpostos(rs.getDouble("percentual_com_impostos"));
        /*projetoDeEstimativaModel.setTotalComImposto(rs.getDouble("total_com_imposto"));*/
        projetoDeEstimativaModel.setPercentualLucroDesejado(rs.getDouble("percentual_lucro_desejado"));
        /*projetoDeEstimativaModel.setLucroCalculado(rs.getDouble("lucro_calculado"));
        projetoDeEstimativaModel.setDias(rs.getInt("dias"));
        projetoDeEstimativaModel.setMeses(rs.getDouble("meses"));
        projetoDeEstimativaModel.setPrecoFinalCliente(rs.getDouble("preco_final_cliente"));
        projetoDeEstimativaModel.setMediaPorMes(rs.getDouble("media_por_mes"));*/

        return projetoDeEstimativaModel;
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
            ///observer.update(projetos);
            observer.updateProjetoModel(projetosDeEstimativaModel);
        }
    }


}
