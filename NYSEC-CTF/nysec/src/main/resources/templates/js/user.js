function loginOut() {
    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
    sessionStorage.clear();
    localStorage.clear();
    location.reload();
}
function logout() {
    if (document.cookie.length<1) {
        localStorage.clear();
    }
}

window.onload = function () {
    logout();
    var cookies = document.cookie;
    var check = document.getElementById("mylogin");
    if (cookies.length>0 && cookies.startsWith("token=")){
        check.textContent = "退出登录";
        check.id = "loginout";
        check.href = "#"
        check.onclick = loginOut;

    }
}