function previewAvatar(event) {
    var input = event.target;
    var reader = new FileReader();
    reader.onload = function () {
        var dataURL = reader.result;
        var avatarImg = document.createElement('img');
        avatarImg.src = dataURL;
        var avatarContainer = document.getElementById('avatar-container');
        avatarContainer.innerHTML = '';
        avatarContainer.appendChild(avatarImg);
    };
    reader.readAsDataURL(input.files[0]);
}

function handleAvatarClick(event) {
    event.stopPropagation(); // 阻止事件冒泡到 input 元素
}
function submitFormData() {
    // 获取表单数据
    var username = document.getElementById('username').value;
    var email = document.getElementById('email').value;
    var introduction = document.getElementById('introduction').value;
    var password = document.getElementById('password').value;
    var jsonData = {
        username: username,
        email: email,
        introduction: introduction,
        password: password
    };
    var emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    // 发送 POST 请求
    if (emailRegex.test(email)) {
            fetch('/register/api', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(jsonData)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('POST request succeeded with JSON response:', data);
                })
                .catch(error => {
                    console.error('There was a problem with the POST request:', error);
                });
    }else {
        alert("邮箱地址错误");
    }
}
function limit() {
    var maxLength = 100; // 最大输入长度
    var textArea = document.getElementById('introduction');
    var remainingCharsSpan = document.getElementById('remainingChars');
    var remainingChars = maxLength - textArea.value.length;

    // 如果剩余字符数小于0，则截断输入的文本
    if (remainingChars < 0) {
        textArea.value = textArea.value.slice(0, maxLength);
        remainingChars = 0;
    }

    // 更新剩余字符数的显示
    remainingCharsSpan.textContent = remainingChars;
}

function login(message){
    var email = document.getElementById('email').value;
    var password = document.getElementById('password').value;
    var confirem = {
        email: email,
        password: password
    }
    fetch('/login/api', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(confirem)
    })
        .then(response => response.json())
        .then(data =>{
            const token = data.token;
            const username = data.username;
            const email = data.email;
            const introduction = data.introduction;

// 将数据存储到本地存储
            localStorage.setItem("token", token);
            localStorage.setItem("username", username);
            localStorage.setItem("email", email);
            localStorage.setItem("introduction", introduction);

// 将 token 存储到 cookie 中
            document.cookie = "token=" + token;

        })
        .catch(error => console.error('Error',error))

}
