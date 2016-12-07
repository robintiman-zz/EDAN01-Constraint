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
      var 0..n: residential
    } in
    count([houseType[i,j]|j in 1..n], 1, residential)
    /\
    rowScore[i] = point_distribution[residential]
);


constraint lex2(houseType);
% Column score
array[GridRange] of var -5..5: colScore;
constraint forall (i in GridRange) (
    let {
      var 0..n: residential
    } in
    count([houseType[j,i]|j in 1..n], 1, residential)
    /\
    colScore[i] = point_distribution[residential]
);


% Total score
var 0..50: score = sum(rowScore) + sum(colScore);

% Print solution
output [
  "score: " ++ show(score) ++ "\n" ++
  "rowScore: " ++ show(rowScore) ++ "\n" ++
  "colScore: " ++ show(colScore) ++ "\n" ++ 
  "houseTypes:"
] ++
[
  if j = 1 then "\n" else " " endif ++
    show(houseType[i,j])
  | i,j in 1..n
] ++
["\n"]++
[
  if j = 1 then "\n" else " " endif ++
    if fix(houseType[i,j]) == 1 then 
     "R"
    else 
     "C"
    endif
  | i,j in 1..n
];

solve :: int_search(
   [houseType[i,j] | i,j in 1..n] ++
   rowScore ++ colScore, % ++ [x[i,j] | i,j in 1..n], 
   first_fail,
   indomain_min,
   complete)
  % satisfy;
maximize score;