var Apery = 0;
var n = 1;
var resultPfr;
function setup() {
    noCanvas();
    resultPfr = createP(" ");
}
function draw() {
    Apery += (1/Math.pow(n,3));
    n++;
    resultPfr.html("Valeur approximée de la constante d'Apéry: " + Apery);
}
