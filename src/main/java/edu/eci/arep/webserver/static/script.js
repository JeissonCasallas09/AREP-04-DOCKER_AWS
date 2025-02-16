function loadGetMsg() {
    let nameVar = document.getElementById("name").value;
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function() {
        document.getElementById("getrespmsg").innerHTML = this.responseText;
    }
    xhttp.open("GET", "/app/greeting?name=" + nameVar);
    xhttp.send();
}

function loadPostMsg() {
    let url = "/app/";
    fetch(url, { method: 'GET' })
        .then(x => x.text())
        .then(y => document.getElementById("postrespmsg").innerHTML = y);
}

function loadGetCountChars() {
    let nameVar = document.getElementById("name2").value;
    const xhttp = new XMLHttpRequest();
    xhttp.onload = function() {
        document.getElementById("getcountChars").innerHTML = this.responseText;
    }
    xhttp.open("GET", "/app/countChars?word=" + nameVar);
    xhttp.send();
}

function loadGetConcat(){
    let valor1 = document.getElementById("valor1").value;
    let valor2 = document.getElementById("valor2").value;
    fetch(`/app/concat?a=${valor1}&b=${valor2}`, { method: 'GET' })
        .then(x => x.text())
        .then(y => document.getElementById("getconcat").innerHTML = y);
}


function loadGetMax(){
    let valor1 = document.getElementById("numero1").value;
    let valor2 = document.getElementById("numero2").value;
    let valor3 = document.getElementById("numero3").value;
    fetch(`/app/max?a=${valor1}&b=${valor2}&c=${valor3}`, { method: 'GET' })
        .then(x => x.text())
        .then(y => document.getElementById("getmax").innerHTML = y);
}
