package br.projeto.repository_factory;

import br.projeto.db.DB;
import br.projeto.repository.PerfilProjetoIntermediariaRepository;

public class PerfilProjetoIntermediariaRepositoryFactory extends RepositoryFactory {

    @Override
    public <T> T createRepository() {
        return (T) new PerfilProjetoIntermediariaRepository(DB.getConnection());
    }
}
