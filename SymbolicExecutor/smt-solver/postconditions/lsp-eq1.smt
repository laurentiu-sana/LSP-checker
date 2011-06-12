(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (x Int) )
  :formula
    (not
     (and

      (implies
       (and (< x 0) (< x 100))
       (= x (* 3 x))
      )

      (implies
       (and (>= x 0) (< x 100))
       (= (* 2 x) (* 3 x))
      )

      (implies
       (and (>= x 0) (>= x 100))
       (= (* 2 x) (* 4 x))
      )

     )
    )
