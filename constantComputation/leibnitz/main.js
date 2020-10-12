var pi = 4;
var resultP = document.getElementById("val");
var approxP = document.getElementById("diff");
var piComp = 3.1415926535897932;
var diff = 0;
var iteration = 0;
var den = 1;
var frameRate = 60;
//resultP = createDiv("Valeur Approximée: ");
//approxP = createDiv("Différence: ");
function main() {
    den = iteration * 2 + 3;
    if(iteration % 2 == 0) {
        pi -= (4 / den);
    } else {
        pi += (4 / den);
    }
    resultP.innerHTML = "Valeur Aproximée: " + pi;
    diff = pi - piComp;
    approxP.innerHTML = "Différence: " + diff;
    iteration++;
}
setInterval(() => {
  main();  
}, 1000/frameRate);