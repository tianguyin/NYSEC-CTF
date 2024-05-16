function pop(choice){
    var po = document.getElementById("pop");
    switch (choice){
        case 'avatar':
            avatarPop();
            po.classList.toggle('active');
            break;
        case 'introductionContent':
            introductionContentPop();
            break;
        default:
            return false;
    }
}
function introductionContentPop(){
    alert('11');
}
function avatarPop(){
    var popDiv = document.getElementById('pop');
    // 清空 popDiv 容器内容
    popDiv.innerHTML = '<span class="close-button" onclick="reactive()">×</span>';
    var fileInput = document.createElement('input');
    fileInput.setAttribute('id','avatarFile');
    fileInput.setAttribute('type', 'file');
    fileInput.setAttribute('name', 'uploadedFile');
    fileInput.setAttribute('accept', '.jpg, .jpeg, .png');
    fileInput.style.display = 'none'; // 隐藏原生文件选择框

    var uploadIcon = document.createElement('span');
    uploadIcon.setAttribute('id', 'uploadIcon');
    uploadIcon.setAttribute('class', 'upload-icon');
    uploadIcon.innerHTML = '&#8686;'; // 上传图标

    var uploadText = document.createElement('span');
    uploadText.setAttribute('id', 'uploadText');
    uploadText.setAttribute('class', 'upload-text');
    uploadText.textContent = '点击此处选择文件';
    var submitButton = document.createElement('button');
    submitButton.setAttribute('id', 'submitButton');
    submitButton.setAttribute('class', 'submitButton');
    submitButton.setAttribute('onclick', 'uploadAvatar()');
    submitButton.textContent = '上传文件';

    var uploadDiv = document.createElement('div');
    uploadDiv.setAttribute('class', 'uploadDiv');
    uploadDiv.appendChild(fileInput);
    uploadDiv.appendChild(uploadIcon);
    uploadDiv.appendChild(document.createElement('br'));
    uploadDiv.appendChild(uploadText);
    uploadDiv.appendChild(document.createElement('br'));

    popDiv.appendChild(uploadDiv);
    popDiv.appendChild(submitButton);
    // 点击表单区域触发文件选择框
    uploadDiv.addEventListener('click', function() {
        fileInput.click();
    });

    // 文件选择后更新文本内容
    fileInput.addEventListener('change', function() {
        if (fileInput.files.length > 0) {
            uploadText.textContent = fileInput.files[0].name;
        } else {
            uploadText.textContent = '点击此处选择文件或拖放文件到此';
        }
    });
}
function reactive(){
    var po = document.getElementById("pop");
    po.classList.remove('active');
}
function toggleNav() {
    var nav = document.getElementById('nav');
    nav.classList.toggle('active'); // 切换active类
}

function logout() {
    if (document.cookie.length<1) {
        localStorage.clear();
    }
}
function uploadAvatar(){
    alert('1111');
}
function Info(){
    var username = localStorage.getItem('username');
    var email = localStorage.getItem('email');
    var introduction = localStorage.getItem('introduction');
    var name = document.getElementById('name');
    var introductionContent = document.getElementById('introductionContent');
    var title = document.getElementById('title');
    var avatar = document.getElementById('avatar');
    title.textContent = `${username}的主页`;
    name.textContent = username;
    introductionContent.textContent = introduction;
    avatar.src = "/" + email + "/avatar";
}
function loginOut() {
    document.cookie = "token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;"
    sessionStorage.clear();
    localStorage.clear();
    location.reload();
}

function personload(){
    var cookies = document.cookie;
    var check = document.getElementById("mylogin");
    if (cookies.length>0 && cookies.startsWith("token=")){
        check.textContent = "退出登录";
        check.id = "loginout";
        check.href = "#"
        check.onclick = loginOut;

    }
}
window.onload = function (){
    logout();
    Info();
    personload();
};