<!DOCTYPE html>
<html>
<head>
    <title>BUYSELL</title>
</head>
<body>
<h1>BUYSELL</h1><hr>
<h4>Подробная информация о товаре</h4>

<#list images as img>
    <img src="/images/${img.getId()}" height="300px"/><br><br>
</#list>

<b>Название товара: </b>${product.title}<br>
<b>Описание товара: </b>${product.description}<br>
<b>Цена: </b>${product.price}<br>
<b>Город: </b>${product.city}<br>
<b>Автор: </b><a href="/user/${product.user.id}">${product.user.name}</a><br>
<b>Номер телефона: </b>${product.user.phoneNumber}<br>

<hr>
<form action="/product/delete/${product.id}" method="post">
    <#if currentUserEmail == product.user.email || currentUserHasRole?seq_contains("ROLE_ADMIN")>
        <button type="submit">Удалить товар</button>
    </#if>
    <input type="hidden" name="_csrf" value="${_csrf.token}">
</form>
</body>
</html>