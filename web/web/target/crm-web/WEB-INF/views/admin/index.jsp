<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="shiro" uri="http://shiro.pandawork.net/tags" %>
<html>
<head>
    <title>欢迎界面</title>
    <link rel="stylesheet" href="/css/welcome/welcome.css" />
    <jsp:include flush="true" page="/WEB-INF/views/admin/common/head.jsp"></jsp:include>
</head>
<body>
    <jsp:include flush="true" page="/WEB-INF/views/admin/common/header.jsp"></jsp:include>
<div class="page clearfix ">
    <div class="holder bg">
        <div class="col-sm-12">
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-home"></i>&nbsp;首页</a></li>
            </ol>
        </div>
        <div class="col-sm-12 margin-top--10 ">
            <h1 class="bg-box">
                <p class="small-font">欢迎进入</p>
                <p class="big-font">正大国际医院CRM</p>
            </h1>
        </div>
    </div>
</div>
    <jsp:include flush="true" page="/WEB-INF/views/admin/common/footer.jsp"></jsp:include>
<script type="text/javascript" src="/js/login/welcome.js"></script>
</body>

</html>