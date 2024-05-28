<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
</head>
<!------ Include the above in your HEAD tag ---------->
<link href="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css" rel="stylesheet" id="bootstrap-css">
<script src="//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<style>
    body {
        margin: 0;
        padding: 0;
        background-color: #FFF;
        height: 100vh;
    }
    #login .container #login-row #login-column #login-box {
        margin-top: 120px;
        max-width: 600px;
        height: 320px;
        border: 1px solid #9C9C9C;
        background-color: #EAEAEA;
    }
    #login .container #login-row #login-column #login-box #login-form {
        padding: 20px;
    }
    #login .container #login-row #login-column #login-box #login-form #register-link {
        margin-top: -85px;
    }
    .btn.btn-info.btn-md {
        margin-top:16px;
        background-color:#009ddc;
        width: 100%;
        height: 48px;
    }
    .text-info {
        color:#000000 !important;
    }
    #login-box {
        background: none !important;
        border: 0px !important;
    }
    .brand-logo {
        width: 100%;
        height: 50px;
        background: url(/resources/css/img/brand-logo.png) no-repeat 50% 0;
    }

</style>

<body>
<div id="login">
    <h3 class="text-center text-white pt-5">Login form</h3>
    <div class="container">
        <div id="login-row" class="row justify-content-center align-items-center">
            <div id="login-column" class="col-md-6">
                <div id="login-box" class="col-md-12">
                    <form id="login-form" class="form" action="/cwlogin" method="post">
                        <div class="brand-logo"></div>
                        <div class="form-group">
                            <label for="username" class="text-info">사번</label><br>
                            <input type="text" name="username" id="username" class="form-control">
                        </div>
                        <div class="form-group">
                            <label for="password" class="text-info">비밀번호</label><br>
                            <input type="password" name="password" id="password" class="form-control" style="ime-mode:inactive;">
                        </div>
                        <div class="form-group">
                            <input type="submit" name="submit" class="btn btn-info btn-md" value="로그인">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>

</html>