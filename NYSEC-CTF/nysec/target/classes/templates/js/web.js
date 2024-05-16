
function loginOut() {
    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
    sessionStorage.clear();
    localStorage.clear();
    location.reload();
}
function showQuestionWindow(id,web){
    // 创建遮罩层
    var overlay = document.createElement("div");
    overlay.id = "overlay";
    document.body.appendChild(overlay);

// 创建容器
    var container = document.createElement("div");
    container.id = "container";

// 创建弹窗内容
    var popupContent = document.createElement("div");
    popupContent.classList.add("popup-content");
    popupContent.innerHTML = `
    <p>Web${id}</p>
    <a id="closeButton">关闭</a>
`;
    container.appendChild(popupContent);

// 添加关闭按钮点击事件处理程序
    var closeButton = popupContent.querySelector("#closeButton");
    closeButton.addEventListener("click", function() {
        // 隐藏弹窗
        document.body.removeChild(overlay);
        document.body.removeChild(container);
    });

// 将容器添加到文档中
    document.body.appendChild(container);

}
function logout() {
    if (document.cookie.length<1) {
        localStorage.clear();
    }
}
window.onload = function() {
    logout();
    // 从 localStorage 中获取用户名，并更新页面上的用户名显示
    var cookies = document.cookie;
    var check = document.getElementById("mylogin");
    var email = localStorage.getItem("email");
    var questionList = document.getElementById("questionList");
    if (cookies.length>0 && cookies.startsWith("token=")){
        check.textContent = "退出登录";
        check.id = "loginout";
        check.href = "#"
        check.onclick = loginOut;
        var requestBody = {
            token: cookies,
            email: email
        };

        // 发送 POST 请求到服务器端获取用户分数数据
        fetch('/web/api', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                data.forEach((question) => {
                    if(question.web === "0"){
                        var listItem = document.createElement("p");
                        listItem.classList.add("web-challenge-d");
                        listItem.innerHTML = `web${question.id}<br>未完成`;
                        listItem.onclick =function (){showQuestionWindow(question.id,question.web)};
                        questionList.appendChild(listItem);
                    }
                    else if (question.web === "1"){
                        var listItem = document.createElement("p");
                        listItem.classList.add("web-challenge-f");
                        listItem.innerHTML = `web${question.id}<br>已完成`;
                        listItem.onclick = function (){showQuestionWindow(question.id,question.web)};
                        questionList.appendChild(listItem);
                    }
                })
            })
            .catch(error => {
                // 处理错误
                console.log('There was a problem with the fetch operation:', error);
            });

    }
}

function loginOut() {
    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
    sessionStorage.clear();
    localStorage.clear();
    location.reload();
}
function showQuestionWindow(id,web){
    // 创建遮罩层
    var overlay = document.createElement("div");
    overlay.id = "overlay";
    document.body.appendChild(overlay);

// 创建容器
    var container = document.createElement("div");
    container.id = "container";

// 创建弹窗内容
    var popupContent = document.createElement("div");
    popupContent.classList.add("popup-content");
    popupContent.innerHTML = `
    <p>Web${id}</p>
    <a id="closeButton">关闭</a>
`;
    container.appendChild(popupContent);

// 添加关闭按钮点击事件处理程序
    var closeButton = popupContent.querySelector("#closeButton");
    closeButton.addEventListener("click", function() {
        // 隐藏弹窗
        document.body.removeChild(overlay);
        document.body.removeChild(container);
    });

// 将容器添加到文档中
    document.body.appendChild(container);

}
window.onload = function() {

    // 从 localStorage 中获取用户名，并更新页面上的用户名显示
    var cookies = document.cookie;
    var check = document.getElementById("mylogin");
    var email = localStorage.getItem("email");
    var questionList = document.getElementById("questionList");
    if (cookies.length>0 && cookies.startsWith("token=")){
        check.textContent = "退出登录";
        check.id = "loginout";
        check.href = "#"
        check.onclick = loginOut;
        var requestBody = {
            token: cookies,
            email: email
        };

        // 发送 POST 请求到服务器端获取用户分数数据
        fetch('/web/api', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(requestBody)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                data.forEach((question) => {
                    if(question.web === "0"){
                        var listItem = document.createElement("p");
                        listItem.classList.add("web-challenge-d");
                        listItem.innerHTML = `web${question.id}<br>未完成`;
                        listItem.onclick =function (){showQuestionWindow(question.id,question.web)};
                        questionList.appendChild(listItem);
                    }
                    else if (question.web === "1"){
                        var listItem = document.createElement("p");
                        listItem.classList.add("web-challenge-f");
                        listItem.innerHTML = `web${question.id}<br>已完成`;
                        listItem.onclick = function (){showQuestionWindow(question.id,question.web)};
                        questionList.appendChild(listItem);
                    }
                })
            })
            .catch(error => {
                // 处理错误
                console.log('There was a problem with the fetch operation:', error);
            });

    }
}

