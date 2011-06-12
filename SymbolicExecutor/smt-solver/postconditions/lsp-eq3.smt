(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (x Int) )
  :formula
    (not 
     (and

      (implies
       (and (< x 1) (true))
       (= x x)
      )

      (implies
       (and (>= x 1) (true))
       (= (* 2 x) x)
      )

     )
    )
