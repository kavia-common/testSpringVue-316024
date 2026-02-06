# testSpringVue
Poc para Stack: PL-SQL (Oracle) + Java (Spring) + JavaScript (VusJs)

---
## /db

Diretório contempla script para criação do banco de dados Oracle. 

Para teste rápido recomendo usar database gratuíto na Oracle Cloud.

How-To de criação de instância free-tier na nuvem e conexão no Dbeaver: 

https://dev.to/pinei/free-oracle-database-how-to-10hb

---
## /server

Diretório contempla projeto Java para BackEnd (APIs).

Este projeto utiliza lombok para código boilerplate, instale a extenção na sua IDE preferida (Eclipse recomendado):

https://projectlombok.org/setup/eclipse

O projeto foi criado com o Spring Initializr

https://start.spring.io/

Este projeto utiliza spring, que por sua vez conecta por SSH por .jks e não .sso/.p12,
configure o ojdbc.properties de sua wallet para utilizar keystore.

https://blogs.oracle.com/dev2dev/ssl-connection-to-oracle-db-using-jdbc,-tlsv12,-jks-or-oracle-wallets

O projeto foi criado com Spring Boot e possui Tomcat embarcado. Além de ser possível utilizar em WebContainer como o Tomcat pode ser utilizado em qualquer servidor J2EE (WebLogic, WebSphire, JBoss e etc..)

Para deploy em servidor procure a documentação do mesmo, para subir como micro-serviço basta JDK1.8+ e executar o .war

```
java -jar poc-0.0.1-SNAPSHOT.war
```

Endereço do swagger:

http://localhost:8080/swagger-ui.html

---
### Para realização do crud

Para montar as APIs foram utilizados vários tutoriais diferentes, utilizando o que era útil em cada um.

A montagem base das APIs para o crud de exemplo foi baseada no seguinte material:

https://www.javaguides.net/2020/01/spring-boot-hibernate-oracle-crud-example.html

Porém utilizando de Lombok como feito neste material mas para MySQL:

https://www.oracle.com/br/technical-resources/articles/dsl/crud-rest-sb2-hibernate.html

Além disso, alguns imports do Java não estavam claros, as duvidas foram tiradas com base neste terceiro tutorial:

https://mkyong.com/spring-boot/spring-boot-spring-data-jpa-oracle-example/

A inclusão não funcionava devido ao erro 'ORA-32575: Explicit column default is not supported for modifying vie hibernate' onde o Oracle não permitia informar o campo ID como 'default' e o Hibernate exige informar o campo para ID como não NULL e com estratégia de geração. Como o campo é controlado pelo banco de dados foi usado um gerador dummy, como retirei deste material:

https://stackoverflow.com/questions/3194721/bypass-generatedvalue-in-hibernate-merge-data-not-in-db

A alteração não retornava os campos calculados da view devido ao cache do hibernate, como limpar ele foi retirado deste material:

https://stackoverflow.com/questions/25063995/spring-boot-handle-to-hibernate-sessionfactory

Para ter uma interface grafica das APIs foi incluido um swagger, o how-to foi tirado deste material:

https://www.treinaweb.com.br/blog/documentando-uma-api-spring-boot-com-o-swagger/

Durante a implementação do FrontEnd o acesso foi travado por politica de CORS, liberado com o seguinte material:

https://stackoverflow.com/questions/35091524/spring-cors-no-access-control-allow-origin-header-is-present

Para compilar o projeto basta (Eclipse, Run As)
Maven: clean, install

---
## /client

Esta aplicação foi criada baseada no material abaixo, ignorando a parte do Python/Flask que foi realizada em Java/Spring, ou seja, aproveitando apenas o FrontEnd e alterando para ser compatível com as tabelas:

https://testdriven.io/blog/developing-a-single-page-app-with-flask-and-vuejs/

Para executar a aplicação com npm (gerenciador de pacotes do NodeJs) execute:

 ```sh
cd client
npm install
npm run serve -- --port 8081
```

Endereço da aplicação:

http://localhost:8081