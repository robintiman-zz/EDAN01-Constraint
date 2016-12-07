include "globals.mzn";

% Example 1 (cost 14)
% int: n = 5;
% var int: n_commercial = 13;
% var int: n_residential = 12;
% array[0..5] of int: point_distribution =
%    array1d(0..5, [
%       -5, -4,-3, 3, 4, 5,
%     ]);

% Example 2 (cost 36)
% int: n = 5;
% var int: n_commercial = 7;
% var int: n_residential = 18;
% array[0..5] of int: point_distribution =
%    array1d(0..5, [
%       -5, -4,-3, 3, 4, 5,
%     ]);

% Example 3 (cost 58)
int: n = 7;
var int: n_commercial = 20;
var int: n_residential = 29;
array[0..7] of int: point_distribution =
   array1d(0..7, [-7, -6, -5, -4, 4, 5, 6, 7]);

array[1..n,1..n] of var 1..2: houseType;
array[1..n] of var -n..n: rowScores;
array[1..n] of var -n..n: colScores;

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
var 0..100: score = sum(rowScores) + sum(colScores);

%  Constrain number of residentials to n_residential
constraint count([houseType[i,j]|i,j in 1..n], 1, n_residential);

% Magic constraint
constraint lex2(houseType);


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