function loginOut() {
    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
    sessionStorage.clear();
    localStorage.clear();
    location.reload();
}
window.onload = function() {

    var nameDisplay = document.getElementById("nameDiplay");
    var scoreList = document.getElementById("Person-List");

    // 从 localStorage 中获取用户名，并更新页面上的用户名显示
    var username = localStorage.getItem("username");
    nameDisplay.textContent = username;
    var cookies = document.cookie;
    var check = document.getElementById("mylogin");
    if (cookies.length>0 && cookies.startsWith("token=")){
        check.textContent = "退出登录";
        check.id = "loginout";
        check.href = "#"
        check.onclick = loginOut;
        var requestBody = {
            token: cookies
        };

        // 发送 POST 请求到服务器端获取用户分数数据
        fetch('/data/common/scoredata/api', {
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
                // 处理从服务器接收到的 JSON 数据
                data.reverse();

                // 将用户分数数据显示在页面上
                data.forEach(user => {
                    var listItem = document.createElement('p');
                    listItem.classList.add('list');
                    listItem.textContent = `${user.username}: ${user.score}`;
                    scoreList.appendChild(listItem);
                });
            })
            .catch(error => {
                // 处理错误
                console.error('There was a problem with the fetch operation:', error);
            });
    }
}
