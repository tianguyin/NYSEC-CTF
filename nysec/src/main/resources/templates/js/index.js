// scripts.js

function loadPage(page) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            document.getElementById("main-content").innerHTML = this.responseText;
            scrollToMainContent();
        }
    };
    xhttp.open("GET", page, true);
    xhttp.send();
}
