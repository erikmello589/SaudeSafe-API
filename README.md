# SaudeSafe - API RestFul

## Aplicação com foco em Facilidade, Segurança e Organização de Documentação Médica.

Venha conhecer um pouco mais do Lado de Servidor desse Projeto!

## 📑 Índice

- [Sobre o projeto](#sobre-o-projeto)
- [Funcionalidades Principais](#funcionalidades-principais)
- [Estrutura do Banco de Dados](#estrutura-do-banco-de-dados)
- [Dependências Principais](#dependências-principais)
- [Tecnologias utilizadas](#tecnologias-utilizadas)

## 📁 Sobre o projeto

### O que é? 

Uma API para gerenciamento de documentação médica, desenvolvida em Java com Spring Boot para criar endpoints RESTful. O projeto utiliza Postman para testes e Swagger para documentação interativa, além de Database Client JDBC para consultas e administração do banco de dados.

### 🎯 Objetivo

O objetivo deste projeto é oferecer de forma intuitiva e simplificada uma nova opção para que possamos armazenar com segurança prontuários médicos, sem depender de documentos físicos em papel ou organizações em pastas em um dispositivo centralizado.

## 📌 Funcionalidades Principais

- **Roles**: Criação das roles (Permissões) "ROLE_ADMIN" e/ou "ROLE_USER".
- **Cadastro de Usuários e/ou Médicos**: Criação com dados indispensáveis, visualização de todos ou pelo ID, atualização de informações e exclusão do cadastro.
- **Login**: Acessar uma conta de Usuário já registrada.
- **Gerenciamento do Consultas**: Criação e armazenamento de consultas, atualização, visualização de todas do Usuário ou de seus pacientes e exclusão.
- **Atestados**: Anexação de um atestado médico recebido em um atendimento, atualização, visualização e exclusão.É permitido ao usuário anexar um arquivo (seja ele uma imagem ou um PDF) para salvar o mesmo.
- **Receitas**: Anexação de uma Receita médica e seus medicamentos recebidos em um atendimento, atualização, visualização e exclusão. É permitido ao usuário anexar um arquivo (seja ele uma imagem ou um PDF) para salvar a mesma.
- **Pedido de Exames**: Anexação de um Pedido e os detalhamento dos exames requisitados após uma consulta, atualização, visualização e exclusão. É permitido ao usuário anexar um arquivo (seja ele uma imagem ou um PDF) para salvar o mesmo.
- **Verificação de um profissional de saúde**: Baseado nas credenciais do profissional, o Usuário Administrador tem a possibilidade de checar os dados de um médico no sistema organizador da area para conferir a situação de atividade do mesmo e atualizar na aplicação SaudeSafe.

## 📊 Estrutura do Banco de Dados

O Banco de Dados possui como principais tabelas:

- **Users**: Armazena informações dos usuários, incluindo nome de usuário, e-mail e senha.
- **Roles**: Define os papéis dos usuários no sistema (ADMIN e/ou USER).
- **Pacientes**: Armazena as informações de um paciente.
- **Profissional de Saúde**: Armazena informações de um médico, tais como seu número do conselho regional e seu estado de atuação.
- **Estabelecimento de Saúde**: Armazena as informações de um ambiente de atendimento, como o endereço e o CEP.
- **Consultas**: Guarda informações de uma consulta, que conecta um paciente, um médico e um estabelecimento de saúde.

  ![Modelo Lógico V1 - 5TFC (1)](https://github.com/user-attachments/assets/b8d6555a-16f3-45b9-8b9f-ddab3ffae97c)


## 💪 Dependências Principais

O projeto foi desenvolvido com as seguintes dependências principais:

- **Spring Boot**: Framework para construção do backend.
- **Spring Data JPA**: Para interação com o Banco de Dados.
- **Spring Security**: Para autenticação e autorização de usuários.
- **PostgreSQL**: Banco de dados utilizado no projeto.

## 👩🏻‍💻 Tecnologias utilizadas

- **VsCode**
- **Java**
  - Orientação a Objetos (com classes como Paciente e ProfissionalSaude)
  - Persistência de Dados: Leitura e escrita de arquivos (.txt)
- **Spring Boot**
  - Ferramenta que facilita a criação de APIs e aplicações Java com configuração mínima e suporte para persistência de dados e injeção de dependências.
- **Postman**
  - Plataforma para desenvolvimento e teste de APIs, permitindo simular requisições HTTP e analisar respostas, ideal para testar e documentar endpoints em ambientes de desenvolvimento.
- **Swagger**
  - Ferramenta para documentar e testar APIs, com interface interativa para visualização de endpoints.
- **Database Client JDBC - Extensão VsCode**
  - Ferramenta de gerenciamento de banco de dados, com suporte para diversas bases e uma interface intuitiva para consultas SQL e administração.

## 🎨 Autor

- [Erik Mello Guedes](https://github.com/erikmello589)
