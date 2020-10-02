var button = document.querySelector(".dark-mode-btn");
var lightOrDark = document.querySelector("span");
var body = document.querySelector("body");
var ho = document.querySelector("h1");
var captiondblclick = 1;
button.style.textAlign = "center";
button.onclick = () => {
    if(captiondblclick == 2) {
        lightOrDark.className = "dark-mode";
        body.style.color = "#f9f9f9";
        ho.style.color = "#f9f9f9";
        body.style.backgroundColor = "#000";
        captiondblclick = 1;
        button.innerHTML = "Mode blanc";
    } else if(captiondblclick == 1) {
        lightOrDark.className = "light-mode";
        body.style.color = "#000";
        ho.style.color = "#000";
        body.style.backgroundColor = "#f9f9f9";
        captiondblclick = 2;
        button.innerHTML = "Mode noir";
    }
}    
