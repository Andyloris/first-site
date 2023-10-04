var p = 0;
var c = 1;
var slider = document.getElementById("myRange");
var fibo = 1;
var textTerm = document.getElementById("term_advanced");
var term = slider.value;
let resultP;
var printedResult = " ";
var canvas = document.querySelector("main");
var button = document.querySelector(".dark-mode-btn");
var body = document.querySelector("body");
var ho = document.querySelector("h1");
var captiondblclick = 1;
function setup() {
    createCanvas(0, 0);
    for (var i = 0; i <= term; i++) {
        console.log(fibo);
        fibo = c + p;
        p = c;
        c = fibo;
    }
    printedResult = str(fibo);
    canvas.remove();
    resultP = createP('value');
}
function draw() {
    term = slider.value;
    slider.oninput = function () {
        p = 0;
        c = 1;
        fibo = 1;
        i = 0;
        for (var i = 0; i <= term; i++) {
            fibo = c + p;
            p = c;
            c = fibo;
        }
        printedResult = str(fibo);
    }
    textTerm.oninput = function () {
        if (parseInt(textTerm.value) > slider.value || parseInt(textTerm.value) < slider.value) {
            term = parseInt(textTerm.value);
            p = 0;
            c = 1;
            fibo = 1;
            i = 0;
            resultP.html(`therme n°${term} de la suite de fibonnacci: ` + printedResult);
            for (var i = 0; i <= term; i++) {
                fibo = c + p;
                p = c;
                c = fibo;
            }
            printedResult = str(fibo);
        }
        term = parseInt(textTerm.value);
    }
    button.style.textAlign = "center";
    button.onclick = () => {
        if (captiondblclick == 2) {
            body.style.color = "#f9f9f9";
            ho.style.color = "#f9f9f9";
            body.style.backgroundColor = "#000";
            captiondblclick = 1;
            button.innerHTML = "Mode blanc";
        } else if (captiondblclick == 1) {
            body.style.color = "#000";
            ho.style.color = "#000";
            body.style.backgroundColor = "#f9f9f9";
            captiondblclick = 2;
            button.innerHTML = "Mode noir";
        }
    }

    resultP.html(`Terme n°${term} de la suite de fibonnacci: ` + printedResult);
    //console.log(printedResult + " " + fibo);
}
