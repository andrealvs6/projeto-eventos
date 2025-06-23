Sistema de Gestão de Eventos
Descrição Geral
Este é um sistema de desktop completo para a gestão de eventos, desenvolvido em Java com uma interface gráfica Swing e persistência de dados através de um banco de dados SQLite. A aplicação permite que dois tipos de utilizadores interajam com o sistema: Organizadores e Participantes.

O projeto foi construído seguindo boas práticas de engenharia de software, com uma arquitetura modular (Model-View-Service-DAO) e utiliza o Maven para a gestão de dependências e construção do projeto.

Funcionalidades Principais
Perfil: Organizador
Gestão de Eventos:

Criar novos eventos com nome, descrição, data, local e capacidade.

Editar eventos existentes para atualizar qualquer informação.

Vincular e desvincular múltiplos palestrantes durante a criação ou edição de um evento.

Cancelar eventos.

Visualizar a lista de todos os eventos (passados e futuros).

Visualizar a lista de todos os participantes inscritos num evento específico e remover inscrições.

Gestão de Palestrantes:

Cadastrar novos palestrantes com nome, currículo e área de atuação.

Excluir palestrantes.

Perfil: Participante
Descoberta e Inscrição:

Realizar um primeiro registo rápido com nome e e-mail.

Fazer login usando o e-mail.

Visualizar uma lista de todos os eventos futuros disponíveis.

Inscrever-se em eventos que possuam vagas.

Gestão Pessoal:

Visualizar uma lista separada apenas com os eventos em que está inscrito.

Cancelar a sua própria inscrição num evento.

Emitir um certificado de participação (após a data do evento).

Tecnologias Utilizadas
Linguagem: Java (versão 17 ou superior)

Interface Gráfica: Swing

Banco de Dados: SQLite (para persistência de dados local num único ficheiro)

Gestão de Projeto e Dependências: Maven

Testes: JUnit 5
