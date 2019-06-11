# Intelliport - EP2 OO UnB-FGA

Intelliport é o mais novo app que te ajuda a decidir dentre várias opções de transporte de cargas qual é a que mais se encaixa nos seus objetivos.

Além disso, Intelliport mantém de forma organizada toda frota, motoristas e entregas, tudo isso na núvem, para o seu conforto.

Intelliport - Transporting Management Solutions

## Instruções

Instale o APK disponível na pasta root do repositório em seu aparelho android. Nota: você precisa de conexão com a internet para poder usar, e o app pede permissões para acesso na hora da instalação.

Crie sua conta:

<p align="center"> 
<img src="https://i.imgur.com/Iz342nH.jpg">
</p>

(sua senha é criptografada pelo google mas mesmo assim não bota qualquer uma não :D)

Adicione motoristas:

<p align="center"> 
<img src="https://i.imgur.com/sU4KLVV.jpg">
</p>

Adicione veiculos:

<p align="center"> 
<img src="https://i.imgur.com/M6hDFD7.jpg">
</p>

Preencha detalhes das suas entregas:

<p align="center"> 
<img src="https://i.imgur.com/FYV67iD.jpg">
</p>

Veja suas opções de entregas (você pode escolhe-las manualmente ou deixar que o app faça uma curagem das melhores opções):

<p align="center"> 
<img src="https://i.imgur.com/OwePbZy.jpg">
</p>

O menu principal mantém tudo organizado para você se guiar:

<p align="center"> 
<img src="https://i.imgur.com/T4jnBDC.jpg">
</p>

Todos os direitos reservados ao Zé do Caminhão™.

## Detalhes

Todos os dados persistentes do app são guardados usando a base de dados [FireBase](https://firebase.google.com/) do [Google](https://www.google.com), bem como contas de usuário. As contas são registradas usando o Firebase Auth, sendo assim não se tem acesso ás senhas pois são criptografadas. Como as contas e dados são totalmente remotas, qualquer smartphone pode acessar a mesma conta.

Diagrama UML de casos de uso, demonstrando a interação entre usuário, sistema e a base de dados remota.

<p align="center"> 
<img src="https://i.imgur.com/PoJGzg9.jpg">
</p>

Diagrama UML de classes, demonstrando a interação entre as classes relevantes que compõe o app (e a classe delivery log, uma singleton).

<p align="center"> 
<img src="https://i.imgur.com/slXnccH.png">
</p>
