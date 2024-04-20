window.onload = function () {
    var cookies = document.cookie;
    if (cookies.startsWith("token=")){
        var storeTOKEN = cookies.substring("token=".length,cookies.length)
        var token = storeTOKEN.split(',');
        const Info = [
            localStorage.getItem("username"),
            localStorage.getItem("email"),
            localStorage.getItem("introduction")
        ]
        for (let i = 0; i < Info.length; i++) {
            alert(Info[i]);
        }
    }
}