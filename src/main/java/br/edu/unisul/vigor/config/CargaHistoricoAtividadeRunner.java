package br.edu.unisul.vigor.config;

import br.edu.unisul.vigor.dto.RegistroCargaHistorico;
import br.edu.unisul.vigor.model.HistoricoAtividade;
import br.edu.unisul.vigor.service.CargaHistoricoAtividadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Executa uma carga técnica do histórico ao iniciar a aplicação com o perfil
 * {@code carga-historico}. Fora desse perfil, o componente não é criado.
 *
 * <p>Os valores são lidos das variáveis de ambiente CARGA_MEMBRO_ID,
 * CARGA_FUNCIONARIO_ID, CARGA_ATIVIDADE, CARGA_DATA_ATIVIDADE e
 * CARGA_TEMPO_EXECUCAO_MINUTOS. Se a carga for rejeitada, a inicialização é
 * interrompida com a mensagem do problema e nenhum registro é gravado.
 */
@Component
@Profile(CargaHistoricoAtividadeRunner.PERFIL)
public class CargaHistoricoAtividadeRunner implements ApplicationRunner {

    public static final String PERFIL = "carga-historico";

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CargaHistoricoAtividadeRunner.class);

    private final CargaHistoricoAtividadeService cargaService;
    private final Environment environment;

    public CargaHistoricoAtividadeRunner(
            CargaHistoricoAtividadeService cargaService, Environment environment) {
        this.cargaService = cargaService;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments argumentos) {
        RegistroCargaHistorico registro = new RegistroCargaHistorico(
                environment.getProperty("carga.membro.id"),
                environment.getProperty("carga.funcionario.id"),
                environment.getProperty("carga.atividade"),
                environment.getProperty("carga.data.atividade"),
                environment.getProperty("carga.tempo.execucao.minutos"));

        HistoricoAtividade salva = cargaService.carregar(registro);

        LOGGER.info("Carga técnica do histórico concluída: atividade registrada com ID {}.",
                salva.getId());
    }
}
