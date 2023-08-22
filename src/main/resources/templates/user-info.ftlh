<!DOCTYPE html>
<html>
<head>
    <title>BUYSELL</title>
</head>
<body>
<h1>BUYSELL</h1><hr>

<img src="/images/${user.getAvatar().getId()}" height="300px" style="border-radius: 200px"/><br><br>
<#if currentUserEmail==user.email>
    <form action="/user/uploadavatar" method="post" enctype="multipart/form-data">
        Фотография: <input type="file" name="file"/><br><br>
        <input type="hidden" name="userId" value="${user.getId()}">
        <input type="submit" value="Добавить аватар"/>
        <input type="hidden" name="_csrf" value="${_csrf.token}">
    </form>
</#if>
<h3>Подробная информация о пользователе ${user.name}</h3>
<b>Номер телефона: </b> ${user.phoneNumber}<br>
<b>Email: </b> ${user.email}<br>
<h4>Товары пользователя ${user.name}</h4>
<#list products as product>
    <div>
        <p><b>${product.title}</b> ${product.price} тг. | <a href="/product/${product.id}">Подробнее...</a></p>
    </div>
<#else>
    <h3>Товаров нет</h3>
</#list>
<#if errorMessage??>
    <h2 style="color: red">${errorMessage}</h2>
</#if>
</body>
</html>