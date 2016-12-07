include "globals.mzn";

int: n = 5;
var int: n_commercial = 13;
var int: n_residential = 12;
array[0..5] of int: point_distribution =
   array1d(0..5, [
      -5, -4,-3, 3, 4, 5, % for residential
    ]);

array[1..n,1..n] of var 1..2: houseType;
array[1..n] of var -5..5: rowScores;
array[1..n] of var -5..5: colScores;

%  Count number of residentials in each row
constraint forall (i in 1..n) (
  let {
      var 0..n: residential
    } in
    count([houseType[i,j]|j in 1..n], 1, residential)
    /\
    %    Set score to score[#residentials]
    rowScores[i] = point_distribution[residential]
); 

%  Count number of residentials in each column
constraint forall(i in 1..n) (
  let {
    var 0..n: residential
  } in 
  count([houseType[j,i]|j in 1..n], 1, residential)
  /\
  %    Set score to score[#residentials] 
  colScores[i] = point_distribution[residential]
);

%  Set total score to rowScore + colScore
var 0..50: score = sum(rowScores) + sum(colScores);

%  Constrain number of residentials to n_residential
constraint count([houseType[i,j]|i,j in 1..n], 1, n_residential);

% Print solution
output [
  "score: " ++ show(score) ++ "\n" ++
  "rowScores: " ++ show(rowScores) ++ "\n" ++
  "colScores: " ++ show(colScores) ++ "\n" ++ 
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

% Solve
solve :: int_search(
   [houseType[i,j] | i,j in 1..n] ++
   rowScores ++ colScores, % ++ [x[i,j] | i,j in 1..n], 
   first_fail,
   indomain_min,
   complete)
  % satisfy;
maximize score;