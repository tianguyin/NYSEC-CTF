// 定义一个变量 score，并初始化为 0
var score = 200;

// 页面加载完成后执行的函数
window.onload = function() {
    var nameDiplay= document.getElementById("nameDiplay");
    // 获取 id 为 scoreDisplay 的元素
    var scoreDisplay = document.getElementById("scoreDisplay");
    nameDiplay.textContent = localStorage.getItem("username")
    // 更新 scoreDisplay 元素的内容为当前 score 的值
    scoreDisplay.textContent = score;
    var cookies = document.cookie;
    if (cookies.startsWith("token=")){
        var token = cookies.substring("token=".length,cookies.length);
    }
    var getApi = {
        token:token
    }
    fetch('/data/common/scoredata',{
        method:'POST',
        headers:{
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(getApi)
    })
};
