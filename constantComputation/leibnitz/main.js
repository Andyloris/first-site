var pi = 4;
var resultP = document.getElementById("val");
var approxP = document.getElementById("diff");
var piComp = 3.141592653589793238462643383279;
var diff = 0;
var iteration = 0;
var den = 1;
var frameRate = 60;

function main() {
    den = iteration * 2 + 3;
    if(iteration % 2 == 0) {
        pi -= (4 / den);
    } else {
        pi += (4 / den);
    }
    resultP.innerHTML = "Valeur Approximée: " + pi;
    diff = pi - piComp;
    approxP.innerHTML = "Différence: " + diff;
    iteration++;
}
setInterval(() => {
  main();  
}, 1000/frameRate);
