include "globals.mzn";

int: n = 5;
var int: n_commercial = 13;
var int: n_residential = 12;
array[1..6] of var int: point_distribution = [-5, -4, -3, 3, 4, 5];

set of int: GridRange = 1..n;
set of int: HouseType = 1..2;

array[GridRange,GridRange] of var HouseType: houseType;

% Constrain number of residentials
var int: count_residential = sum(i, j in GridRange where houseType[i,j] == 1)(1);
constraint count_residential = n_residential;

% Constrain number of commercial
var int: count_commercial = sum(i, j in GridRange where houseType[i,j] == 2)(1);
constraint count_commercial = n_commercial;

% Row score
array[GridRange] of var -5..5: rowScore;
constraint forall (i in GridRange) (
    let {
      var 0..n: count
    } in 
    count = sum(j in GridRange where houseType[i,j] == 1) (1)
    /\
    rowScore[i] = point_distribution[count]
);

% Column score
array[GridRange] of var -5..5: colScore;
constraint forall (i in GridRange) (
    let {
      var 0..n: count
    } in 
    count = sum(j in GridRange where houseType[j,i] == 1) (1)
    /\
    colScore[i] = point_distribution[count]
);


% Total score
var 0..50: score = sum(rowScore) + sum(colScore);

% Print solution
output[show(houseType), "\n", show(score), "\n"];

solve :: int_search(
   [houseType[i,j] | i,j in 1..n] ++
   rowScore ++ colScore, % ++ [x[i,j] | i,j in 1..n], 
   first_fail, 
   indomain_min, 
   complete) 
  % satisfy;
maximize score;