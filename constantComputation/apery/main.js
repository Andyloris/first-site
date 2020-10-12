var Apery = 0;
var n = 1;
var resultPfr;
var resultPen;
function setup() {
    noCanvas();
    resultPfr = createP(" ");
    resultPen = createP(" ")
}
function draw() {
    Apery += (1/Math.pow(n,3));
    n++;
    resultPfr.html("Valeur approximée de la constante d'Apéry: " + Apery);
    resultPen.html("Approximed value of the Apéry contant: " + Apery);
}