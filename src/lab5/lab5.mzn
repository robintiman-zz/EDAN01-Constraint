include "globals.mzn";

int: n = 5;
var int: n_commercial = 13;
var int: n_residential = 12;
array[1..6] of var int: point_distribution = [-5, -4, -3, 3, 4, 5];

set of int: GridRange = 1..n;
set of int: HouseType = 1..2;

var int: score; % Score of the solution
array[GridRange,GridRange] of var HouseType: houseType;

% Constrain number of residentials
var int: count_residential = sum(i, j in GridRange where houseType[i,j] == 1)(1);
constraint count_residential = n_residential;

% Constrain number of commercial
var int: count_commercial = sum(i, j in GridRange where houseType[i,j] == 2)(1);
constraint count_commercial = n_commercial;

% Row score
array[GridRange] of var int: rowScore;
constraint forall (i in GridRange) (
    if sum(j in GridRange where houseType[i,j] == 1) (1) >= 3
    then rowScore[i] = sum(j in GridRange where houseType[i,j] == 1) (1)
    else rowScore[i] = sum(j in GridRange where houseType[i,j] == 2) (-1)
    endif
);

% Column score
array[GridRange] of var int: colScore;
constraint forall (i in GridRange) (
    if sum(j in GridRange where houseType[j, i] == 1) (1) >= 3	
    then colScore[i] = sum(j in GridRange where houseType[j, i] == 1) (1)
    else colScore[i] = sum(j in GridRange where houseType[j, i] == 2) (-1)
    endif
);

% Total score
var int: temp1 = sum(i in rowScore)(rowScore[i]);
var int: temp2 = sum(i in colScore) (colScore[i]);
score = temp1 + temp2;

% Print solution
output[show(houseType), "\n", show(score), "\n"];

solve maximize score;